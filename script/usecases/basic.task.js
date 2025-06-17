// here find the root dir containing 'core'
// first, find the file that fits the srcfile string
// then, reassemble the path
// finally, output the move op {identifier:"assets:"+...,args:{src,dest....}}

import fs from 'fs';
import path from 'path';

// Find root dir containing 'core' from inside out
function findRootDir() {
    let currentDir = process.cwd();
    const root = path.parse(currentDir).root;
    // 找到盘根就不找了
    while (currentDir !== root) {
        // Check if core directory exists
        if (fs.existsSync(path.join(currentDir, 'core'))) {
            return currentDir;
        }
        // Move up one directory
        currentDir = path.dirname(currentDir);
    }
    return null;
}
const rootpath = findRootDir();
// 确定 core 模块路径
const coreDir = path.join(rootpath, 'core');
if (!fs.existsSync(coreDir)) {
    throw new Error(`Core directory not found at ${coreDir}`);
}
// 确定 assets 目录路径
const assetsDir = path.join(coreDir, 'src', 'main', 'assets');
if (!fs.existsSync(assetsDir)) {
    throw new Error(`Assets directory not found at ${assetsDir}`);
}
// 确定 java 目录路径
const javaDir = path.join(coreDir, 'src', 'main', 'java');
if (!fs.existsSync(javaDir)) {
    throw new Error(`Java directory not found at ${javaDir}`);
}
// 确定 ShatteredPixelDungeon 包所在目录
const spdJavaDir = path.join(javaDir, 'com', 'shatteredpixel', 'shatteredpixeldungeon');
if (!fs.existsSync(spdJavaDir)) {
}

// Find source file recursively within 'script' directory
function findSourceFile(rootDir, srcfile) {
    const scriptDir = path.join(rootDir, 'script');
    // 支持带子目录路径匹配，先规范化去掉前缀 './' 或 '/'
    const normalized = srcfile.replace(/^(?:\.\/|\/|\\\\)+/, '');
    const parts = normalized.split(/[\\/]/);
    const pattern = parts.join('/');
    const isPathPattern = parts.length > 1;
    let found = null;
    function search(dir) {
        const entries = fs.readdirSync(dir);
        for (const entry of entries) {
            const fullPath = path.join(dir, entry);
            const stat = fs.statSync(fullPath);
            if (stat.isDirectory()) {
                search(fullPath);
                if (found) return;
            } else if (stat.isFile()) {
                if (isPathPattern) {
                    // 判断相对路径是否以 pattern 为后缀
                    const rel = path.relative(scriptDir, fullPath).split(path.sep).join('/');
                    if (rel === pattern || rel.endsWith(`/${pattern}`)) {
                        found = fullPath;
                        return;
                    }
                } else {
                    // 普通文件名匹配
                    if (entry === normalized || entry === srcfile || entry.includes(srcfile)) {
                        found = fullPath;
                        return;
                    }
                }
            }
        }
    }
    search(scriptDir);
    return found;
}

export default {
    asset([srcfile, destRelative]) {
        console.log("asset filing",srcfile,destRelative)
        // 在 script 目录中搜索源文件
        const srcPath = findSourceFile(rootpath, srcfile);
        if (!srcPath) {
            throw new Error(`Could not find source file matching ${srcfile}`);
        }
        // destRelative 是相对于 assetsDir 的路径
        const destPath = path.join(assetsDir, destRelative);
        return [{
            identifier: "assets:" + srcfile,
            type: "file",
            args: { src: srcPath, dest: destPath }
        }];
    },
    copy_root([srcfile, dest]) {
        // 在 script 目录中搜索源文件
        const srcPath = findSourceFile(rootpath, srcfile);
        if (!srcPath) {
            throw new Error(`Could not find source file matching ${srcfile}`);
        }

        // dest 是相对于项目根目录的路径
        const destPath = path.join(rootpath, dest);
        return [{
            identifier: "assets:" + srcfile,
            type: "file",
            args: { src: srcPath, dest: destPath }
        }];
    },
    spdfile([srcfile, destRelative]) {
        console.log("spd filing",srcfile,destRelative)
        // 在 script 目录中搜索源文件
        const srcPath = findSourceFile(rootpath, srcfile);
        if (!srcPath) {
            console.error("can t find")
            throw new Error(`Could not find source file matching ${srcfile}`);
        }
        // 如果未提供 destRelative，则默认使用源文件名
        const targetRelative = destRelative || srcfile;
        // 目标路径：ShatteredPixelDungeon 包目录下的相对路径
        const destPath = path.join(spdJavaDir, targetRelative);
        console.log("parsed into ",srcPath,destPath)
        return [{
            identifier: "codefile:" + srcfile,
            type: "file",
            args: { src: srcPath, dest: destPath }
        }];
    }
};
