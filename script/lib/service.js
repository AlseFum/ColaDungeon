import { submit_diff, submit_file, reset_file, submit_load } from "./op.js";
import path from 'path';
import fs from 'fs';
import { scan_workspace, scan_modules, scan_task_js, scan_task_md } from './scan.js';

/**
 * @typedef {import('./types').Op} Op
 * @typedef {import('./types').Workflow} Workflow
 * @typedef {import('./types').ParsedManifest} ParsedManifest
 * @typedef {import('./types').LocationsTuple} LocationsTuple
 * @typedef {import('./types').BatchResult} BatchResult
 */

/**
 * 将各种形式的 task（函数、单个 Op 对象、Op 数组）"实体化"为 Op[]。
 * @param {Op|Op[]|function(Object<string,string>): Op|Op[]} task
 * @param {Object<string,string>} params  参数映射，用来替换模板中的占位符
 * @returns {Op[]}
 */
export const reify_task = (task, params = {}) => {
  if (typeof task === 'function') {
    const result = task(params);
    return Array.isArray(result) ? result : [result];
  }

  if (Array.isArray(task)) {
    // Recursively 扁平化所有子 task
    return task.flatMap(sub => reify_task(sub, params));
  }

  if (typeof task === 'object' && task.args) {
    // 对带占位符的任务做模板替换
    const content = task.content.replace(/\{\{(.*?)\}\}/g, (_, key) =>
      params[key] ?? task.args[key] ?? `{{${key}}}`
    );
    return [{ ...task, content }];
  }

  if (typeof task === 'object') {
    // 纯粹的 Op 对象
    return [task];
  }

  console.warn('[reify_task] 不支持的 task 类型：', task);
  return [];
};
export const task2ops = reify_task;


//expand some macros to enhance the task, like #!assets(./sprite,SPRITE), is to change the task to move the assets files.
//but we don't handle it now.
export const useMacro = (task) => {
  return task;
}

/**
 * Check if workflow conditions are met given a set of args.
 * @param {string[]} reqs - Required flags (prefix '!' for negation).
 * @param {Set<string>} args - Set of active flags.
 * @returns {boolean}
 */
const check_cond = (reqs, args) =>
  reqs.every(req => req.startsWith('!') ? !args.has(req.slice(1)) : args.has(req));

//only accept one module.
//that is the [*].body.workflow of scan_modules()
//return workflows in order;
/**
 * Arrange workflows into batches based on initial arguments.
 * @param {Workflow[]} wf - Array of workflow definitions.
 * @param {string[]} initial_args - Initially active argument keys.
 * @returns {string[][]} Batches of workflow names in execution order.
 */
export const arrange_workflow = (workflows, initArgs) => {
  const args = new Set(initArgs);
  const added = new Set();
  const batches = [];
  while (true) {
    const ready = workflows.filter(w => !added.has(w.name) && check_cond(w.cond, args));
    if (!ready.length) break;
    batches.push(ready.map(w => w.name));
    ready.forEach(w => {
      added.add(w.name);
      w.set.forEach(a => args.add(a));
    });
  }
  return batches;
}
/**
 * Mapping alias for arrange_workflow.
 * @type {typeof arrange_workflow}
 */
export const workflows2batchs = arrange_workflow;
//in workflow scope
/**
 * Given workflow definitions and a batch of names, reify inline and declared operations (ops).
 * @param {Workflow[]} workflow - Array of workflow definitions.
 * @param {string[]} batch - Names of workflows to execute this batch.
 * @param {Object<string, Op|Op[]|function>} task_set - Mapping of operation name to definition or generator.
 * @returns {{inline: Op[], tasks: Op[]}} Inline ops and declared ops to apply.
 */
export const batch2tasks = (workflow, batch, task_set) => {
  const avail = workflow.filter(w => batch.includes(w.name));
  const inline = avail.flatMap(w => w.inline);
  const tasks = avail.flatMap(w => w.tasks)
    .flatMap(t => {
      const [name, ...args] = t.split(/\s+/);
      const fn = task_set[name];
      if (!fn) {
        console.warn(`[batch2tasks] task ${name} not found`);
        return [];
      }
      return useMacro(reify_task(fn, args));
    });
  return { inline, tasks };
};

/**
 * Given workflow definitions and a batch of names, reify inline and declared operations into a flat array of ops.
 * @param {Workflow[]} workflow - Array of workflow definitions.
 * @param {string[]} batch - Names of workflows to execute this batch.
 * @param {Object<string, Op|Op[]|function>} task_set - Mapping of operation name to definition or generator.
 * @returns {Op[]} Array of operations (inline + tasks).
 */
export function batch2ops(workflow, batch, task_set) {
  // 使用 batch2tasks 作为中间层，合并 inline 与 tasks
  const { inline, tasks } = batch2tasks(workflow, batch, task_set);
  return [...inline, ...tasks];
}



/**
 * Merge an array of operations into a unique op map keyed by identifier.
 * @param {Op[]} ops - Array of operations to merge.
 * @returns {Object<string, Op>} Merged map of identifier to op.
 */
export function merge_ops(ops = []) {
  return ops.reduce((acc, op) => {
    if (!op) return acc;
    if (acc[op.identifier]) {
      acc[op.identifier].content += '\n' + op.content ?? "";
    } else {
      acc[op.identifier] = { ...op };
    }
    return acc;
  }, {});
}

/**
 * Convert scan results into maps for diff blocks, default blocks, file changes, and templates.
 * @param {object} scanResult
 * @param {TargetBlock[]} scanResult.locations - Array of target blocks scanned from code.
 * @param {string[]} scanResult.file_changes - List of file paths recorded in .diff.md.
 * @param {Template[]} scanResult.templates - List of parsed templates.
 * @returns {[Map<string,{file:string,start:number,end:number}>,Map<string,string>,Map<string,boolean>,Map<string,Template>]}
 */
export function scanresult2locations({ locations = [], file_changes = [], templates = [] }) {
  const diffMap = new Map();
  const defaultMap = new Map();

  locations.forEach(({ identifier, contentLines, file, start, end }) => {
    if (contentLines != null) {
      defaultMap.set(identifier, contentLines);
    } else {
      diffMap.set(identifier, { file, start, end });
    }
  });

  const fileMap = new Map(
    file_changes.map(filePath => [filePath, true])
  );

  const templateMap = new Map(
    templates.map(({ path: tplPath, body }) => {
      const id = body?.metadata?.identifier ?? path.basename(tplPath, path.extname(tplPath));
      return [id, body];
    })
  );

  return [diffMap, defaultMap, fileMap, templateMap];
}
/**
 * @todo it's only fit diff ops. we need more open style.
 * Submit a list of operations (ops) to files based on scan locations.
 * @param {LocationsTuple} locations - Tuple of diffMap, defaultMap, fileMap, templateMap.
 * @param {Object<string, {content: string}>} merged_task - Mapping of op identifier to content.
 * @returns {Promise<Array<{id:string,type:string,file?:string,result?:any}>>}
 */
export const submit_ops = async (locations, merged_task) => {
  const [diffMap, defaultMap, fileMap, templateMap] = locations;
  const entries = Object.entries(merged_task);
  const results = [];
  for (const [id, task] of entries) {
    if (task.type == 'reset') {
      if (diffMap.has(id)) {//重置diff
        const { file, start, end } = diffMap.get(id);
        const defaultLines = defaultMap.get(id);
        const content = Array.isArray(defaultLines) ? defaultLines.join('\n') : defaultLines;
        const patch = { start: start + 1, end: end - 1, content };
        const res = await submit_diff(file, [patch]);
        results.push({ id, type: 'reset', file, result: res });
      } else {
        //重置文件
        await reset_file(task.identifier ?? task.path);
        results.push({ id, type: 'reset', file: task.identifier ?? task.path, result: true });
      }
    } else
      if (task.type == 'file') {
        if (task.args && (task.args.source ?? task.args.src) && (task.args.to ?? task.args ?? dest)) {
          //submit load
          const source = task.args.source ?? task.args.src;
          const dest = task.args.to ?? task.args.dest;
          await submit_load(source, dest);
          results.push({ id, type: 'load', source, dest, result: true });
        } else {
          const pkey = task.identifier ?? task.path
          if (fileMap.has(pkey)) {
            await submit_file(fileMap.get(pkey), task.content)
          } else {
            await submit_file(task.identifier ?? task.path, task.content)
          }
          results.push({ id, type: 'file', file: pkey, result: true })
        }
      }
      else
        if (task.type == 'diff') {
          if (diffMap.has(id)) {
            const { file, start, end } = diffMap.get(id);
            const patch = { start: start + 1, end: end - 1, content: task.content };
            const res = await submit_diff(file, [patch]);
            results.push({ id, type: 'diff', file, result: res });
          }
          else if (defaultMap.has(id) && diffMap.has(id)) {
            const { file, start, end } = diffMap.get(id);
            const defaultLines = defaultMap.get(id);
            const content = Array.isArray(defaultLines) ? defaultLines.join('\n') : defaultLines;
            const patch = { start: start + 1, end: end - 1, content };
            const res = await submit_diff(file, [patch]);
            results.push({ id, type: 'default', file, result: res });
          }
        }
        else {
          console.warn('[submit_ops]: unknown op type:', task.type, task.identifier);
        }
  }
  return results;
};

/**
 * Evaluate a module's workflows given default and additional state arguments.
 * @param {ParsedManifest} moduleObj - Parsed manifest object.
 * @param {string[]} [stateArgs=[]] - Additional argument flags.
 * @param {Object<string, Op|function>} task_set - Task definitions.
 * @returns {Array<{batch:string[], inline:Op[], tasks:Op[]}>} Workflow batches with inline ops and declared ops.
 */
export function evaluate_module(moduleObj, stateArgs = [], task_set) {
  const args = [...Object.keys(moduleObj.args || {}), ...stateArgs];
  const batches = arrange_workflow(moduleObj.workflows, args);
  return batches.map(batch => {
    const { inline, tasks } = batch2tasks(moduleObj.workflows, batch, task_set);
    return { batch, inline, tasks };
  });
}

/**
 * Pre-apply a module: evaluate workflows then merge ops per batch.
 * @param {ParsedManifest} moduleObj - Manifest object.
 * @param {string[]} [stateArgs=[]] - Additional argument flags.
 * @param {Object<string, Op|function>} task_set - Task definitions.
 * @returns {Array<{batch:string[], merged:Object<string, Op>}>} Array of batches with merged ops.
 */
export function preapply_module(moduleObj, stateArgs = [], task_set) {
  // 1. 使用 evaluate_module 获取每个批次的 inline 和 tasks
  const plan = evaluate_module(moduleObj, stateArgs, task_set);
  // 2. 对每个批次合并 ops
  return plan.map(({ batch, inline, tasks }) => ({
    batch,
    merged: merge_ops([...inline, ...tasks])
  }));
}




/**
 * Apply a module batch-by-batch, rescanning after template ops.
 * @param {string} root - Project root directory for rescanning.
 * @param {{locations:any[], file_changes:string[], templates:any[]}} initialScan - Initial scan result.
 * @param {ParsedManifest} moduleObj - Parsed manifest object.
 * @param {string[]} [stateArgs=[]] - Additional argument flags.
 * @param {Object<string, Op|function>} task_set - Task definitions mapping names to ops.
 * @returns {Promise<Array<{id:string,type:string,file?:string,result:any}>>} Results of all submitted operations.
 */
export async function apply_module(root, initialScan, moduleObj, stateArgs = [], task_set) {
  const results = [];
  // initial scan state
  let scanres = initialScan;
  let locs = scanresult2locations({
    locations: scanres.locations,
    file_changes: scanres.file_changes,
    templates: scanres.templates
  });
  // pre-apply module: get merged ops per batch
  const batchMerged = preapply_module(moduleObj, stateArgs, task_set);
  for (const { batch, merged } of batchMerged) {
    // apply merged ops for this batch
    const batchRes = await submit_ops(locs, merged);
    results.push(...batchRes);
    // 每个批次执行后都重新扫描，更新 locs
    scanres = await scan_workspace(root);
    locs = scanresult2locations({
      locations: scanres.locations,
      file_changes: scanres.file_changes,
      templates: scanres.templates
    });
  }
  return results;
}

/**
 * Alias for converting project scan results into locations tuple.
 * @type {typeof scanresult2locations}
 */
export const project2locations = scanresult2locations;

export async function reset_all() {
  // 扫描工作空间
  const wks = await scan_workspace("./");
  const locations = scanresult2locations(wks);
  const [diffs, defaults, file_changes] = locations;


  let diff_changes = new Map();
  for (let [key, item] of diffs) {
    diff_changes.set(key, Object.assign(item, { identifier: key, type: "diff", content: defaults.get(key) ?? "" }));
  }

  let merged = merge_ops(diff_changes.values());
  //merge: 对同一个identifier的内容更改合并
  let dealed = Object.entries(merged).reduce((acc, [identifier, item]) => {
    const { file, start, end, content } = item;
    if (!acc[file]) {
      acc[file] = [];
    }
    acc[file].push({ start: start + 1, end, content });
    return acc;
  }, {});

  for (let file in dealed) {
    console.log(await submit_diff(file, dealed[file]));
  }
  for (let file of file_changes.keys()) {
    await reset_file(file);
  }
}