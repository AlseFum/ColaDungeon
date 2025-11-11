# SaveManager 速查表

## 文件结构

```
游戏目录/
├── global.json       # 全局数据（图鉴、成就、排行榜）
├── save-001.json     # 存档1（包含所有数据）
├── save-002.json     # 存档2
└── ...
```

---

## 常用操作

### 保存/加载游戏

```java
// 保存
SaveManager.saveGame(slot, bundle);

// 加载
Bundle data = SaveManager.loadGame(slot);

// 删除
SaveManager.deleteGame(slot);

// 复制
SaveManager.copySave(fromSlot, toSlot);

// 检查是否存在
boolean exists = SaveManager.saveExists(slot);
```

### 全局数据

```java
// 保存全局数据
SaveManager.saveGlobal(bundle);

// 加载全局数据
Bundle data = SaveManager.loadGlobal();

// 检查
boolean exists = SaveManager.globalExists();
```

### 查询存档

```java
// 获取存档信息（快速）
SaveInfo info = SaveManager.getSaveInfo(slot);

// 列出所有存档
List<SaveInfo> saves = SaveManager.listSaves();

// 按时间排序
List<SaveInfo> recent = SaveManager.listSavesByLastPlayed();

// 按深度排序
List<SaveInfo> byDepth = SaveManager.listSavesByDepth();

// 获取空槽位
int empty = SaveManager.getFirstEmptySlot();

// 获取总数
int count = SaveManager.getSaveCount();
```

### 导入导出

```java
// 导出到剪贴板
SaveManager.exportToClipboard(slot);

// 从剪贴板导入
int newSlot = SaveManager.importFromClipboard();
```

---

## SaveInfo 属性

```java
SaveInfo info = SaveManager.getSaveInfo(1);

info.slot;         // 槽位编号
info.heroClass;    // 英雄职业
info.heroLevel;    // 英雄等级
info.depth;        // 当前深度
info.gold;         // 金币数
info.playTime;     // 游戏时长（毫秒）
info.lastPlayed;   // 最后游玩时间（时间戳）
info.exists;       // 是否存在
```

---

## 迁移旧存档

```java
// 迁移单个存档
SaveManager.migrateFromLegacy(oldSlot, newSlot);

// 批量迁移（游戏启动时）
int count = SaveManager.migrateAllLegacySaves();
```

---

## 调试工具

```java
// 打印存档信息
SaveManager.printSaveInfo();

// 获取总大小
long size = SaveManager.getTotalSize();

// 清除缓存
SaveManager.clearCache();
```

---

## 数据结构

### 存档文件（save-001.json）

```json
{
  "slot": 1,
  "version": 1,
  "lastPlayed": 1699776000000,
  "heroClass": "WARRIOR",
  "hero": { },
  "depth": 5,
  "gold": 1234,
  "levels": {
    "depth_1_0": { },
    "depth_2_0": { }
  }
}
```

### 全局文件（global.json）

```json
{
  "version": 1,
  "lastSaved": 1699776000000,
  "journal": { },
  "badges": { },
  "rankings": [ ]
}
```

---

## 错误处理

```java
try {
    SaveManager.saveGame(slot, data);
} catch (IOException e) {
    // 处理保存失败
}

try {
    Bundle data = SaveManager.loadGame(slot);
} catch (IOException e) {
    // 处理加载失败（存档不存在或损坏）
}
```

---

## 性能提示

- ✅ `getSaveInfo()` 使用缓存，非常快
- ✅ `listSaves()` 一次性获取所有，比循环快
- ⚠️ `loadGame()` 会读取整个文件，较慢
- ⚠️ 避免频繁保存（每5分钟一次即可）

---

## 常量

```java
SaveManager.MAX_SLOTS = 42;  // 最大存档槽数
```

---

## 快速集成

### 步骤1：游戏启动时

```java
public void create() {
    // 迁移旧存档
    SaveManager.migrateAllLegacySaves();
}
```

### 步骤2：保存游戏

```java
public static void saveGame(int slot) {
    Bundle bundle = new Bundle();
    // ... 添加数据
    
    try {
        SaveManager.saveGame(slot, bundle);
    } catch (IOException e) {
        // 处理错误
    }
}
```

### 步骤3：加载游戏

```java
public static void loadGame(int slot) {
    try {
        Bundle bundle = SaveManager.loadGame(slot);
        // ... 读取数据
    } catch (IOException e) {
        // 处理错误
    }
}
```

### 步骤4：显示存档列表

```java
public void refreshSaveList() {
    List<SaveInfo> saves = SaveManager.listSavesByLastPlayed();
    
    for (SaveInfo info : saves) {
        String text = String.format("%s Lv.%d - Depth %d",
            info.heroClass, info.heroLevel, info.depth);
        // 显示到UI
    }
}
```

---

## 完整示例

```java
// 创建新游戏
int slot = SaveManager.getFirstEmptySlot();
if (slot != -1) {
    Bundle newGame = createNewGameBundle();
    SaveManager.saveGame(slot, newGame);
}

// 继续游戏
SaveInfo info = SaveManager.getSaveInfo(1);
if (info != null && info.exists) {
    Bundle data = SaveManager.loadGame(1);
    continueGame(data);
}

// 删除存档
if (SaveManager.saveExists(1)) {
    SaveManager.deleteGame(1);
}

// 复制存档（备份）
SaveManager.copySave(1, 2);

// 导出存档
if (SaveManager.exportToClipboard(1)) {
    showMessage("存档已复制到剪贴板");
}
```

---

**提示**：所有操作都会自动打印日志到控制台，方便调试。

