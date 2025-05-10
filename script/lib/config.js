import { fileURLToPath } from 'url';
import { dirname, resolve } from 'path';
import fs from 'fs';
import path from 'path';

// __filename and __dirname setup for ESM
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const CONFIG_FILE = path.join(process.cwd(), 'dungeonrepl.config.json');

/**
 * Configuration for the root-level config.csv file
 */
export const csvConfig = {
  // Absolute path to the CSV file at the project root
  filePath: resolve(__dirname, '../config.csv'),
  // CSV parsing options
  parseOptions: {
    delimiter: ',',        // Field delimiter
    columns: true,         // Treat first row as header columns
    skip_empty_lines: true,
    trim: true,
  },
};

// 默认配置
const DEFAULT_CONFIG = {
  modules: {
    // 示例模块配置
    'example-module': {
      enabled: true,
      params: {
        param1: 'value1',
        param2: 'value2'
      }
    }
  },
  global: {
    maxDisplayLines: 10,
    colorEnabled: true
  }
};

// 加载配置
export function loadConfig() {
  try {
    if (fs.existsSync(CONFIG_FILE)) {
      const rawData = fs.readFileSync(CONFIG_FILE, 'utf8');
      return JSON.parse(rawData);
    }
  } catch (err) {
    console.error('Failed to load config:', err);
  }
  return DEFAULT_CONFIG;
}

// 保存配置
export function saveConfig(config) {
  try {
    fs.writeFileSync(CONFIG_FILE, JSON.stringify(config, null, 2), 'utf8');
  } catch (err) {
    console.error('Failed to save config:', err);
  }
}

// 获取模块配置
export function getModuleConfig(moduleName) {
  const config = loadConfig();
  return config.modules[moduleName] || { enabled: false, params: {} };
}

// 更新模块配置
export function updateModuleConfig(moduleName, newConfig) {
  const config = loadConfig();
  config.modules[moduleName] = newConfig;
  saveConfig(config);
}

// 获取全局配置
export function getGlobalConfig() {
  const config = loadConfig();
  return config.global;
}

// 更新全局配置
export function updateGlobalConfig(newConfig) {
  const config = loadConfig();
  config.global = newConfig;
  saveConfig(config);
} 