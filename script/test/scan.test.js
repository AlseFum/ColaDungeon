import { scan_diff_block, scan_file_change, scan_templates, scan_templates_txt, scan_snippets, scan_tasks, scan_task_js, scan_task_md, scan_modules, scan_project } from '../scan.js';
import fs from 'fs';
import path from 'path';
import { test } from './frame.js';

// 测试辅助函数
async function setupTestDir() {
    const testDir = path.join(process.cwd(), './exp');
    console.log('Setting up test directory:', testDir);
    if (!fs.existsSync(testDir)) {
        fs.mkdirSync(testDir);
    }
    return testDir;
}

async function cleanupTestDir() {
    const testDir = path.join(process.cwd(), './exp');
    if (fs.existsSync(testDir)) {
        fs.rmSync(testDir, { recursive: true, force: true });
    }
}

// 测试 scan_java
test.test('scan_java - 扫描Java文件中的代码块', async () => {
    const testDir = await setupTestDir();
    const javaFile = path.join(testDir, 'Test.java');
    const content = `
    //#+ tag
    public void test() {
        System.out.println("test");
    }
    //#- tag
    `;
    
    fs.writeFileSync(javaFile, content);
    
    debugger; // 检查扫描前的状态
    const result = await scan_diff_block(testDir);
    debugger; // 检查扫描后的状态
    
    test.assert(result.length === 1, '应该找到一个代码块');
    test.assert(result[0].identifier === 'tag', '代码块标识符应该匹配');
    test.assert(result[0].file === 'Test.java', '文件路径应该匹配');
    
    await cleanupTestDir();
});

// 测试 scan_modules
test.test('scan_modules - 扫描 manifest 模块', async () => {
    const testDir = await setupTestDir();
    const modPath = path.join(testDir, 'test.mod.manifest.md');
    const content = ['# ModuleX', '**author** Alice'].join('\n');
    fs.writeFileSync(modPath, content, 'utf-8');
    const modules = await scan_modules(testDir);
    test.assert(modules.length === 1, '应该扫描到一个模块');
    test.assert(modules[0].name === 'ModuleX', '模块名称应该正确');
    test.assert(modules[0].body.author === 'Alice', '作者应该正确');
    await cleanupTestDir();
});

// 测试 scan_templates_txt
test.test('scan_templates_txt - 扫描文本模板', async () => {
    const testDir = await setupTestDir();
    const tplPath = path.join(testDir, 'my.tplt');
    const content = ['#*id T1', 'hello', 'world'].join('\n');
    fs.writeFileSync(tplPath, content, 'utf-8');
    const templates = await scan_templates_txt(testDir);
    test.assert(templates.length === 1, '应该扫描到一个文本模板');
    test.assert(templates[0].path === 'my.tplt', '模板路径应该匹配');
    test.assert(templates[0].body.metadata.id === 'T1', '模板 metadata.id 应该正确');
    test.assert(templates[0].body.content === 'hello\nworld', '模板内容应该匹配');
    await cleanupTestDir();
});

// 运行所有测试
test.run().catch(console.error); 