import { promises as fs } from 'fs';
import path from 'path';
/**
 * @typedef {Object} WalkFileOptions
 * @property {string[]} [ignoreDirs] - An array of directory names to ignore.
 * @property {(filePath: string) => boolean} [fileFilter] - A function that determines if a file should be included.
 * @property {(dirPath: string) => boolean} [dirFilter] - A function that determines if a directory should be included.
 * @property {(filePath: string, helpers: {content: () => Promise<string>}) => any} [fileHandler] - A function that handles a file.
 */
/**
 * Recursively walks a directory and returns all file paths that match the filter.
 *
 * @param {string} dir - The starting directory.
 * @param {Array<WalkFileOptions>|WalkFileOptions} options - single or array of option objects
 * @returns {Promise<string[]>|Promise<string[][]>} A list of file paths or array of file path arrays.
 */
export async function walkFiles(dir, options = {}) {
  const rawOpts = Array.isArray(options) ? options : [options];
  const opts = rawOpts.map(opt => {
    const {
      ignoreDirs = [],
      fileFilter = () => true,
      dirFilter = () => true,
      fileHandler = null
    } = opt;
    return { ignoreDirs:['node_modules', 'target', 'build', '.git',...ignoreDirs],fileFilter, dirFilter, fileHandler };
  });
  const results = opts.map(() => []);
  async function recurse(currentDir, contexts) {
    const entries = await fs.readdir(currentDir, { withFileTypes: true }).catch(() => []);
    for (const entry of entries) {
      const fullPath = path.join(currentDir, entry.name);
      if (entry.isDirectory()) {
        const nextContexts = contexts.filter(({ opt }) =>
          !opt.ignoreDirs.includes(entry.name) && opt.dirFilter(fullPath)
        );
        if (nextContexts.length) {
          await recurse(fullPath, nextContexts);
        }
      } else if (entry.isFile()) {
        await Promise.all(
          contexts.map(async ({ opt, idx }) => {
            if (opt.fileFilter(fullPath)) {
              if (typeof opt.fileHandler === 'function') {
                const res = await opt.fileHandler(fullPath, {
                  raw: () => fs.readFile(fullPath),
                  content: () => fs.readFile(fullPath, 'utf-8')
                });
                if (res != null) {
                  if (Array.isArray(res)) {
                    results[idx].push(...res);
                  } else {
                    results[idx].push(res);
                  }
                }
              } else {
                results[idx].push(fullPath);
              }
            }
          })
        );
      }
    }
  }
  await recurse(dir, opts.map((opt, idx) => ({ opt, idx })));
  return Array.isArray(options) ? results : results[0];
}

export const separate=(...idenfns)=>(arr)=>{
  let result=Array(idenfns.length+1).fill(0).map(()=>[]);
  let count=idenfns.length;
  for(let i=0;i<arr.length;i++){
    let cur=arr[i];
    let idx=idenfns.findIndex(id=>id(cur));
    result[idx==-1?count:idx].push(cur);
  }
  return result;
}
export const unzip = (...props) => obj => props.map(prop => obj[prop])
export const execIf=(filter,handler)=>(...args)=>filter(...args)?handler(...args):null
export const zip=(...props)=>arr=>{
  return props.reduce((acc,curr,curr_idx)=>{
    acc[curr]=arr[curr_idx];
    return acc;
  },{})
}
export const pipe=(...fns)=>(...args)=>{
  return fns.reduce((acc,cur)=>cur(acc),args)
}

//start, end 都是行号。包含start，不包含end
export const diff = (originalText, diffs) => {
  const lines = originalText.split(/\r?\n/);
  let output = [];
  let currentLine = 0;
  if(!Array.isArray(diffs))diffs=[diffs]
  // 按起始行升序处理，确保按文件顺序应用补丁
  diffs.sort((a, b) => a.start - b.start);

  for (const task of diffs) {
      const adjustedStart = task.start - 1; // 0-based索引，所以减1
      const adjustedEnd = task.end - 1;

      // 处理前置内容
      if (currentLine < adjustedStart) {
          let n=lines.slice(currentLine, adjustedStart);

          output.push(n.length>0?n.join('\n'):"");
      }

      // 生成新内容块（保持原样）
      const newContent = Array.isArray(task.content)
          ? task.content.join('\n')
          : task.content;

      output.push(newContent);
      currentLine = adjustedEnd ; // 移到end的下一行，因为不包含end行

      // 自动补充缺失行处理
      if (currentLine < adjustedEnd) {
          const missingLines = adjustedEnd - currentLine + 1;
          output.push(...Array(missingLines).fill(''));
          currentLine = adjustedEnd + 1;
      }
  }

  // 添加剩余内容
  if (currentLine < lines.length) {
      output.push(lines.slice(currentLine).join('\n'));
  }
  return output.filter(line=>line.length>0).join('\n');
};

// Simple ANSI color functions for formatting
export const colors = {
  red: msg => `\x1b[31m${msg}\x1b[0m`,
  green: msg => `\x1b[32m${msg}\x1b[0m`,
  yellow: msg => `\x1b[33m${msg}\x1b[0m`,
  blue: msg => `\x1b[34m${msg}\x1b[0m`,
  cyan: msg => `\x1b[36m${msg}\x1b[0m`,
  dim: msg => `\x1b[2m${msg}\x1b[0m`
};

// Unified log helper
export const log = {
  info: msg => console.log(colors.blue(`[INFO] ${msg}`)),
  success: msg => console.log(colors.green(`[SUCCESS] ${msg}`)),
  warn: msg => console.log(colors.yellow(`[WARN] ${msg}`)),
  error: msg => console.error(colors.red(`[ERROR] ${msg}`))
};

// 增加模糊匹配模块的函数
export function fuzzyFindModule(mods, alias) {
  const lower = alias.toLowerCase();
  return mods.filter(m => m.name.toLowerCase().includes(lower));
}

export const jlog=(...args)=>{
  console.log(...args.map(arg=>JSON.stringify(arg,null,2)));
}

