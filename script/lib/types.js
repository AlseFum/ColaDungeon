//#region AboutTask

/**
 * @typedef {object} Op
 * @property {string} identifier - Unique operation identifier.
 * @property {string} content - Operation content or patch.
 * @property {string} [type] - Operation type (e.g., 'diff').
 * @property {Record<string,string>} [args] - Optional arguments map.
 */

/**
 * @typedef {Op[]} TaskBody
 */

/**
 * @typedef {function(...args:any[]):Op[]} TaskGenerator
 */

/**
 * @typedef {object} Task
 * @property {string} task_id - Identifier for the static task (e.g., source file path or name).
 * @property {Op[]|TaskGenerator[]} body - Task group that can be either a static Op[]  or a function that generates Op[]
 */

/**
 * @typedef {Record<string, (Task|TaskGenerator)[]>} TaskCollection
 * @property {string} _path - Path to the task definition file.
 */

/**
 * @typedef {object} TaskCall
 * @property {string} task_id - Identifier for the static task (e.g., source file path or name).
 * @property {string[]} args - Arguments for the task.
 */
//#endregion

//#region AboutScan
/**
 * @typedef {object} ScanWorkspaceResults
 * @property {TargetBlock[]} locations - Array of found block locations.
 * @property {string[]} file_changes - Array of file change paths (diff.md entries).
 * @property {Snippet[]} [snippets] - Array of code snippets.
 * @property {Template[]} [templates] - Array of templates.
 */

/**
 * @typedef {object} ErrorEntry
 * @property {string} file - File path where the error occurred.
 * @property {number} line - Line number of the error.
 * @property {string} message - Error message.
 */

/**
 * @todo
 * @typedef {object} Snippet
 * @property {string} identifier - Identifier of the snippet.
 * @property {string} content - Content of the snippet.
 */

/**
 * @todo
 * @typedef {object} Template
 * @property {string} path - Path to the template file.
 * @property {object} body - Template body content.
 * @property {object} [body.metadata] - Template metadata.
 * @property {string} [body.metadata.identifier] - Template identifier.
 */

/**
 * @typedef {object} ParsedTextTemplate
 * @property {Record<string,string>} metadata - Template metadata parsed from lines starting with "#*".
 * @property {string} content - The remaining content of the template.
 */

/**
 * @typedef {object} TargetBlock
 * @property {string} file - Relative file path from project root.
 * @property {number} start - Start line number of the target block (1-based inclusive).
 * @property {number} end - End line number of the target block (1-based inclusive).
 * @property {string} identifier - Identifier of the target block.
 * @property {string[]} tags - Tags associated with the block.
 * @property {string} [contentLines] - Optional content of the block when tagged as default.
 */

/**
 * Alias for TargetBlock, used historically.
 * @typedef {TargetBlock} DiffLocBlock
 */

/**
 * @alias {TaskCollection} ScanTaskResult
 */


//#endregion

//#region AboutModule&Workflow

/**
 * @typedef {object} ParsedManifest
 * @property {string} name - Manifest title.
 * @property {string} author - Manifest author.
 * @property {Record<string,string>} args - Parsed key/value args from manifest.
 * @property {string[]} local - Local section items.
 * @property {string[]} extern - Extern section items.
 * @property {Workflow[]} workflows - Array of workflow definitions.
 */
/**
 * @typedef {object} ParsedManifest
 * @property {string} name - Manifest title.
 * @property {string} author - Manifest author.
 * @property {Record<string,string>} args - Parsed key/value args from manifest.
 * @property {string[]} local - Local section items.
 * @property {string[]} extern - Extern section items.
 * @property {Workflow[]} workflows - Array of workflow definitions.
 */
/**
 * @todo
 * @typedef {object} Module
 * @property {string} name - Module name.
 * @property {TaskCollection} [body.tasks] - List of task identifiers.
 * @property {Workflow[]} [body.workflows] - List of workflows.
 */

/**
 * @typedef {object} Workflow
 * @property {string} name - Workflow name.
 * @property {string[]} cond - Workflow conditions.
 * @property {string[]} then - arguments that will add after the workflow is selected to be executed.
 * @property {TaskCall[]} tasks - List of task identifiers in this workflow.
 * @property {Op[]} inline - Inline operations associated with this workflow.
 */

//#endregion

//#region ForUtilFunctions

/**
 * @typedef {object} WalkFileOptions
 * @property {string[]} [ignoreDirs] - List of directory names to ignore.
 * @property {(filePath: string) => boolean} [fileFilter] - Function to filter files by path.
 * @property {(dirPath: string) => boolean} [dirFilter] - Function to filter directories by path.
 * @property {FileHandler} [fileHandler] - Optional file handler function.
 */

/**
 * @typedef {object} FileHandlerHelpers
 * @property {() => Promise<Buffer>} raw - Read file content as Buffer.
 * @property {() => Promise<string>} content - Read file content as string.
 */

/**
 * @callback FileHandler
 * @param {string} filePath - The path to the file.
 * @param {FileHandlerHelpers} helpers - Helper functions to read file content.
 * @returns {Promise<any>} The result of handling the file.
 */
//#endregion

//#region ForOp
/**
 * @typedef {object} ReplaceResult
 * @property {boolean} success - Indicates if the replace_all operation succeeded.
 * @property {string[]} modifiedFiles - Paths of files modified.
 * @property {any[]} errors - Any errors encountered during replacement.
 */

/**
 * @typedef {object} Diff
 * @property {number} start - Start line number of the diff (1-based inclusive).
 * @property {number} end - End line number of the diff (1-based inclusive).
 * @property {string|string[]} content - Replacement content, either string or array of strings.
 */

//#endregion










export {}; 