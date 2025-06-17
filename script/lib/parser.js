/** @typedef {import('./types').ErrorEntry} ErrorEntry */
/** @typedef {import('./types').Task} Task */
/** @typedef {import('./types').InlineOp} InlineOp */
/** @typedef {import('./types').Workflow} Workflow */
/** @typedef {import('./types').TargetBlock} TargetBlock */
/** @typedef {import('./types').TaskCollection} TaskCollection */
/** @typedef {import('./types').ParsedManifest} ParsedManifest */
/** @typedef {import('./types').ParsedTextTemplate} ParsedTextTemplate */

/**
 * Find comment pair markers in a Java source text.
 * @param {string} text - File content to parse for comment markers.
 * @returns {Promise<TargetBlock[]>} Array of discovered target blocks (without file property).
 */
export async function find_comment_pairs(text) {
    const result = [], err = [];
    const isValid = (lines, s, e, tags) => !tags.includes('default') || lines.slice(s, e - 1).every(l => { const t = l.trim(); return t === '' || t.startsWith('//'); });
    const lines = text.split(/\r?\n/);
    const markers = [];
    lines.forEach((l, i) => {
        const t = l.trim();
        if (t.startsWith('//#') && t.length > 4) {
            const typ = t[3];
            if ('+-<>'.includes(typ)) {
                const parts = t.slice(4).trim().split(/\s+/);
                const id = parts[0];
                if (id) markers.push({ ln: i + 1, typ, id, tags: parts.slice(1) });
            }
        }
    });
    let open = null;
    markers.forEach(m => {
        if ('+<'.includes(m.typ)) {
            open = open ? null : m;
        } else {
            if (open && open.id === m.id && m.ln > open.ln && isValid(lines, open.ln, m.ln, open.tags)) {
                let res = { start: open.ln, end: m.ln, identifier: open.id, tags: open.tags, }
                if (open.tags.includes("default")) {
                    res.contentLines = lines.slice(open.ln, m.ln - 1).map(l => l.trim().slice(2)).join("\n");
                }
                result.push(res);
            }
            open = null;
        }
    });
    if (open) err.push({ line: open.ln, message: `Unmatched start: //#${open.typ}${open.id}` });
    return result;
}

/**
 * Find property comment pairs in text and return an array of target blocks.
 * @param {string} text - File content to parse for property comment pairs.
 * @returns {TargetBlock[]} Array of discovered target blocks.
 */
export function find_properties_comment_pair(text) {
    const res = [];
    const lines = text.split(/\r?\n/);
    const m = [];
    lines.forEach((l, i) => {
        const t = l.trim();
        if ((t.startsWith('#+') || t.startsWith('#-')) && t.length > 2) {
            const typ = t[1];
            const parts = t.slice(2).trim().split(/\s+/);
            const id = parts[0];
            if (id) m.push({ ln: i + 1, typ, id, tags: parts.slice(1) });
        }
    });
    let open = null;
    m.forEach(x => {
        if (x.typ === '+') {
            open = open ? null : x;
        } else if (x.typ === '-') {
            if (open && open.id === x.id && x.ln > open.ln) {
                res.push({ start: open.ln, end: x.ln, identifier: open.id, tags: open.tags, contentLines: lines.slice(open.ln, x.ln - 1) });
            }
            open = null;
        }
    });
    // Unmatched start markers are ignored
    if (open) open = null;
    return res;
}

/**
 * Statically parse Markdown task definitions into a mapping from headings to operation arrays.
 * This function performs static parsing of markdown content and does not import or execute code, unlike dynamic parse_task_js.
 * @static
 * @param {string} mdtext - Markdown content with task code blocks.
 * @returns {TaskCollection} Mapping from section headings to list of Ops.
 */
export const parse_task_md = mdtext => {
  const res = {};
  let heading = "", inCode = false, id = "", args = [], buf = [];

  for (const line of mdtext.split(/\r?\n/)) {
    const t = line.trim();

    if (/^#+\s+/.test(t)) {
      heading = t.replace(/^#+\s+/, "");
      res[heading] = res[heading] || [];
    }
    else if (t.startsWith("```")) {
      if (!inCode) {
        [id, ...args] = t.slice(3).trim().split(/\s+/);
        buf = [];
      } else {
        const op = { identifier: id, content: buf.join("\n"), type: "diff" };
        if (args.length) {
          op.args = Object.fromEntries(
            args.map(a => a.split("=").slice(0, 2))
                      .filter(([k, v]) => k && v)
          );
        }
        res[heading].push(op);
      }
      inCode = !inCode;
    }
    else if (inCode) {
      buf.push(line);
    }
  }
  return res;
};

/**
 * Parse a manifest markdown into a structured ParsedManifest object.
 * @param {string} mdtext - Markdown content of the manifest.
 * @returns {ParsedManifest} Parsed manifest object.
 */
export function parseManifest(mdtext) {
    const lines = mdtext.split(/\r?\n/);
    const manifest = { name: '', author: '', args: [], local: [], extern: [] ,workflows:[]};
    let currentSection = null;
    let currentWorkflow = null;
    let inCode = false;
    let codeId = '';
    let codeBuf = [];

    for (const raw of lines) {
        const line = raw.trim();
        if (!inCode && line.startsWith('# ')) {
            manifest.name = line.slice(2).trim();
            //todo current Section?
            currentSection = null;
        } else if (!inCode && /^\*\*([^*]+)\*\*\s*(.*)$/.test(line)) {
            const [, key, val] = line.match(/^\*\*([^*]+)\*\*\s*(.*)$/);
            if (key === 'author') {
                manifest.author = val.trim();
            }
        } else if (!inCode && line.startsWith('## ')) {
            const sec = line.trim().slice(3).split(' ')[0];
            if (sec === 'args') currentSection = 'args';
            else if (sec === 'local') currentSection = 'local';
            else if (sec === 'extern') currentSection = 'extern';
            else currentSection = null;
        } else if (!inCode && line.startsWith('### ')) {
            currentSection = "workflow";
            const parts = line.slice(4).trim().split(/\s+/);
            const wfName = parts[0];
            const flagSet = parts.slice(1).join('') || '';
            
            const [condStr = '', setStr = ''] = flagSet.split("=>");
            const condFlags = condStr.split(/([ ])*,([ ])*/g).filter(s => s);
            const setFlags = setStr.split(/([ ])*,([ ])*/g).filter(s => s);
            currentWorkflow = { name: wfName, cond: condFlags, set: setFlags, tasks: [], inline: [] };
            manifest.workflows.push(currentWorkflow);
        } else if (!inCode && line.startsWith('- ') && currentSection === 'args') {
            //args section
            manifest.args.push(line.slice(2).trim());
        } else if (!inCode && line.startsWith('- ') && currentSection === 'local') {
            //local section
            const item = line.slice(2).trim();
            manifest.local.push(item);
        } else if (!inCode && line.startsWith('- ') && currentSection === 'extern' && currentWorkflow) {
            //现在跟local一样
            //extern section
        } else if(!inCode && currentSection === "workflow" && line.startsWith('- ')){
            currentWorkflow.tasks.push(line.slice(2).trim());
        }
        else if (line.startsWith('```')) {
            if (!inCode) {
                inCode = true;
                codeId = line.slice(3).trim();
                codeBuf = [];
            } else {
                if (currentWorkflow && codeId) {
                    currentWorkflow.inline.push({ identifier: codeId, content: codeBuf.join('\n'),type:"diff" });
                }
                inCode = false;
                codeId = '';
            }
        } else if (inCode) {
            codeBuf.push(raw);
        }
    }
    //deal with manifest.args
    let oldArgs = manifest.args;
    manifest.args = oldArgs.reduce((acc, arg) => {
        let [key,value] = arg.split(/[ ]*=[ ]*/g);
        acc[key] = value??"unset";
        return acc;
    }, {});
    return manifest;
}

/**
 * Parse a text template file into metadata and content body.
 * @param {string} tpltxt - Raw template file text.
 * @returns {ParsedTextTemplate} Parsed template with metadata and content.
 */
export const parse_tplt_txt = (tpltxt) => {
    const lines = tpltxt.split(/\r?\n/);
    const metadata = {};
    let contentStart = 0;
    
    // Parse metadata lines (consecutive lines starting with #)
    for (let i = 0; i < lines.length; i++) {
        const line = lines[i].trim();
        if (line.startsWith('#')) {
            if(line.startsWith("#*")){
                const [key, ...valueParts] = line.slice(2).trim().split(/\s+/);
                if (key) {
                    metadata[key] = valueParts.join(' ');
                }
            }
            
        } else {
            contentStart = i;
            break;
        }
    }
    
    // Get the remaining content
    const content = lines.slice(contentStart).join('\n');
    
    return {
        metadata,
        content
    };
}
