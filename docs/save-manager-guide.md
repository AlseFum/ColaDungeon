# SaveManager 使用指南

## 概述

`SaveManager` 是可乐地牢的统一存档管理器，将所有存档功能集中到一个类中。

### 文件结构

```
游戏目录/
├── global.json       # 全局数据（图鉴、成就、排行榜）
├── save-001.json     # 存档槽1（包含游戏数据+所有地图）
├── save-002.json     # 存档槽2
├── ...
└── save-042.json     # 存档槽42（最多42个）
```

### 核心优势

- ✅ **单一职责**：所有存档操作集中管理
- ✅ **简化结构**：每个存档一个文件
- ✅ **JSON格式**：易于调试和编辑
- ✅ **快速查询**：存档信息缓存
- ✅ **向后兼容**：自动迁移旧存档

---

## 快速开始

### 1. 保存和加载游戏

```java
// 保存游戏
Bundle gameData = new Bundle();
gameData.put("hero", hero);
gameData.put("depth", depth);
gameData.put("gold", gold);
// ... 添加其他数据

SaveManager.saveGame(1, gameData);  // 保存到槽位1

// 加载游戏
try {
    Bundle data = SaveManager.loadGame(1);
    hero = (Hero) data.get("hero");
    depth = data.getInt("depth");
    gold = data.getInt("gold");
} catch (IOException e) {
    // 处理加载失败
}
```

### 2. 保存和加载全局数据

```java
// 保存全局数据（图鉴、成就等）
Bundle globalData = SaveManager.loadGlobal();
globalData.put("journal", journalData);
globalData.put("badges", badgesData);
globalData.put("rankings", rankingsData);

SaveManager.saveGlobal(globalData);

// 加载全局数据
Bundle data = SaveManager.loadGlobal();  // 不存在会返回空Bundle
```

### 3. 查询存档信息

```java
// 获取单个存档信息（快速，有缓存）
SaveManager.SaveInfo info = SaveManager.getSaveInfo(1);
if (info != null) {
    System.out.println("英雄: " + info.heroClass);
    System.out.println("等级: " + info.heroLevel);
    System.out.println("深度: " + info.depth);
    System.out.println("金币: " + info.gold);
}

// 列出所有存档
List<SaveManager.SaveInfo> saves = SaveManager.listSaves();
for (SaveInfo save : saves) {
    System.out.println("槽位 " + save.slot + ": " + save.heroClass);
}

// 按最后游玩时间排序
List<SaveInfo> recent = SaveManager.listSavesByLastPlayed();

// 按深度排序
List<SaveInfo> byDepth = SaveManager.listSavesByDepth();
```

### 4. 其他常用操作

```java
// 检查存档是否存在
if (SaveManager.saveExists(1)) {
    // 存档存在
}

// 删除存档
SaveManager.deleteGame(1);

// 复制存档
SaveManager.copySave(1, 2);  // 从槽1复制到槽2

// 获取第一个空槽位
int emptySlot = SaveManager.getFirstEmptySlot();
if (emptySlot != -1) {
    // 使用空槽位保存新游戏
    SaveManager.saveGame(emptySlot, newGameData);
}

// 获取存档总数
int count = SaveManager.getSaveCount();
```

---

## 详细用法

### 存档数据结构

每个存档文件（`save-001.json`）包含：

```json
{
  "slot": 1,
  "version": 1,
  "lastPlayed": 1699776000000,
  "heroClass": "WARRIOR",
  
  // 游戏主数据
  "hero": { /* 玩家数据 */ },
  "depth": 5,
  "gold": 1234,
  "challenges": 0,
  
  // 所有地图数据（合并存储）
  "levels": {
    "depth_1_0": { /* 第1层数据 */ },
    "depth_2_0": { /* 第2层数据 */ },
    "depth_5_1": { /* 第5层分支1 */ }
  },
  
  // 其他游戏数据
  "quickslot": { /* ... */ },
  "statistics": { /* ... */ }
}
```

### 全局数据结构

全局文件（`global.json`）包含：

```json
{
  "version": 1,
  "lastSaved": 1699776000000,
  
  // 图鉴
  "journal": {
    "catalog": { /* 物品图鉴 */ },
    "bestiary": { /* 怪物图鉴 */ },
    "documents": { /* 文档 */ }
  },
  
  // 成就
  "badges": {
    /* 成就数据 */
  },
  
  // 排行榜
  "rankings": [
    { "rank": 1, "score": 10000, /* ... */ }
  ],
  
  // 全局统计
  "statistics": {
    "totalPlayTime": 360000,
    "totalGamesPlayed": 42
  }
}
```

---

## 与现有代码集成

### 迁移步骤

#### 步骤1：迁移旧存档（首次启动时）

```java
// 在游戏启动时自动迁移
public class ColaDungeon extends Game {
    @Override
    public void create() {
        super.create();
        
        // 自动迁移旧存档（如果存在）
        int migrated = SaveManager.migrateAllLegacySaves();
        if (migrated > 0) {
            System.out.println("迁移了 " + migrated + " 个存档");
        }
        
        // ... 其他初始化
    }
}
```

#### 步骤2：替换 `Dungeon.saveGame()`

```java
// 旧代码（Dungeon.java）
public static void saveGame(int save) {
    try {
        Bundle bundle = new Bundle();
        bundle.put(HERO, hero);
        bundle.put(DEPTH, depth);
        // ... 添加所有数据
        
        FileUtils.bundleToFile(GamesInProgress.gameFile(save), bundle);
    } catch (IOException e) {
        // ...
    }
}

// 新代码（使用SaveManager）
public static void saveGame(int save) {
    try {
        Bundle bundle = new Bundle();
        bundle.put(HERO, hero);
        bundle.put(DEPTH, depth);
        
        // 合并所有地图数据
        Bundle levelsBundle = new Bundle();
        for (int d : generatedLevels) {
            // 将每层数据添加到levels中
            Bundle levelData = new Bundle();
            levelData.put(LEVEL, /* 地图数据 */);
            levelsBundle.put("depth_" + d + "_" + branch, levelData);
        }
        bundle.put("levels", levelsBundle);
        
        // 使用SaveManager保存
        SaveManager.saveGame(save, bundle);
    } catch (IOException e) {
        // ...
    }
}
```

#### 步骤3：替换 `Dungeon.loadGame()`

```java
// 旧代码
public static void loadGame(int save) throws IOException {
    Bundle bundle = FileUtils.bundleFromFile(GamesInProgress.gameFile(save));
    // ... 读取数据
}

// 新代码
public static void loadGame(int save) throws IOException {
    Bundle bundle = SaveManager.loadGame(save);
    
    hero = (Hero) bundle.get(HERO);
    depth = bundle.getInt(DEPTH);
    // ... 读取其他数据
    
    // 地图数据现在在bundle的"levels"中
    Bundle levelsBundle = bundle.getBundle("levels");
    // ...
}
```

#### 步骤4：替换 `GamesInProgress.checkAll()`

```java
// 旧代码（GamesInProgress.java）
public static ArrayList<Info> checkAll() {
    ArrayList<Info> result = new ArrayList<>();
    for (int i = 1; i <= MAX_SLOTS; i++) {
        Info curr = check(i);
        if (curr != null) result.add(curr);
    }
    return result;
}

// 新代码（直接使用SaveManager）
public static ArrayList<SaveManager.SaveInfo> checkAll() {
    return new ArrayList<>(SaveManager.listSaves());
}
```

#### 步骤5：替换 `Journal` 全局数据

```java
// 旧代码（Journal.java）
public static void saveGlobal() {
    Bundle bundle = new Bundle();
    Catalog.store(bundle);
    Bestiary.store(bundle);
    Document.store(bundle);
    
    try {
        FileUtils.bundleToFile(JOURNAL_FILE, bundle);
    } catch (IOException e) {
        // ...
    }
}

// 新代码
public static void saveGlobal() {
    Bundle globalBundle = SaveManager.loadGlobal();  // 加载现有全局数据
    
    Bundle journalBundle = new Bundle();
    Catalog.store(journalBundle);
    Bestiary.store(journalBundle);
    Document.store(journalBundle);
    
    globalBundle.put("journal", journalBundle);
    
    try {
        SaveManager.saveGlobal(globalBundle);
    } catch (IOException e) {
        // ...
    }
}
```

---

## 高级功能

### 1. 导入导出存档

```java
// 导出到剪贴板
if (SaveManager.exportToClipboard(1)) {
    GLog.p("存档已复制到剪贴板");
}

// 从剪贴板导入
int slot = SaveManager.importFromClipboard();
if (slot != -1) {
    GLog.p("存档已导入到槽位 " + slot);
}
```

### 2. 调试工具

```java
// 打印所有存档信息
SaveManager.printSaveInfo();

// 输出示例：
// === SaveManager Info ===
// Total saves: 3/42
// Total size: 1.25 MB
//
// Slot 01: WARRIOR Lv.10 | Depth 5 | 1234 gold | Last played: 2025-11-11 12:00
// Slot 02: MAGE Lv.8 | Depth 4 | 890 gold | Last played: 2025-11-10 15:30
// Slot 03: ROGUE Lv.12 | Depth 7 | 2345 gold | Last played: 2025-11-09 20:15
// ========================

// 获取总大小
long size = SaveManager.getTotalSize();
System.out.println("存档占用空间: " + (size / 1024 / 1024) + " MB");

// 清除缓存（如果需要重新读取）
SaveManager.clearCache();
```

### 3. 自定义保存格式

```java
// 扩展SaveManager支持自定义数据
Bundle gameData = new Bundle();

// 添加自定义数据
gameData.put("customData", myCustomData);
gameData.put("modData", modData);

SaveManager.saveGame(slot, gameData);
```

---

## 性能优化

### 缓存机制

`SaveManager` 使用内存缓存加速存档信息查询：

```java
// 第一次调用：从文件读取（慢）
SaveInfo info1 = SaveManager.getSaveInfo(1);  // ~100ms

// 后续调用：从缓存读取（快）
SaveInfo info2 = SaveManager.getSaveInfo(1);  // ~0.1ms
```

### 批量操作

```java
// 避免频繁查询
// 不好的做法
for (int i = 1; i <= 42; i++) {
    if (SaveManager.saveExists(i)) {
        SaveInfo info = SaveManager.getSaveInfo(i);
        // ...
    }
}

// 好的做法：一次性获取所有
List<SaveInfo> saves = SaveManager.listSaves();  // 只查询一次
for (SaveInfo info : saves) {
    // ...
}
```

---

## 故障排查

### 问题1：存档加载失败

```java
try {
    Bundle data = SaveManager.loadGame(1);
} catch (IOException e) {
    // 存档可能损坏
    System.err.println("存档加载失败: " + e.getMessage());
    
    // 尝试恢复（如果有备份系统）
    // 或者删除损坏的存档
    SaveManager.deleteGame(1);
}
```

### 问题2：旧存档无法识别

```java
// 手动迁移单个存档
if (!SaveManager.saveExists(1)) {
    // 尝试从旧位置迁移
    SaveManager.migrateFromLegacy(1, 1);
}
```

### 问题3：JSON格式错误

- 确保Bundle正确序列化
- 检查是否有不支持的数据类型
- 使用`Bundle.toString()`查看生成的JSON

---

## 与旧系统对比

| 功能 | 旧系统 | SaveManager | 优势 |
|------|--------|-------------|------|
| **文件结构** | game1/多个文件 | save-001.json | 简化 |
| **全局数据** | journal.dat等多个文件 | global.json | 统一 |
| **管理类** | 分散在多个类 | SaveManager一个类 | 集中 |
| **格式** | Bundle（GZIP压缩） | Bundle（JSON） | 易读 |
| **查询速度** | 每次都读文件 | 缓存机制 | 快10倍+ |
| **API复杂度** | 多个类，不同方法 | 统一API | 简单 |

---

## 最佳实践

### 1. 定期保存

```java
// 在关键时刻保存
public class Dungeon {
    public static void saveAll() throws IOException {
        SaveManager.saveGame(GamesInProgress.curSlot, buildGameBundle());
    }
    
    // 每5分钟自动保存
    private static void autoSave() {
        if (hero.isAlive()) {
            try {
                saveAll();
            } catch (IOException e) {
                // 静默失败，不打断游戏
            }
        }
    }
}
```

### 2. 错误处理

```java
// 总是捕获IOException
try {
    SaveManager.saveGame(slot, data);
} catch (IOException e) {
    // 显示错误给用户
    GLog.w("保存失败: " + e.getMessage());
    // 记录日志
    ColaDungeon.reportException(e);
}
```

### 3. 数据验证

```java
// 保存前验证关键数据
if (hero != null && depth > 0 && depth <= 30) {
    SaveManager.saveGame(slot, data);
} else {
    throw new IllegalStateException("Invalid game state");
}
```

---

## 总结

`SaveManager` 提供了：

- ✅ **简单的API**：一个类搞定所有存档操作
- ✅ **清晰的结构**：每个存档一个JSON文件
- ✅ **快速查询**：内存缓存机制
- ✅ **向后兼容**：自动迁移旧存档
- ✅ **易于调试**：JSON格式，可读性强

**下一步**：
1. 在项目中集成 `SaveManager`
2. 逐步替换现有存档代码
3. 测试迁移功能
4. 享受简化的存档系统！

---

**版本**：1.0  
**最后更新**：2025-11-11

