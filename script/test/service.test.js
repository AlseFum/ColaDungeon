import fs from 'fs';
import path from 'path';
import { test } from './frame.js';
import { scan_project, scan_modules, scan_templates_txt } from '../scan.js';
import { project2locations, new_submit_tasks, arrange_workflow } from '../service.js';

// 测试 diff 分支的全流程
test.test('full flow - diff branch', async () => {
  const testDir = path.join(process.cwd(), 'exp');
  if (!fs.existsSync(testDir)) fs.mkdirSync(testDir, { recursive: true });
  const oldCwd = process.cwd();
  process.chdir(testDir);

  const javaFile = 'Test.java';
  const content = ["public class Test {",'//#+ block1', 'int nihao=1;', '//#- block1',"}" ].join('\n');
  fs.writeFileSync(javaFile, content, 'utf-8');
debugger;
  const scanResult = await scan_project(testDir);
  const locations = project2locations(scanResult);
  const merged_task = { block1: { content: 'newLine' } };
  debugger;
  const results = await new_submit_tasks(locations, merged_task);
    debugger;
  test.assert(results.length === 1, '应该处理一个任务');
  test.assert(results[0].type === 'diff', '类型应该是 diff');

  const updated = fs.readFileSync(javaFile, 'utf-8');
  test.assert(updated.includes('newLine'), '文件内容应该包含 newLine');

  process.chdir(oldCwd);
  fs.rmSync(testDir, { recursive: true, force: true });
});

// 测试模板分支的全流程
test.test('full flow - template branch', async () => {
  const testDir = path.join(process.cwd(), 'exp_flow_tpl');
  if (!fs.existsSync(testDir)) fs.mkdirSync(testDir, { recursive: true });
  const oldCwd = process.cwd();
  process.chdir(testDir);

  const tpltFile = 'foo.tplt';
  const tplContent = ['#*identifier fooTpl', '#*foo bar', 'helloTpl'].join('\n');
  fs.writeFileSync(tpltFile, tplContent, 'utf-8');

  const scanResult = await scan_project(testDir);
  const locations = project2locations(scanResult);
  const merged_task = { fooTpl: {} };
  const results = await new_submit_tasks(locations, merged_task);

  test.assert(results.length === 1, '应该处理一个模板任务');
  test.assert(results[0].type === 'template', '类型应该是 template');

  test.assert(fs.existsSync('fooTpl'), '应该生成文件 fooTpl');
  const generated = fs.readFileSync('fooTpl', 'utf-8');
  test.assert(generated === 'helloTpl', '生成内容应该匹配模板');

  process.chdir(oldCwd);
  fs.rmSync(testDir, { recursive: true, force: true });
});

// 端到端测试：从扫描 manifest 到根据模板生成文件
test.test('full flow - from manifest to file generation', async () => {
  const testDir = path.join(process.cwd(), 'exp_flow_manifest');
  if (!fs.existsSync(testDir)) fs.mkdirSync(testDir, { recursive: true });
  const oldCwd = process.cwd();
  process.chdir(testDir);

  // 创建 manifest 文件
  const manifestName = 'my.mod.manifest.md';
  const manifestContent = ['# MyModule', '**author** Tester'].join('\n');
  fs.writeFileSync(manifestName, manifestContent, 'utf-8');

  // 创建纯文本模板文件
  const tplName = 'bar.tplt';
  const tplContent = ['#*identifier bar', 'Generated Content'].join('\n');
  fs.writeFileSync(tplName, tplContent, 'utf-8');

  // 扫描模块和模板
  const modules = await scan_modules(testDir);
  test.assert(modules.length === 1, 'scan_modules 应扫描到一个模块');
  const templates = await scan_templates_txt(testDir);
  test.assert(templates.length === 1, 'scan_templates_txt 应扫描到一个纯文本模板');

  // 组合 locations 并执行 new_submit_tasks
  const scanResult = await scan_project(testDir);
  const locations = project2locations(scanResult);
  const results = await new_submit_tasks(locations, { bar: {} });

  test.assert(results.length === 1, 'new_submit_tasks 应处理一个任务');
  test.assert(results[0].type === 'template', '任务类型应为 template');

  // 验证生成文件
  test.assert(fs.existsSync('bar'), '应生成文件 bar');
  const out = fs.readFileSync('bar', 'utf-8');
  test.assert(out === 'Generated Content', '生成文件内容应匹配模板');

  process.chdir(oldCwd);
  fs.rmSync(testDir, { recursive: true, force: true });
});

// 端到端测试：扫描 manifest 并验证 workflow 与 inline tasks 以及调度顺序
test.test('full flow - manifest workflows and inline tasks', async () => {
  const testDir = path.join(process.cwd(), 'exp_flow_wf');
  if (!fs.existsSync(testDir)) fs.mkdirSync(testDir, { recursive: true });
  const oldCwd = process.cwd();
  process.chdir(testDir);

  // 创建带 workflows 和 inline tasks 的 manifest 文件
  const manifestName = 'wf.mod.manifest.md';
  const mdLines = [
    '# MyMod',
    '**author** Tester',
    '## args',
    '- none',
    '### wf1 => flagA',
    '- task1',
    '``` inline1',
    'Inline Content 1',
    '```',
    '### wf2 flagA =>',
    '- task2',
    '``` inline2',
    'Inline Content 2',
    '```'
  ];
  fs.writeFileSync(manifestName, mdLines.join('\n'), 'utf-8');

  // 扫描 modules
  const modules = await scan_modules(testDir);
  test.assert(modules.length === 1, '应扫描到一个模块');
  const wfList = modules[0].body.workflows;
  test.assert(wfList.length === 2, '应有两个 workflow');

  // 验证第一个 workflow
  const wf1 = wfList[0];
  test.assert(wf1.name === 'wf1', '第一个 workflow 名称应为 wf1');
  test.assert(wf1.cond.length === 0, 'wf1 应无条件');
  test.assert(wf1.set.length === 1 && wf1.set[0] === 'flagA', 'wf1 应设置 flagA');
  test.assert(wf1.tasks.length === 1 && wf1.tasks[0] === 'task1', 'wf1 应有 task1');
  test.assert(wf1.inline.length === 1 && wf1.inline[0].identifier === 'inline1', 'wf1 应有 inline1');
  test.assert(wf1.inline[0].content === 'Inline Content 1', 'inline1 内容应匹配');

  // 验证第二个 workflow
  const wf2 = wfList[1];
  test.assert(wf2.name === 'wf2', '第二个 workflow 名称应为 wf2');
  test.assert(wf2.cond.length === 1 && wf2.cond[0] === 'flagA', 'wf2 应依赖 flagA');
  test.assert(wf2.set.length === 0, 'wf2 无设置');
  test.assert(wf2.tasks.length === 1 && wf2.tasks[0] === 'task2', 'wf2 应有 task2');
  test.assert(wf2.inline.length === 1 && wf2.inline[0].identifier === 'inline2', 'wf2 应有 inline2');
  test.assert(wf2.inline[0].content === 'Inline Content 2', 'inline2 内容应匹配');

  // 验证调度顺序
  const batches = arrange_workflow(wfList, []);
  test.assert(batches.length === 2, '应有两个批次');
  test.assert(batches[0][0] === 'wf1', '第一批次应包含 wf1');
  test.assert(batches[1][0] === 'wf2', '第二批次应包含 wf2');

  process.chdir(oldCwd);
  fs.rmSync(testDir, { recursive: true, force: true });
});

// 测试 arrange_workflow 条件响应
test.test('arrange_workflow - respects initial flags', () => {
  const workflows = [
    { name: 'a', cond: [], set: ['flag1'], tasks: [], inline: [] },
    { name: 'b', cond: ['flag1'], set: [], tasks: [], inline: [] }
  ];
  // 未提供 flag1 时
  let batches = arrange_workflow(workflows, []);
  test.assert(batches.length === 2, '没有初始 flag1 应生成两批次');
  test.assert(batches[0][0] === 'a', '第一批次应包含 a');
  test.assert(batches[1][0] === 'b', '第二批次应包含 b');
  // 提供 flag1 时
  batches = arrange_workflow(workflows, ['flag1']);
  test.assert(batches.length === 1, '提供初始 flag1 应生成一批次');
  test.assert(batches[0].includes('a') && batches[0].includes('b'), '第一批次应同时包含 a 和 b');
});

// 运行所有测试
test.run().catch(console.error); 