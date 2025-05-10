import { find_comment_pairs, parse_task_md, parseManifest, parse_tplt_txt } from '../parser.js';
import { test } from './frame.js';

// 测试 find_comment_pairs
test.test('find_comment_pairs - 基本配对', async () => {
  const text = `//#+test
// 注释行
//#-test`;
  const result = await find_comment_pairs(text);
  test.assert(result.length === 1, '应该找到一个注释对');
  const p = result[0];
  test.assert(p.start === 1, 'start 应该为 1');
  test.assert(p.end === 3, 'end 应该为 3');
  test.assert(p.identifier === 'test', '标识符应该匹配');
});

// 测试 parse_task_md
test.test('parse_task_md - 简单 diff 块', () => {
  const md = '# Tasks\n## Section1\n```task1 arg1=val1 arg2=val2\nline a\nline b\n```';
  const result = parse_task_md(md);
  test.assert(result['Section1'].length === 1, '应该有一个任务在 Section1');
  const task = result['Section1'][0];
  test.assert(task.identifier === 'task1', '标识符应该匹配');
  test.assert(task.type === 'diff', '类型应该为 diff');
  test.assert(task.content === 'line a\nline b', '内容应该匹配行');
  test.assert(task.args.arg1 === 'val1', '参数 arg1 应该匹配');
  test.assert(task.args.arg2 === 'val2', '参数 arg2 应该匹配');
});

// 测试 parseManifest
test.test('parseManifest - 基本 manifest', () => {
  const md = '# ManifestName\n**author** Bob\n## args\n- x=10\n## local\n- foo\n### wf1\n- task1\n```code1\nline1\n```';
  const m = parseManifest(md);
  test.assert(m.name === 'ManifestName', '应该解析名称');
  test.assert(m.author === 'Bob', '应该解析 author');
  test.assert(m.args.x === '10', '应该解析 args');
  test.assert(m.local.length === 1 && m.local[0] === 'foo', '应该解析 local');
  test.assert(m.workflows.length === 1, '应该有一个工作流');
  const wf = m.workflows[0];
  test.assert(wf.name === 'wf1', '工作流名称应该匹配');
  test.assert(wf.tasks.length === 1 && wf.tasks[0] === 'task1', '任务应该匹配');
  test.assert(wf.inline.length === 1, '应该有一个内联代码块');
  test.assert(wf.inline[0].identifier === 'code1', '内联标识符应该匹配');
  test.assert(wf.inline[0].content === 'line1', '内联内容应该匹配');
  test.assert(wf.inline[0].type === 'diff', '内联类型应该为 diff');
});

// 测试 parse_tplt_txt
test.test('parse_tplt_txt - 基本模板解析', () => {
  const txt = `#*foo bar
#*baz qux
line1
line2`;
  const res = parse_tplt_txt(txt);
  test.assert(res.metadata.foo === 'bar', 'metadata foo 应该匹配');
  test.assert(res.metadata.baz === 'qux', 'metadata baz 应该匹配');
  test.assert(res.content === 'line1\nline2', '内容应该匹配');
});

// 运行所有测试
test.run().catch(console.error); 