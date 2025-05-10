/**
 * Basic file operation functions
 */

//#region import
import fs from 'fs';
const { readFile, writeFile ,access,rename,readdir} = fs.promises;
import path from "path"
import {diff} from "./util.js"

//#endregion
/**
 * Submit a file by writing content to a file, backing up the existing file and recording changes in a diff file.
 * @param {string} file_path - The path of the file to submit.
 * @param {string|Buffer} content - The content to write to the file. If isText is true, content is a string; otherwise, a Buffer.
 * @param {boolean} [isText=true] - Whether to treat content as text (utf-8) or binary.
 * @returns {Promise<void>}
 */
export const submit_file = async (file_path, content, isText = true) => {
    // Ensure diff file path and relative file path
    const diff_file = path.join(path.dirname(file_path), ".diff.md");
    const relFile = path.relative(path.dirname(diff_file), file_path);
    // Append file record if not already present
    const diffExists = await fs.promises.access(diff_file).then(() => true).catch(() => false);
    // only backup if the file is newly submitted (not already listed in diff file)
    let needBackup = false;
    if (diffExists) {
        const existing = await readFile(diff_file, 'utf-8');
        const lines = existing.split(/\r?\n/);
        if (!lines.includes(relFile)) {
            await fs.promises.appendFile(diff_file, `\n${relFile}`, 'utf-8');
            needBackup = true;
        }
    } else {
        await fs.promises.appendFile(diff_file, relFile, isText ? 'utf-8' : undefined);
        needBackup = true;
    }
    // Backup original file if exists
    const fileExists = await fs.promises.access(file_path).then(() => true).catch(() => false);
    if (fileExists && needBackup) {
        const stat = await fs.promises.stat(file_path).catch(() => null);
        if (stat && stat.isFile()) {
            const backupName = `${path.basename(file_path)}.${Date.now()}.bak`;
            const backupPath = path.join(path.dirname(file_path), backupName);
            await fs.promises.rename(file_path, backupPath);
            const relBackup = path.relative(path.dirname(diff_file), backupPath);
            // Append backup entry when needed
            await fs.promises.appendFile(diff_file, `\n<- ${relBackup}`, 'utf-8');
        }
    }
    // Write new content
    await writeFile(file_path, content, 'utf-8');
}
/**
 * Reset a file to its last backup or delete it if no backup exists, and update the diff file.
 * @param {string} file_path - The path of the file to reset.
 * @returns {Promise<void>}
 */
export const reset_file = async (file_path) => {
    const dir = path.dirname(file_path);
    const diff_file = path.join(dir, '.diff.md');

    // 如果没有 diff 文件，直接结束
    const hasDiff = await access(diff_file).then(() => true).catch(() => false);
    if (!hasDiff) return;

    // 读取 diff 内容，按行拆分并去空
    const lines = (await readFile(diff_file, 'utf-8').catch(() => '')).split(/\r?\n/).map(l => l.trim()).filter(l => l);
    


    // 收集备份文件名
    const allFiles = await readdir(dir).catch(() =>console.log("here, readdir error",e) ||[]);
    const prefix = `${path.basename(file_path)}.`;
    const bakFiles = allFiles.filter(f => f.startsWith(prefix) && f.endsWith('.bak'))
        .sort((a, b) => Number(a.slice(prefix.length, -4)) - Number(b.slice(prefix.length, -4)));

    let deleted_path=null;
    // 如果存在备份，则恢复最新一个
    if (bakFiles.length > 0) {
        const lastBak = bakFiles[bakFiles.length - 1];
        const bakPath = path.join(dir, lastBak);
        deleted_path=bakPath;
        rename(bakPath, file_path).catch(() => { });
        
    } else {
        // 没有备份，说明是新文件，要删除它
        deleted_path=file_path;
        await fs.promises.unlink(file_path).catch(() => { });
    }
    // 移除原始文件记录
    if(deleted_path!=null){
        let base_del_path=path.basename(deleted_path);
        const idxFile = lines.findIndex(l => l.endsWith(base_del_path));
        if (idxFile !== -1) lines.splice(idxFile, 1);
    }
    // 写回或删除 diff 文件
    if (lines.length > 0) {
        await fs.promises.writeFile(diff_file, lines.join('\n'), 'utf-8').catch(() => { });
    } else {
        await fs.promises.unlink(diff_file).catch(() => { });
    }
}
//tasks=[{start:1,end:2,content:'edited'}]
/**
 * Apply diffs to a file's content.
 * @param {string} path - The path to the file to apply diffs.
 * @param {import('./types').Diff|import('./types').Diff[]} diffs - One or more diff operations.
 * @returns {Promise<string>}
 */
export const submit_diff=async(path,diffs)=>{
    const originalText=await readFile(path,'utf-8');
    const result=diff(originalText,diffs);
    await writeFile(path,result,'utf-8');
    return result;
}
//in the future, we need to move some assets file, and add it in diffmd
/**
 * Load a resource file and submit it to a destination, recording changes in the diff file.
 * @param {string} src - The source file path.
 * @param {string} dest - The destination file path.
 * @returns {Promise<void>}
 */
export const submit_load=async(src,dest)=>{
    // Read resource file as binary Buffer
    const content = await readFile(src);
    await submit_file(dest, content,false);
}

