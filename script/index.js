import {startup} from './lib/repl.js';
import { loadConfig } from './lib/config.js';

// 加载配置
const config = loadConfig();

// 启动 REPL 并传递配置
startup(config);
