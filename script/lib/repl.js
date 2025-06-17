import readline from 'readline';
import { scan_workspace, scan_diff_block, scan_modules, scan_task_js, scan_task_md } from './scan.js';
import { project2locations, submit_ops, evaluate_module, preapply_module, apply_module, reset_all } from './service.js';
import { submit_file, reset_file, submit_diff, submit_load } from './op.js';
import path from 'path';
import { format_change_results, format_scan_results, format_manifest_results, format_ops, format_preapply_tasks, format_diff_blocks, format_help } from './format.js';

import {
  colors, log, fuzzyFindModule
} from './util.js'

import { getModuleConfig, updateModuleConfig, getGlobalConfig, updateGlobalConfig } from './config.js';

class ReplState {
  constructor(root, config) {
    this.root = root;
    this.config = config;
    this.locations = [];
    this.file_changes = [];
    this.templates = [];
    this.errors = [];
    this.modules = [];
    this.jsTasks = {};
    this.mdTasks = {};
    this.taskSet = {};
    this.commandHistory = [];
    this.currentHistoryIndex = -1;
    this.menuMode = false;
    this.menuItems = [];
    this.selectedMenuIndex = 0;
    this.configMode = false;
    this.configOptions = [
      { name: 'maxDisplayLines', description: 'Maximum lines to display for content', value: 5, min: 1, max: 50, step: 1 },
      { name: 'showExamples', description: 'Show examples in menu', value: true },
      { name: 'colorEnabled', description: 'Enable colored output', value: true },
      { name: 'verboseMode', description: 'Show verbose information', value: false },
      { name: 'pageSize', description: 'Number of lines per page in help', value: 10, min: 5, max: 50, step: 5 }
    ];
    this.selectedConfigIndex = 0;
  }

  async scan() {
    const workspace_instance = await scan_workspace(this.root);

    this.locations = workspace_instance.locations;
    this.errors = workspace_instance.errors;
    this.file_changes = workspace_instance.file_changes;
    this.templates = workspace_instance.templates;

    this.modules = await scan_modules(this.root);

    const jsCollections = await scan_task_js(this.root);
    this.jsTasks = Object.assign({}, ...jsCollections);
    const mdCollections = await scan_task_md(this.root);
    this.mdTasks = Object.assign({}, ...mdCollections);

    this.taskSet = Object.assign({}, this.jsTasks, this.mdTasks);
  }

  list() {
    if (!this.locations.length) {
      console.log('No blocks scanned.');
      return;
    }
    console.log('Scanned Blocks:');
    // 按文件分组并截断路径尾部，只保留文件名
    const groups = this.locations.reduce((acc, l) => {
      acc[l.file] = acc[l.file] || [];
      acc[l.file].push(l);
      return acc;
    }, {});
    Object.entries(groups).forEach(([file, locs]) => {
      const displayFile = path.basename(file);
      console.log(`  ${displayFile}:`);
      locs.forEach(l => {
        console.log(`    ${l.identifier} -> ${l.start}-${l.end}`);
      });
    });
  }

  // New method to generate menu items for the interactive menu
  generateMenuItems() {
    // Group commands by category
    return [
      { category: 'Scanning', commands: [
        { name: 'scan', description: 'Scan code blocks and update state' },
        { name: 'scan-workspace', description: 'Display scan results' },
        { name: 'scan-diff-block', description: 'Display diff blocks' },
        { name: 'scan-file-change', description: 'List file changes' }
      ]},
      { category: 'Modules & Tasks', commands: [
        { name: 'scan-modules', description: 'List manifest modules' },
        { name: 'scan-tasks', description: 'Scan and list all tasks' },
        { name: 'scan-task-js', description: 'Scan JS task files' },
        { name: 'scan-task-md', description: 'Scan MD task files' }
      ]},
      { category: 'Workflow', commands: [
        { name: 'eval-module', description: 'Evaluate a module with state args', args: '<module> [args]' },
        { name: 'preapply-module', description: 'Preview module application', args: '<module> [args]' },
        { name: 'submit-module', description: 'Apply module workflows', args: '<module> [args]' }
      ]},
      { category: 'Operations', commands: [
        { name: 'apply', description: 'Apply a single task', args: '<id> <content>' },
        { name: 'submit-file', description: 'Write file and record changes', args: '<file> <content>' },
        { name: 'submit-diff', description: 'Apply a diff patch', args: '<file> <start> <end> <content>' },
        { name: 'submit-load', description: 'Record asset file submission', args: '<src> <dest>' },
        { name: 'status', description: 'Show pending file changes' }
      ]},
      { category: 'File Management', commands: [
        { name: 'list', description: 'List scanned code blocks' },
        { name: 'reset-file', description: 'Reset a single file', args: '<file>' },
        { name: 'reset-all', description: 'Reset all changes' },
        { name: 'replace-all', description: 'Replace all pending operations' }
      ]},
      { category: 'Info', commands: [
        { name: 'manifest', description: 'Display module manifest' },
      ]},
      { category: 'Utility', commands: [
        { name: 'help', description: 'Show this help menu' },
        { name: 'menu', description: 'Show interactive menu (use arrow keys to navigate)' },
        { name: 'config', description: 'Configure REPL settings (use arrow keys to navigate)' },
        { name: 'exit', description: 'Exit the REPL' }
      ]}
    ];
  }

  // New method to display the interactive menu
  displayMenu(rl) {
    console.clear();
    console.log(colors.green('=== DungeonREPL Interactive Menu ==='));
    console.log(colors.cyan('Use ↑/↓ arrows to navigate, ←/→ to expand/collapse, Enter to select, Esc to exit menu'));
    console.log(colors.cyan('When a command is selected, required parameters will be shown as placeholders.'));
    console.log('');
    
    // Display menu items with categories
    let flatItemIndex = 0;
    this.menuItems.forEach((category, catIndex) => {
      const isCategorySelected = this.menuMode === 'category' && this.selectedMenuIndex === catIndex;
      console.log(colors.yellow(`${isCategorySelected ? '▶' : ' '} ${category.category}`));
      
      if (category.expanded || isCategorySelected) {
        category.commands.forEach((cmd, cmdIndex) => {
          const isSelected = this.menuMode === 'command' && 
                            this.selectedMenuIndex === flatItemIndex;
          console.log(`  ${isSelected ? '▶' : ' '} ${cmd.name}${cmd.args ? ' ' + cmd.args : ''}`);
          if (isSelected) {
            console.log(`    ${colors.dim(cmd.description)}`);
            
            // For commands with args, show additional usage guidance
            if (cmd.args) {
              console.log(`    ${colors.dim('Usage:')} ${colors.cyan(cmd.name + ' ' + cmd.args)}`);
              if (cmd.name === 'submit-file') {
                console.log(`    ${colors.dim('Example:')} ${colors.green('submit-file path/to/file.txt "File content here"')}`);
              } else if (cmd.name === 'submit-diff') {
                console.log(`    ${colors.dim('Example:')} ${colors.green('submit-diff path/to/file.txt 10 15 "New content for lines 10-15"')}`);
              } else if (cmd.name === 'reset-file') {
                console.log(`    ${colors.dim('Example:')} ${colors.green('reset-file path/to/file.txt')}`);
              } else if (cmd.name.includes('module')) {
                console.log(`    ${colors.dim('Example:')} ${colors.green(cmd.name + ' moduleName arg1 arg2')}`);
              }
            }
          }
          flatItemIndex++;
        });
      } else {
        flatItemIndex += category.commands.length;
      }
    });
    
    // Help footer
    console.log('');
    console.log(colors.dim('Tip: When you select a command, you can add required parameters before executing.'));
    console.log(colors.dim('     Press Enter to return to the prompt without executing a command.'));
  }

  // New method to handle menu navigation and selection
  async handleMenuKeys(rl) {
    this.menuItems = this.generateMenuItems();
    this.menuItems.forEach(category => category.expanded = false);
    this.menuMode = 'category';
    this.selectedMenuIndex = 0;
    
    // Enable raw mode to capture key presses
    readline.emitKeypressEvents(rl.input);
    if (rl.input.isTTY) {
      rl.input.setRawMode(true);
    }
    
    this.displayMenu(rl);
    
    return new Promise((resolve) => {
      const keyHandler = async (str, key) => {
        if (key.name === 'escape' || (key.name === 'c' && key.ctrl)) {
          // Exit menu mode
          rl.input.removeListener('keypress', keyHandler);
          if (rl.input.isTTY) {
            rl.input.setRawMode(false);
          }
          console.clear();
          resolve(null);
          return;
        }
        
        if (this.menuMode === 'category') {
          // Category navigation
          if (key.name === 'up' && this.selectedMenuIndex > 0) {
            this.selectedMenuIndex--;
          } else if (key.name === 'down' && this.selectedMenuIndex < this.menuItems.length - 1) {
            this.selectedMenuIndex++;
          } else if (key.name === 'right' || key.name === 'return') {
            // Expand category or enter category
            this.menuItems[this.selectedMenuIndex].expanded = true;
            this.menuMode = 'command';
            
            // Calculate first command index in this category
            let firstCommandIndex = 0;
            for (let i = 0; i < this.selectedMenuIndex; i++) {
              firstCommandIndex += this.menuItems[i].commands.length;
            }
            this.selectedMenuIndex = firstCommandIndex;
          }
        } else if (this.menuMode === 'command') {
          // Command navigation
          const totalCommands = this.menuItems.reduce((sum, cat) => sum + cat.commands.length, 0);
          
          if (key.name === 'up' && this.selectedMenuIndex > 0) {
            this.selectedMenuIndex--;
          } else if (key.name === 'down' && this.selectedMenuIndex < totalCommands - 1) {
            this.selectedMenuIndex++;
          } else if (key.name === 'left') {
            // Go back to category mode
            this.menuMode = 'category';
            
            // Find which category this command belongs to
            let commandCount = 0;
            for (let i = 0; i < this.menuItems.length; i++) {
              commandCount += this.menuItems[i].commands.length;
              if (this.selectedMenuIndex < commandCount) {
                this.selectedMenuIndex = i;
                break;
              }
            }
          } else if (key.name === 'return') {
            // Select command
            // Find which command this is
            let commandIndex = this.selectedMenuIndex;
            let categoryIndex = 0;
            
            for (let i = 0; i < this.menuItems.length; i++) {
              if (commandIndex < this.menuItems[i].commands.length) {
                categoryIndex = i;
                break;
              }
              commandIndex -= this.menuItems[i].commands.length;
            }
            
            const selectedCommand = this.menuItems[categoryIndex].commands[commandIndex];
            rl.input.removeListener('keypress', keyHandler);
            if (rl.input.isTTY) {
              rl.input.setRawMode(false);
            }
            console.clear();
            
            // Return the selected command with any required args template
            const commandText = selectedCommand.args ? 
              `${selectedCommand.name} ${selectedCommand.args}` : 
              selectedCommand.name;
            
            resolve(commandText);
            return;
          }
        }
        
        this.displayMenu(rl);
      };
      
      rl.input.on('keypress', keyHandler);
    });
  }

  // New method to display the configuration menu
  displayConfigMenu() {
    console.clear();
    console.log(colors.green('=== DungeonREPL Configuration ==='));
    console.log(colors.cyan('Use ↑/↓ arrows to navigate, ←/→ to change values, Enter to save, Esc to exit'));
    console.log('');
    
    this.configOptions.forEach((option, index) => {
      const isSelected = index === this.selectedConfigIndex;
      const prefix = isSelected ? '▶ ' : '  ';
      
      if (typeof option.value === 'boolean') {
        // Display boolean options as toggle
        console.log(`${prefix}${option.name}: ${option.value ? colors.green('Enabled') : colors.red('Disabled')}`);
      } else if (typeof option.value === 'number') {
        // Display number options with range
        const valueBar = this.generateValueBar(option.value, option.min, option.max, 20);
        console.log(`${prefix}${option.name}: ${option.value} ${valueBar}`);
      } else {
        // Display string options
        console.log(`${prefix}${option.name}: ${option.value}`);
      }
      
      if (isSelected) {
        console.log(`    ${colors.dim(option.description)}`);
      }
    });
    
    console.log('');
    console.log(colors.dim('Tip: Use left/right arrows to change the selected setting'));
  }
  
  // Helper to generate a visual value bar for numeric settings
  generateValueBar(value, min, max, length) {
    const percentage = (value - min) / (max - min);
    const barLength = Math.floor(percentage * length);
    
    let bar = '[';
    for (let i = 0; i < length; i++) {
      bar += i < barLength ? '■' : '□';
    }
    bar += ']';
    
    return colors.cyan(bar);
  }
  
  // New method to handle configuration menu navigation
  async handleConfigKeys(rl) {
    this.configMode = true;
    this.selectedConfigIndex = 0;
    
    // Enable raw mode to capture key presses
    readline.emitKeypressEvents(rl.input);
    if (rl.input.isTTY) {
      rl.input.setRawMode(true);
    }
    
    this.displayConfigMenu();
    
    return new Promise((resolve) => {
      const keyHandler = async (str, key) => {
        if (key.name === 'escape' || (key.name === 'c' && key.ctrl)) {
          // Exit config mode
          rl.input.removeListener('keypress', keyHandler);
          if (rl.input.isTTY) {
            rl.input.setRawMode(false);
          }
          console.clear();
          this.configMode = false;
          resolve();
          return;
        }
        
        if (key.name === 'up' && this.selectedConfigIndex > 0) {
          this.selectedConfigIndex--;
        } else if (key.name === 'down' && this.selectedConfigIndex < this.configOptions.length - 1) {
          this.selectedConfigIndex++;
        } else if (key.name === 'left' || key.name === 'right') {
          // Change the value of the selected option
          const option = this.configOptions[this.selectedConfigIndex];
          
          if (typeof option.value === 'boolean') {
            // Toggle boolean value
            option.value = !option.value;
          } else if (typeof option.value === 'number') {
            // Increment/decrement numeric value within bounds
            const step = option.step || 1;
            if (key.name === 'left' && option.value > option.min) {
              option.value = Math.max(option.min, option.value - step);
            } else if (key.name === 'right' && option.value < option.max) {
              option.value = Math.min(option.max, option.value + step);
            }
          }
        } else if (key.name === 'return') {
          // Save configuration and exit
          rl.input.removeListener('keypress', keyHandler);
          if (rl.input.isTTY) {
            rl.input.setRawMode(false);
          }
          console.clear();
          this.configMode = false;
          console.log(colors.green('Configuration saved!'));
          resolve();
          return;
        }
        
        this.displayConfigMenu();
      };
      
      rl.input.on('keypress', keyHandler);
    });
  }
}

class ReplView {
  constructor(state) {
    this.state = state;
  }
  
  // Methods for displaying REPL UI could be added here
}

async function main(config) {
  const root = process.cwd();
  const state = new ReplState(root, config);
  const view = new ReplView(state);
  await state.scan();

  console.log(colors.green(`\n==[]DungeonREPL@${root}`));
  console.log(colors.cyan('Type "help" for commands or "menu" for interactive navigation.'));
  const promptStr = '$> ';
  const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
    prompt: promptStr,
    completer: (line) => {
      // Command completion
      const commands = [
        'scan', 'scan-workspace', 'scan-diff-block', 'scan-file-change',
        'scan-modules', 'scan-tasks', 'scan-task-js', 'scan-task-md',
        'eval-module', 'preapply-module', 'submit-module',
        'apply', 'submit-file', 'submit-diff', 'submit-load', 'status',
        'list', 'reset-file', 'reset-all', 'replace-all',
        'manifest', 'help', 'menu', 'config', 'exit'
      ];
      
      const hits = commands.filter((c) => c.startsWith(line));
      // Show all completions if none found
      return [hits.length ? hits : commands, line];
    }
  });
  
  // Enable history and arrow key navigation
  let history = [];
  let historyCursor = -1;
  let inputBuffer = '';
  let tabCompletions = [];
  let tabIndex = -1;
  
  readline.emitKeypressEvents(process.stdin);
  if (process.stdin.isTTY) {
    process.stdin.setRawMode(true);
  }
  
  process.stdin.on('keypress', (str, key) => {
    // Skip if in menu mode
    if (state.menuMode || state.configMode) return;
    
    // Handle tab completion
    if (key.name === 'tab') {
      // If this is the first tab, generate completions
      if (tabIndex === -1) {
        const line = rl.line.slice(0, rl.cursor);
        const commandPart = line.split(/\s+/)[0]; // Get first word
        
        // If we're completing a command (no space in input yet)
        if (!line.includes(' ')) {
          const commands = [
            'scan', 'scan-workspace', 'scan-diff-block', 'scan-file-change',
            'scan-modules', 'scan-tasks', 'scan-task-js', 'scan-task-md',
            'eval-module', 'preapply-module', 'submit-module',
            'apply', 'submit-file', 'submit-diff', 'submit-load', 'status',
            'list', 'reset-file', 'reset-all', 'replace-all',
            'manifest', 'help', 'menu', 'config', 'exit'
          ];
          
          tabCompletions = commands.filter(c => c.startsWith(line));
        } 
        // If we're completing a module name
        else if (['eval-module', 'preapply-module', 'submit-module'].includes(commandPart)) {
          const moduleArg = line.split(/\s+/)[1] || '';
          tabCompletions = state.modules.map(m => m.name).filter(m => m.startsWith(moduleArg));
        }
        
        if (tabCompletions.length > 0) {
          tabIndex = 0;
          // Replace current input with the first completion
          const completion = tabCompletions[tabIndex];
          const restOfLine = rl.line.slice(rl.cursor);
          
          if (!line.includes(' ')) {
            // Completing a command
            rl.line = completion + restOfLine;
            rl.cursor = completion.length;
          } else {
            // Completing an argument
            const parts = line.split(/\s+/);
            parts.pop(); // Remove the incomplete argument
            parts.push(completion); // Add the completed argument
            rl.line = parts.join(' ') + restOfLine;
            rl.cursor = parts.join(' ').length;
          }
          
          rl._refreshLine();
        }
      } 
      // If tab is pressed again, cycle through completions
      else if (tabCompletions.length > 0) {
        tabIndex = (tabIndex + 1) % tabCompletions.length;
        const completion = tabCompletions[tabIndex];
        
        // Similar logic to replace just the relevant part
        const beforeCursor = rl.line.slice(0, rl.cursor);
        const afterCursor = rl.line.slice(rl.cursor);
        
        if (!beforeCursor.includes(' ')) {
          // Completing a command
          rl.line = completion + afterCursor;
          rl.cursor = completion.length;
        } else {
          // Completing an argument
          const parts = beforeCursor.split(/\s+/);
          parts.pop(); // Remove the incomplete argument
          parts.push(completion); // Add the completed argument
          rl.line = parts.join(' ') + afterCursor;
          rl.cursor = parts.join(' ').length;
        }
        
        rl._refreshLine();
      }
      return;
    }
    
    // Reset tab completion state for non-tab keys
    if (key.name !== 'tab') {
      tabIndex = -1;
      tabCompletions = [];
    }
    
    if (key.name === 'up' && !key.ctrl) {
      // Navigate history up
      if (history.length > 0 && historyCursor < history.length - 1) {
        if (historyCursor === -1) {
          inputBuffer = rl.line;
        }
        historyCursor++;
        rl.line = history[history.length - 1 - historyCursor];
        rl.cursor = rl.line.length;
        rl._refreshLine();
      }
    } else if (key.name === 'down' && !key.ctrl) {
      // Navigate history down
      if (historyCursor > -1) {
        historyCursor--;
        if (historyCursor === -1) {
          rl.line = inputBuffer;
        } else {
          rl.line = history[history.length - 1 - historyCursor];
        }
        rl.cursor = rl.line.length;
        rl._refreshLine();
      }
    } else if (key.name === 'left' && key.ctrl) {
      // Move cursor to the beginning of the previous word
      let i = rl.cursor - 1;
      while (i >= 0 && rl.line[i] === ' ') i--; // Skip spaces
      while (i >= 0 && rl.line[i] !== ' ') i--; // Skip non-spaces
      rl.cursor = i + 1;
      rl._refreshLine();
    } else if (key.name === 'right' && key.ctrl) {
      // Move cursor to the beginning of the next word
      let i = rl.cursor;
      while (i < rl.line.length && rl.line[i] === ' ') i++; // Skip spaces
      while (i < rl.line.length && rl.line[i] !== ' ') i++; // Skip non-spaces
      while (i < rl.line.length && rl.line[i] === ' ') i++; // Skip spaces again
      rl.cursor = i;
      rl._refreshLine();
    } else if (key.name === 'home') {
      // Move cursor to the beginning of the line
      rl.cursor = 0;
      rl._refreshLine();
    } else if (key.name === 'end') {
      // Move cursor to the end of the line
      rl.cursor = rl.line.length;
      rl._refreshLine();
    }
  });
  
  rl.prompt();

  rl.on('line', async line => {
    try {
      const trimmedLine = line.trim();
      if (trimmedLine !== '') {
        history.push(trimmedLine);
        historyCursor = -1;
        inputBuffer = '';
      }
      
      const [cmd, ...args] = trimmedLine.split(/\s+/);
      
      // 示例：处理配置相关的命令
      if (cmd === 'get-config') {
        const [moduleName] = args;
        if (moduleName) {
          console.log('Module config:', getModuleConfig(moduleName));
        } else {
          console.log('Global config:', getGlobalConfig());
        }
      } else if (cmd === 'update-config') {
        const [moduleName, key, value] = args;
        if (moduleName && key && value) {
          const moduleConfig = getModuleConfig(moduleName);
          moduleConfig.params[key] = value;
          updateModuleConfig(moduleName, moduleConfig);
          console.log('Module config updated:', getModuleConfig(moduleName));
        } else {
          console.log('Usage: update-config <moduleName> <key> <value>');
        }
      }
      
      // Handle menu command
      if (cmd === 'menu') {
        state.menuMode = true;
        const selectedCommand = await state.handleMenuKeys(rl);
        state.menuMode = false;
        if (selectedCommand) {
          // Clear the line and write the selected command
          rl.line = '';
          rl.cursor = 0;
          rl._refreshLine();
          console.log(`${promptStr}${selectedCommand}`);
          
          // Write the command but don't execute it
          rl.write(selectedCommand);
          
          // If the command includes argument placeholders (like <file>), 
          // position cursor at the start of the first argument
          if (selectedCommand.includes('<')) {
            const argPos = selectedCommand.indexOf('<');
            rl.cursor = argPos;
            rl._refreshLine();
          }
          
          rl.prompt();
          return;
        }
      }
      
      switch (cmd) {
        case 'scan':
          await state.scan();
          console.log('Scan complete.');
          break;
        case 'scan-workspace': {
          const res = await scan_workspace(state.root);
          const lines = format_scan_results(res, state.root);
          lines.forEach(l => console.log(l));
          break;
        }
        case 'scan-diff-block': {
          const diffBlocks = await scan_diff_block(state.root);
          const diffLines = format_diff_blocks(diffBlocks);
          diffLines.forEach(l => console.log(l));
          break;
        }
        case 'scan-modules': {
          const mods = await scan_modules(state.root);
          const lines = format_manifest_results(mods);
          lines.forEach(l => console.log(l));
          break;
        }
        case 'scan-tasks': {
          // Combine JS and Markdown tasks
          const rawJs = await scan_task_js(state.root);
          const jsTasks = Array.isArray(rawJs) ? rawJs.flat() : rawJs;
          const rawMd = await scan_task_md(state.root);
          const mdTasks = [];
          rawMd.forEach(col => {
            Object.values(col).forEach(arr => {
              if (Array.isArray(arr)) mdTasks.push(...arr);
            });
          });
          const allTasks = [...jsTasks, ...mdTasks];
          const taskLines = format_ops(allTasks, 5);
          taskLines.forEach(l => console.log(l));
          break;
        }
        case 'scan-file-change': {
          state.list();
          break;
        }

        case 'submit-file': {
          const [file, ...rest] = args;
          if (!file || rest.length === 0) {
            log.warn('Usage: submit-file <file> <content>');
            break;
          }
          try {
            const content = rest.join(' ').replace(/\\n/g, '\n');
            await submit_file(file, content);
            log.success(`submit-file: wrote to '${file}'`);
          } catch (err) {
            log.error(`submit-file error: ${err.message || err}`);
          }
          break;
        }
        case 'reset-file': {
          const file = args[0];
          if (!file) {
            log.warn('Usage: reset-file <file>');
            break;
          }
          try {
            await reset_file(file);
            log.success(`reset-file: '${file}' reset`);
          } catch (err) {
            log.error(`reset-file error: ${err.message || err}`);
          }
          break;
        }
        case 'reset-all': {
          try {
            await reset_all(state.root);
            log.success(`reset-all: all diffs and files reset`);
          } catch (err) {
            log.error(`reset-all error: ${err.message || err}`);
          }
          break;
        }
        case 'submit-diff': {
          const [file2, startStr2, endStr2, ...rest2] = args;
          if (!file2 || !startStr2 || !endStr2 || rest2.length === 0) {
            log.warn('Usage: submit-diff <file> <start> <end> <content>');
            break;
          }
          try {
            const start = parseInt(startStr2, 10);
            const end = parseInt(endStr2, 10);
            const content2 = rest2.join(' ').replace(/\\n/g, '\n');
            await submit_diff(file2, { start:start+1, end, content: content2 });
            log.success(`submit-diff applied to '${file2}'`);
          } catch (err) {
            log.error(`submit-diff error: ${err.message || err}`);
          }
          break;
        }
        case 'submit-load': {
          const [src, dest] = args;
          if (!src || !dest) {
            log.warn('Usage: submit-load <src> <dest>');
            break;
          }
          try {
            await submit_load(src, dest);
            log.success(`submit-load: '${src}' -> '${dest}' recorded`);
          } catch (err) {
            log.error(`submit-load error: ${err.message || err}`);
          }
          break;
        }
        case 'eval-module': {
          const alias = args[0];
          const stateArgs = args.slice(1);
          if (!alias) {
            console.log('Usage: eval-module <moduleName> [stateArg...]');
            break;
          }
          const mods = state.modules;
          const matches = fuzzyFindModule(mods, alias);
          if (matches.length === 0) {
            console.log(`Module '${alias}' not found.`);
            break;
          }
          if (matches.length > 1) {
            console.log(`Multiple modules match '${alias}': ${matches.map(m => m.name).join(', ')}; using first.`);
          }
          const mod = matches[0];
          const taskSet = state.taskSet;
          const evalPlan = evaluate_module(mod, stateArgs, taskSet);
          console.log(`\n=== Evaluation for module '${alias}' with state [${stateArgs.join(', ')}] ===`);
          evalPlan.forEach((p, i) => {
            console.log(`Batch ${i + 1}: ${p.batch.map(w => '.' + w).join(', ')}`);
            if (p.inline && p.inline.length) {
              console.log('  Inline tasks:');
              p.inline.forEach(t => console.log(`    - #${t.identifier}`));
            }
            if (p.tasks && p.tasks.length) {
              console.log('  Tasks:');
              p.tasks.forEach(t => console.log(`    - #${t.identifier}`));
            }
          });
          break;
        }
        case 'preapply-module': {
          const alias = args[0];
          const stateArgs = args.slice(1);
          if (!alias) {
            console.log('Usage: preapply-module <moduleName> [stateArg...]');
            break;
          }
          const mods2 = state.modules;
          const matches2 = fuzzyFindModule(mods2, alias);
          if (matches2.length === 0) {
            console.log(`Module '${alias}' not found.`);
            break;
          }
          if (matches2.length > 1) {
            console.log(`Multiple modules match '${alias}': ${matches2.map(m => m.name).join(', ')}; using first.`);
          }
          const mod2 = matches2[0];
          const batchResults = preapply_module(mod2, stateArgs, state.taskSet);
          batchResults.forEach(({ batch, merged }) => {
            console.log(`Batch: ${batch.map(b => '.' + b).join(', ')}`);
            const lines2 = format_preapply_tasks(merged, 5);
            lines2.forEach(line => console.log(line));
          });
          break;
        }
        case 'sm':
        case 'submit-module': {
          const alias = args[0];
          const stateArgs = args.slice(1);
          if (!alias) {
            log.warn('Usage: submit-module <moduleName> [stateArg...]');
            break;
          }
          try {
            const scanresult = { locations: state.locations, file_changes: state.file_changes, templates: state.templates };
            const mods = state.modules;
            const matches = fuzzyFindModule(mods, alias);
            if (matches.length === 0) {
              log.warn(`Module '${alias}' not found.`);
              break;
            }
            if (matches.length > 1) {
              log.warn(`Multiple modules match '${alias}': ${matches.map(m => m.name).join(', ')}; using first.`);
            }
            const mod = matches[0];
            const taskSet = state.taskSet;
            const results = await apply_module(state.root, scanresult, mod, stateArgs, taskSet);
            log.success(`submit-module: '${alias}' applied`);
            const lines = format_change_results(results, 10);
            lines.forEach(line => log.info(line));
          } catch (err) {
            log.error(`submit-module error: ${err.message || err}`);
          }
          break;
        }

        case 'help': {
          const lines = format_help();
          const pageSize = 10;
          // Collect all section headings for navigation
          const allSections = lines.filter(l => /^\S.*:$/.test(l)).map(l => l.replace(/:$/, ''));
          let cur = 0;
          const totalPages = Math.ceil(lines.length / pageSize);
          // 启用原始模式以捕获按键
          readline.emitKeypressEvents(rl.input);
          rl.input.setRawMode(true);

          console.log('Help: 使用 ← 上一页，→ 下一页，q 退出帮助');
          while (true) {
            console.log("\n===============================================");
            // navigation bar with current section highlighted
            const helpIndexStart = cur * pageSize;
            let currentSection = allSections[0];
            for (let i = helpIndexStart; i >= 0; i--) {
              if (/^\S.*:$/.test(lines[i])) {
                currentSection = lines[i].replace(/:$/, '');
                break;
              }
            }
            const nav = allSections.map(s => s === currentSection ? `[${s}]` : s).join('--');
            console.log(nav);
            console.log(`=== Help Page ${cur + 1}/${totalPages} === (← 上一页 | → 下一页 | q 退出)`);
            const pageLines = lines.slice(cur * pageSize, (cur + 1) * pageSize);
            pageLines.forEach(line => console.log(line));
            const key = await new Promise(resolve => {
              rl.input.once('keypress', (_str, key) => resolve(key));
            });
            if (key.name === 'right' && cur < totalPages - 1) {
              cur++;
            } else if (key.name === 'left' && cur > 0) {
              cur--;
            } else if (key.name === 'q' || key.name === 'escape') {
              break;
            }
          }
          // 恢复非原始模式
          rl.input.setRawMode(false);
          break;
        }
        case 'exit':
          rl.close();
          return;
        case 'config': {
          await state.handleConfigKeys(rl);
          break;
        }
        default:
          console.log(`Unknown command: '${cmd}'. Type 'help' for commands or 'menu' for interactive menu.`);
      }
    } catch (err) {
      console.error(colors.red('Error:'), err.message || err);
    }
    rl.prompt();
  }).on('close', () => {
    if (process.stdin.isTTY) {
      process.stdin.setRawMode(false);
    }
    console.log('Goodbye!');
    process.exit(0);
  });

  // Process initial CLI arguments as a single REPL command
  const initArgs = process.argv.slice(2);
  if (initArgs.length) {
    // Execute the provided command with its parameters
    rl.write(initArgs.join(' ') + '\n');
  }

  // 示例：在 REPL 中直接使用配置函数
  console.log('Global config:', getGlobalConfig());
  console.log('Module config for example-module:', getModuleConfig('example-module'));
}

export const startup = main;