// Predefined output templates for formatting
const MESSAGES = {
  HEADER: p => `\n=== Scan Results for: ${p} ===`,
  FOUND_BLOCKS: (n, p) => `\n✅ Found ${n} Code Block(s) in ${p}:`,
  ANNOUNCE_FILE: f => `  -> File: ${f}`,
  NO_BLOCKS: "\nℹ️ No specific code blocks found matching the pattern.",
  FOUND_FILE_CHANGES: n => `\n📝 Found ${n} File Change(s):`,
  NO_FILE_CHANGES: "\nℹ️ No file changes detected.",
  ERRORS_HEADER: n => `\n❌ Found ${n} Error(s)/Warning(s):`,
  NO_ERRORS: "\n✅ No errors or warnings detected during the scan."
};
const padding = n => n.toString().padStart(3);

/**
 * 对内容进行行数截断，用于展示时只保留前若干行，并标记省略
 * @param {string} content - 原始内容
 * @param {number} maxLines - 最大显示行数
 * @returns {string} 处理后的内容字符串
 */
function clampContent(content, maxLines = 5) {
  const lines = content.split('\n');
  if (lines.length <= maxLines) return content;
  return lines.slice(0, maxLines).join('\n') + '\n...';
}

/** @typedef {import('./types').ScanResults} ScanResults */
/** @typedef {import('./types').ErrorEntry} ErrorEntry */
/** @typedef {import('./types').ParsedManifest} ParsedManifest */

/**
 * Formats the results of a scan performed by scan.js into an array of strings.
 *
 * @param {ScanResults} results - The results object containing locations and errors.
 * @param {string} project_root_dir - The root directory that was scanned, for context.
 * @returns {string[]} An array of strings representing the formatted output lines.
 */
export function format_scan_results(
    { locations, errors, file_changes },
    project_root_dir) {
    const output = []; // Renamed variable
    output.push(MESSAGES.HEADER(project_root_dir));
    // --- Format Locations (Grouped by File) ---
    if (locations && locations.length > 0) {
        output.push(MESSAGES.FOUND_BLOCKS(locations.length, project_root_dir));

        const locations_by_file =
            locations.reduce((acc, loc) => {
                acc[loc.file] = acc[loc.file] || [];
                acc[loc.file].push(loc);
                return acc;
            }, {});

        Object.keys(locations_by_file).sort().forEach(file => {

            output.push(MESSAGES.ANNOUNCE_FILE(file));

            locations_by_file[file].forEach(loc => {
                const tag_string = loc.tags.length > 0
                    ? ` [Tags: ${loc.tags.join(', ')}]`
                    : '';
                output.push(`     - Ln ${padding(loc.start)}-${padding(loc.end)}, #${loc.identifier}${tag_string}`);
            });
        });
    } else {
        output.push(MESSAGES.NO_BLOCKS);
    }

    // --- Format File Changes ---
    if (file_changes && file_changes.length > 0) {
        output.push(MESSAGES.FOUND_FILE_CHANGES(file_changes.length));
        file_changes.forEach(file => {
            output.push(`  -> ${file}`);
        });
    } else {
        output.push(MESSAGES.NO_FILE_CHANGES);
    }

    // --- Format Errors (Simplified) ---
    if (errors && errors.length > 0) {
        output.push(MESSAGES.ERRORS_HEADER(errors.length));
        errors.forEach(err => {
            // console.log(`DEBUG: Formatting error: ${JSON.stringify(err)}`); // Remove debug
            const location_str = err.line > 0 ? ` (Line ${err.line})` : ''; // Renamed variable
            // Simplified format
            output.push(`  -> ${err.file}${location_str}: ${err.message}`); // Use renamed variable
        });
    } else {
        output.push(MESSAGES.NO_ERRORS);
    }

    output.push(...summarize_scan_results({ locations, errors }));
    // console.log("DEBUG: formatScanResults finished."); // Remove debug
    return output;
}

/**
 * Generates a concise summary of scan results.
 *
 * @param {ScanResults} results - The results object containing locations and errors.
 * @param {string} project_root_dir - The root directory that was scanned, for context.
 * @returns {string[]} An array of strings representing the summary lines.
 */
export function summarize_scan_results({ locations, errors }) {
    const output_lines = [];
    const total_blocks = locations?.length ?? 0;
    const total_errors = errors?.length ?? 0;
    let files_with_blocks = 0;
    let default_blocks = 0;

    if (locations && locations.length > 0) {
        const unique_files = new Set();
        locations.forEach(loc => {
            unique_files.add(loc.file);
            if (loc.tags.includes('default')) {
                default_blocks++;
            }
        });
        files_with_blocks = unique_files.size;
    }
    output_lines.push(`[]Blocks:${total_blocks}${default_blocks ? `(${default_blocks} default)` : ''} in ${files_with_blocks} files []Errors:${total_errors}`);
    return output_lines;
}

/**
 * Formats manifest modules into a human-readable list.
 *
 * @param {ParsedManifest[]} modules - Array of parsed manifest objects
 * @returns {string[]} An array of formatted lines for each module and its workflows
 */
export function format_manifest_results(modules) {
  const out = [];
  if (!modules || modules.length === 0) {
    out.push("ℹ️ No modules found.");
    return out;
  }
  modules.forEach(mod => {
    out.push(`Module: ${mod.name}`);
    if (mod.author) out.push(`  Author: ${mod.author}`);
    if (mod.args) {
      // Format args whether array or object
      if (Array.isArray(mod.args)) {
        out.push(`  Args: ${mod.args.join(', ')}`);
      } else if (typeof mod.args === 'object') {
        const argList = Object.entries(mod.args)
          .map(([k, v]) => `${k}=${v}`)
          .join(', ');
        out.push(`  Args: ${argList}`);
      }
    }
    if (mod.local) out.push(`  Local: ${mod.local.join(', ')}`);
    if (mod.extern) out.push(`  Extern: ${mod.extern.join(', ')}`);
    if (mod.workflows) {
      out.push(`  Workflows:`);
      mod.workflows.forEach(w => {
        const cond = w.cond.length ? w.cond.join(', ') : 'none';
        const set = w.set.length ? w.set.join(', ') : 'none';
        out.push(`    - .${w.name} (cond: ${cond} => set: ${set})`);
        if (w.tasks && w.tasks.length) out.push(`      Ops: ${w.tasks.map(t => `#${t}`).join(', ')}`);
        if (w.inline && w.inline.length) {
          out.push(`      Inline ops:`);
          w.inline.forEach(inl => {
            out.push(`        * #${inl.identifier}:`);
            out.push(`          ${inl.content.replace(/\n/g, "\n          ")}`);
          });
        }
      });
    }
  });
  return out;
}

/**
 * 格式化 new_submit_tasks 的执行结果
 *
 * @param {Array<{id:string,type:string,file?:string,target?:string,result?:string}>} results
 * @param {number} [maxLines] - 补丁结果展示时的最大行数
 * @returns {string[]} 格式化后的描述行数组
 */
export function format_change_results(results, maxLines = 5) {
  const out = [];
  results.forEach(r => {
    switch (r.type) {
      case 'diff':
        // 仅输出操作提示，不显示具体 diff 内容
        out.push(`Patch '${r.id}' applied to file '${r.file}'.`);
        break;
      case 'default':
        out.push(`Restore default for '${r.id}' in file '${r.file}':`);
        out.push(clampContent(r.result, maxLines));
        break;
      case 'file':
        out.push(`Write file '${r.id}'.`);
        break;
      case 'template':
        out.push(`Apply template '${r.id}' to '${r.target || r.id}'.`);
        break;
      case 'unknown':
        out.push(`Unknown identifier '${r.id}'`);
        break;
      default:
        out.push(`Unsupported action type '${r.type}' for '${r.id}'.`);
    }
  });
  return out;
}

/**
 * Formats an array of operations into human-readable output lines.
 * @param {Array<{identifier:string,content:string,args?:object}>} ops - Array of op objects.
 * @param {number} [maxLines=5] - Maximum number of lines of content to display.
 * @returns {string[]} Array of formatted output lines.
 */
export function format_ops(ops, maxLines = 5) {
  const out = [];
  if (!ops || ops.length === 0) {
    out.push("ℹ️ No operations found.");
    return out;
  }
  out.push(`\n=== Operations (${ops.length}) ===`);
  ops.forEach(op => {
    out.push(`Op: #${op.identifier}`);
    if (op.args && Object.keys(op.args).length) {
      const argsList = Object.entries(op.args || {})
        .map(([k, v]) => `${k}=${v}`)
        .join(', ');
      out.push(`  Args: ${argsList}`);
    }
    out.push('  Content:');
    const clamped = clampContent(op.content || '', maxLines);
    clamped.split('\n').forEach(line => out.push(`    ${line}`));
  });
  return out;
}

// Alias for backwards compatibility
export const format_tasks = format_ops;

/**
 * Format tasks map returned by preapply_module for display.
 * @param {Object<string, {identifier:string,content:string,args?:object}>} mergedTasks - Mapping of identifier to Task object.
 * @param {number} [maxLines=5] - Maximum content lines to display per task.
 * @returns {string[]} Formatted output lines.
 */
export function format_preapply_tasks(mergedTasks, maxLines = 5) {
  const ops = Object.values(mergedTasks);
  return format_ops(ops, maxLines);
}

// Add new formatting functions for various scan results
/**
 * Formats diff block scan results.
 * @param {import('./types').TargetBlock[]} locations
 * @returns {string[]}
 */
export function format_diff_blocks(locations) {
  if (!locations || locations.length === 0) return ['ℹ️ No diff blocks found.'];
  const out = ['🔍 Diff Blocks:'];
  const grouped = locations.reduce((acc, loc) => {
    acc[loc.file] = acc[loc.file] || [];
    acc[loc.file].push(loc);
    return acc;
  }, {});
  Object.keys(grouped).sort().forEach(file => {
    out.push(`  -> File: ${file}`);
    grouped[file].forEach(loc => {
      out.push(`     - Ln ${padding(loc.start)}-${padding(loc.end)}, #${loc.identifier}`);
    });
  });
  return out;
}

/**
 * Formats snippet scan results.
 * @param {import('./types').Snippet[]} snippets
 * @returns {string[]}
 */
export function format_snippets(snippets) {
  if (!snippets || snippets.length === 0) return ['ℹ️ No snippets found.'];
  const out = ['📄 Snippets:'];
  snippets.forEach(s => {
    out.push(`  - #${s.identifier}:`);
    clampContent(s.content, 5).split('\n').forEach(line => out.push(`      ${line}`));
  });
  return out;
}

/**
 * Formats JS template scan results.
 * @param {import('./types').Template[]} templates
 * @returns {string[]}
 */
export function format_templates_js(templates) {
  if (!templates || templates.length === 0) return ['ℹ️ No JS templates found.'];
  const out = ['📄 JS Templates:'];
  templates.forEach(t => {
    out.push(`  - ${t.path}`);
    if (t.body.metadata && t.body.metadata.identifier) {
      out.push(`      id: ${t.body.metadata.identifier}`);
    }
  });
  return out;
}

/**
 * Formats text template scan results.
 * @param {import('./types').Template[]} templates
 * @returns {string[]}
 */
export function format_templates_txt(templates) {
  if (!templates || templates.length === 0) return ['ℹ️ No text templates found.'];
  const out = ['📄 Text Templates:'];
  templates.forEach(t => out.push(`  - ${t.path}`));
  return out;
}

/**
 * Formats file change scan results.
 * @param {string[]} changes
 * @returns {string[]}
 */
export function format_file_changes(changes) {
  if (!changes || changes.length === 0) return ['ℹ️ No file changes found.'];
  const out = ['📝 File Changes:'];
  changes.forEach(c => out.push(`  - ${c}`));
  return out;
}

// Add categorized help formatting for REPL
/**
 * Formats the help menu for available REPL commands, grouped by category.
 * @returns {string[]} Array of categorized help menu lines.
 */
export function format_help() {
  return [
    'Available commands:',
    '',
    'Scanning:',
    '  scan                             : Scan code blocks and update state',
    '  scan-project                     : Display scan results',
    '  scan-diff-block                  : Display diff blocks',
    '  scan-file-change                 : List file changes',
    '',
    'Modules & Tasks:',
    '  scan-modules                     : List manifest modules',
    '  scan-tasks                       : Scan and list all tasks',
    '  scan-task-js                     : Scan JS task files',
    '  scan-task-md                     : Scan MD task files',
    '',
    'Workflow:',
    '  eval-module <module> [args]      : Evaluate a module with state args',
    '  preapply-module <module> [args]  : Preview module application',
    '  submit-module <module> [args]    : Apply module workflows',
    '',
    'Operations:',
    '  apply <id> <content>             : Apply a single task',
    '  submit-file <file> <content>     : Write file and record changes',
    '  submit-diff <file> <start> <end> <content> : Apply a diff patch',
    '  submit-load <src> <dest>         : Record asset file submission',
    '  status                           : Show pending file changes',
    '',
    'File Management:',
    '  list                             : List scanned code blocks',
    '  reset-file <file>                : Reset a single file',
    '  reset-all                        : Reset all changes',
    '  replace-all                      : Replace all pending operations',
    '',
    'Info:',
    '  manifest                         : Display module manifest',
    '',
    'Utility:',
    '  help                             : Show this help menu',
    '  exit                             : Exit the REPL'
  ];
}
