import { submit_file, reset_file, submit_diff } from '../lib/op.js';
import fs from 'fs';
import path from 'path';
import { test } from './frame.js';
import { scan_diff_block, scan_file_change } from '../lib/scan.js';

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

// 生成包含 diff 块的 Java 测试文件
let counter=0;
let spacepadding=n=>' '.repeat(Math.max(0,n-counter++));
const fixtureSource = path.join(process.cwd(), 'test', 'Test.java');
const complexLines = [];
// 添加 Java 文件结构头部
complexLines.push('import java.util.*;');
complexLines.push('public class Test {');
// 生成多行注释以增加复杂度
for (let i = 1; i <= 100; i++) {
    if(Math.random() > 0.6) {
        let indent=2+Math.floor(Math.random()*10);
        let blockindex=counter++;
        let lipus="nihao sayboasef boogy".split(" ");
        let lipusindex=Math.floor(Math.random()*lipus.length);
        complexLines.push(`${spacepadding(indent)}//#+ ${lipus[lipusindex]}_${blockindex}of${indent}`);
        complexLines.push(`${spacepadding(indent)}//#- ${lipus[lipusindex]}_${blockindex}of${indent}`);
    }
}
// 添加 main 方法
complexLines.push('    public static void main(String[] args) {');
complexLines.push('        System.out.println("Complex Test");');
complexLines.push('    }');
complexLines.push('}');
// 写入 fixture 文件
fs.writeFileSync(fixtureSource, complexLines.join('\n'), 'utf-8');

// ===================== 只测试 DIFF 相关功能 =====================

// 测试基本 diff 场景 - 简单替换
test.test('submit_diff - 基本替换', async () => {
    const testDir = await setupTestDir();
    const filePath = path.join(testDir, 'simple_diff.txt');
    const originalContent = 'Line 1\nLine 2\nLine 3\nLine 4\nLine 5';
    fs.writeFileSync(filePath, originalContent);
    
    const diffs = [{ start: 2, end: 3, content: 'Replaced Line' }];
    const result = await submit_diff(filePath, diffs);
    
    const expected = 'Line 1\nReplaced Line\nLine 4\nLine 5';
    test.assert(result === expected, '替换单行应正确生效');
    await cleanupTestDir();
});

// 测试多位置 diff 应用
test.test('submit_diff - 多位置变更', async () => {
    const testDir = await setupTestDir();
    const filePath = path.join(testDir, 'multi_pos.txt');
    const originalContent = 'A\nB\nC\nD\nE\nF';
    fs.writeFileSync(filePath, originalContent);
    
    const diffs = [
        { start: 2, end: 2, content: 'X' },  // 替换 B
        { start: 4, end: 4, content: 'Y' },  // 替换 D
        { start: 6, end: 6, content: 'Z' }   // 替换 F
    ];
    
    const result = await submit_diff(filePath, diffs);
    const expected = 'A\nX\nC\nY\nE\nZ';
    test.assert(result === expected, '替换多处不同位置应正确生效');
    await cleanupTestDir();
});

// 测试重叠位置 diff 合并
test.test('submit_diff - 重叠位置合并', async () => {
    const testDir = await setupTestDir();
    const filePath = path.join(testDir, 'overlap.txt');
    const originalContent = 'A\nB\nC\nD\nE';
    fs.writeFileSync(filePath, originalContent);
    
    const diffs = [
        { start: 2, end: 4, content: 'X' },  // 替换 B,C,D
        { start: 3, end: 5, content: 'Y' }   // 替换 C,D,E (重叠)
    ];
    
    const result = await submit_diff(filePath, diffs);
    const expected = 'A\nX\nY';
    test.assert(result === expected, '重叠位置替换应按顺序合并');
    await cleanupTestDir();
});

// 测试插入新行
test.test('submit_diff - 插入新行', async () => {
    const testDir = await setupTestDir();
    const filePath = path.join(testDir, 'insert.txt');
    const originalContent = 'A\nB\nC';
    fs.writeFileSync(filePath, originalContent);
    
    const diffs = [
        { start: 1, end: 0, content: 'Z' },  // 在开头插入
        { start: 4, end: 3, content: 'Y' }   // 在末尾后插入
    ];
    
    const result = await submit_diff(filePath, diffs);
    const expected = 'Z\nA\nB\nC\nY';
    test.assert(result === expected, '首尾插入新行应正确生效');
    await cleanupTestDir();
});

// 测试多行内容替换
test.test('submit_diff - 多行内容', async () => {
    const testDir = await setupTestDir();
    const filePath = path.join(testDir, 'multiline.txt');
    const originalContent = 'Line 1\nLine 2\nLine 3\nLine 4';
    fs.writeFileSync(filePath, originalContent);
    
    const diffs = [{
        start: 2, 
        end: 3, 
        content: ['New Line 2a', 'New Line 2b', 'New Line 2c']
    }];
    
    const result = await submit_diff(filePath, diffs);
    const expected = 'Line 1\nNew Line 2a\nNew Line 2b\nNew Line 2c\nLine 4';
    test.assert(result === expected, '替换为多行内容应正确应用');
    await cleanupTestDir();
});

// 测试复杂场景 - 大文件多位置混合操作
test.test('submit_diff - 复杂场景混合操作', async () => {
    const testDir = await setupTestDir();
    const filePath = path.join(testDir, 'complex.txt');
    
    // 从生成的 Java 文件复制内容作为初始状态
    fs.copyFileSync(fixtureSource, filePath);
    const originalContent = fs.readFileSync(filePath, 'utf-8');
    const lines = originalContent.split(/\r?\n/);
    const totalLines = lines.length;
    
    // 定义复杂 diff 组合
    const diffs = [
        { start: 1, end: 1, content: '// 头部替换' },
        { start: 3, end: 7, content: ['// 删除多行', '// 替换为两行'] },
        { start: Math.floor(totalLines/2), end: Math.floor(totalLines/2), content: '// 中间插入' },
        { start: totalLines, end: totalLines, content: ['// 文件末尾', '// 添加两行'] }
    ];
    
    const result = await submit_diff(filePath, diffs);
    debugger;
    // 验证替换结果
    const resultLines = result.split(/\r?\n/);
    test.assert(resultLines[0] === '// 头部替换', '头部替换应生效');
    test.assert(resultLines[2] === '// 删除多行', '多行替换第一行应生效');
    test.assert(resultLines[3] === '// 替换为两行', '多行替换第二行应生效');

    test.assert(resultLines[resultLines.length-2] === '// 文件末尾', '末尾添加第一行应生效');
    test.assert(resultLines[resultLines.length-1] === '// 添加两行', '末尾添加第二行应生效');
    await cleanupTestDir();
});

// 测试边界情况 - 空文件、空diff、越界位置
test.test('submit_diff - 边界情况处理', async () => {
    const testDir = await setupTestDir();
    const filePath = path.join(testDir, 'edge_cases.txt');
    
    // 空文件测试
    fs.writeFileSync(filePath, '');
    let result = await submit_diff(filePath, [{ start: 1, end: 1, content: 'New Content' }]);
    test.assert(result === 'New Content', '空文件替换应生效');
    
    // 空 diff 测试
    fs.writeFileSync(filePath, 'Original');
    result = await submit_diff(filePath, []);
    test.assert(result === 'Original', '空 diff 应保持原内容不变');
    
    // 越界位置测试 - 起始位置超出文件行数
    fs.writeFileSync(filePath, 'Line 1\nLine 2');
    result = await submit_diff(filePath, [{ start: 5, end: 6, content: 'Beyond EOF' }]);
    test.assert(result === 'Line 1\nLine 2\n\n\nBeyond EOF', '越界位置应能正确处理');
    
    await cleanupTestDir();
});

// 测试使用 scan_diff_block 获取 diff 位置并应用替换
test.test('scan_diff_block + submit_diff 实际场景测试', async () => {
    const testDir = await setupTestDir();
    
    // 使用已生成的带有 diff 标记的 Java 文件
    const javaFile = path.join(testDir, 'TestWithBlocks.java');
    fs.copyFileSync(fixtureSource, javaFile);
    
    // 扫描 Java 文件获取所有 diff 块位置
    const blocks = await scan_diff_block(testDir);
    test.assert(blocks.length > 0, '应找到至少一个 diff 块');
    
    // 打印找到的 diff 块信息
    console.log(`在 ${blocks.length} 个 diff 块中挑选位置修改：`);
    blocks.forEach((block, idx) => {
        if (idx < 5) { // 只打印前5个，避免信息过多
            console.log(`- 块 ${idx+1}: ${block.identifier}, 行 ${block.start}-${block.end}`);
        }
    });
    
    // 选择前三个 diff 块进行替换
    const targetBlocks = blocks.slice(0, Math.min(3, blocks.length));
    
    // 准备 diff 列表，为每个块生成替换内容
    const diffs = targetBlocks.map(block => ({
        start: block.start,
        end: block.end,
        content: [
            `    // 替换块 ${block.identifier}`,
            `    // 从行 ${block.start} 到 ${block.end}`,
            `    System.out.println("修改了 ${block.identifier}");`
        ]
    }));
    
    // 应用 diff
    const result = await submit_diff(javaFile, diffs);
    
    // 验证结果中包含替换内容
    targetBlocks.forEach((block, idx) => {
        const marker = `修改了 ${block.identifier}`;
        test.assert(result.includes(marker), `结果应包含对块 ${block.identifier} 的修改`);
    });
    
    // 保存修改后的内容到文件并重新扫描
    fs.writeFileSync(javaFile, result, 'utf-8');
    const blocksAfter = await scan_diff_block(testDir);
    
    // 验证替换后不再包含之前的diff块
    test.assert(blocksAfter.length < blocks.length, '替换后应减少diff块数量');
    
    // 验证被替换的块ID不再出现在扫描结果中
    const replacedIds = targetBlocks.map(b => b.identifier);
    const remainingIds = blocksAfter.map(b => b.identifier);
    replacedIds.forEach(id => {
        test.assert(!remainingIds.includes(id), `替换后不应再包含块 ${id}`);
    });
    
    // 清理
    await cleanupTestDir();
});

// 运行所有测试
test.run().catch(console.error); 