import path from 'path';
import { walkFiles } from './util.js';
import { find_comment_pairs, parseManifest as parse_manifest, parse_task_md as _parse_task_md, parse_tplt_txt } from './parser.js';
import { pathToFileURL } from 'url';
/** @typedef {import('./types').ScanResults} ScanResults */
/** @typedef {import('./types').ErrorEntry} ErrorEntry */
/** @typedef {import('./types').Snippet} Snippet */
/** @typedef {import('./types').Template} Template */
/** @typedef {import('./types').Op} Op */
/** @typedef {import('./types').TaskGenerator} TaskGenerator */
/** @typedef {import('./types').Task} Task */
/** @typedef {import('./types').TaskCollection} TaskCollection */
/** @typedef {import('./types').ScanTaskResult} ScanTaskResult */
/** @typedef {import('./types').ParsedManifest} ParsedManifest */
/** @typedef {import('./types').TargetBlock} TargetBlock */

/**
 * Scan for Java diff blocks by parsing comment markers.
 * @param {string} directory - Root directory to scan for Java diff blocks.
 * @returns {Promise<TargetBlock[]>} Array of discovered target blocks.
 */
export async function scan_diff_block(directory) {
    // 扫描 Java 文件，并返回注释对
    const locations = await walkFiles(directory, {
        fileFilter: f => f.endsWith('.java'),
        fileHandler: async (filePath, { content }) => {
            const pairs = await find_comment_pairs(await content().catch(() => ""));
            pairs.forEach(l => l.file = path.relative(directory, filePath));
            return pairs;
        }
    });
    return locations;
}

/**
 * Scan for file changes recorded in .diff.md files.
 * @param {string} directory - Root directory to scan for .diff.md files.
 * @returns {Promise<string[]>} Array of file change paths.
 */
export async function scan_file_change(directory) {
    return await walkFiles(directory, {
        ignoreDirs:["bin"],
        fileFilter: f => f.endsWith('.diff.md'),
        fileHandler: async (filePath, { content }) => {
            let content_str = await content().catch(i => "");
            let lines = content_str.split(/[\r\n]/g).filter(line => line.trim() !== "");
            return lines.map(line => path.join(path.dirname(filePath), line.trim()));
        }
    });
}


/**
 * Combine .md and .js
 * @param {string} project_root_dir - Project root directory.
 * @returns {Promise<Array<Task|TaskGenerator|TaskCollection>>} Tasks, generators, or task collections.
 */
export async function scan_tasks(project_root_dir) {
    // Combine JS and Markdown task collections
    const jsCollections = await scan_task_js(project_root_dir);
    const mdCollections = await scan_task_md(project_root_dir);
    // Return unified array of task collections
    return [...jsCollections, ...mdCollections];
}

/**
 * Scan for JS task files (.task.js) and import them as Task objects or TaskGenerator functions.
 * @param {string} project_root_dir - Project root directory.
 * @returns {Promise<TaskCollection[]>} Array of tasks or task generators annotated with source file path.
 */
export async function scan_task_js(project_root_dir) {
    return await walkFiles(project_root_dir, {
        fileFilter: f => f.endsWith('.task.js'),
        fileHandler: async (filePath) => {
            const sm = await import(pathToFileURL(filePath).href);
            const tasks = sm.default;
            // Annotate static array or dynamic generator with source path
            Object.defineProperty(tasks, '_path', { value: filePath, enumerable: false });
            return tasks;
        }
    });
}

/**
 * Scan for Markdown task files (.task.md) and parse them, adding file path metadata.
 * @param {string} project_root_dir - Project root directory.
 * @returns {Promise<TaskCollection[]>} Array of task collections with `_path` metadata.
 */
export async function scan_task_md(project_root_dir) {
    return await walkFiles(project_root_dir, {
        fileFilter: f => f.endsWith('.task.md'),
        fileHandler: async (filePath, { content }) => {
            let res = _parse_task_md(await content());
            Object.defineProperty(res, '_path', {
                value: filePath,
                enumerable: false,
            });
            return res;
        }
    });
}

/**
 * Scan for manifest files (.manifest.md) and parse module definitions.
 * @param {string} project_root_dir - Project root directory.
 * @returns {Promise<ParsedManifest[]>} Array of parsed manifest objects.
 */
export async function scan_modules(project_root_dir) {
    return await walkFiles(project_root_dir, {
        fileFilter: f => f.endsWith('.manifest.md'),
        fileHandler: async (filePath, { content }) => {
            return parse_manifest(await content());
        }
    });
}

/**
 * Perform a full workspace scan, retrieving diff blocks, file changes, snippets, and templates.
 * @param {string} project_root_dir - Project root directory.
 * @returns {Promise<ScanResults>} Scan results for the workspace.
 */
export async function scan_workspace(project_root_dir) {
    if (!project_root_dir || typeof project_root_dir !== 'string') {
        throw new Error('project_root_dir must be a non-empty string');
    }
    const result ={};
    const diff_blocks = await scan_diff_block(project_root_dir);//locations,error
    result.locations = diff_blocks;
    result.errors = [];
    result.diff_blocks=diff_blocks;
    const file_changes = await scan_file_change(project_root_dir);
    result.file_changes = file_changes;
    const snippets = await scan_snippets(project_root_dir);
    result.snippets = snippets;
    const templates = await scan_templates_txt(project_root_dir);
    result.templates = templates;
    return result;
}


/**
 * Scan for JS templates (.tplt.js) and import them.
 * @param {string} project_root_dir - Project root directory.
 * @returns {Promise<Template[]>} Array of templates.
 */
export async function scan_templates(project_root_dir) {
    return await walkFiles(project_root_dir, {
        fileFilter: f => f.endsWith('.tplt.js'),
        fileHandler: async (filePath) => {
            let abs = path.resolve(path.dirname(path.resolve(project_root_dir)), filePath);
            let ddd = "./" + path.relative(project_root_dir, abs)
            let sm = await import(ddd);
            return { path: filePath, body: sm.default };
        }
    });
}

//not used
/**
 * Scan for text templates (.tplt) and parse them.
 * @param {string} project_root_dir - Project root directory.
 * @returns {Promise<Template[]>} Array of text templates.
 */
export async function scan_templates_txt(project_root_dir){
    return await walkFiles(project_root_dir, {
        fileFilter: f => f.endsWith('.tplt'),
        fileHandler: async (filePath,{content}) => {
            return {path:filePath,body:parse_tplt_txt(await content())}
        }
    });
}

/**
 * Scan for snippet files (.snippet.js, .snippets.js) and import them.
 * @param {string} project_root_dir - Project root directory.
 * @returns {Promise<Snippet[]>} Array of snippets.
 */
export async function scan_snippets(project_root_dir) {
    return await walkFiles(project_root_dir, {
        fileFilter: f => f.endsWith('.snippet.js') || f.endsWith('.snippets.js'),
        fileHandler: async (filePath) => {
            let abs = path.resolve(path.dirname(path.resolve(project_root_dir)), filePath);
            let ddd = "./" + path.relative(project_root_dir, abs)
            let sm = await import(ddd);
            return sm.default;
        }
    });
}