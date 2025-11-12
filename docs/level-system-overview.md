# 楼层系统现状梳理

本文记录 ColaDungeon 现有楼层（关卡）生成与切换机制，为后续扩展成图状结构提供参考。

---

## 1. 基础模型

- **深度 (`depth`)**：主线楼层编号，范围 `1..N`，每层对应一个 `Level`。
- **分支 (`branch`)**：支线标识，`0` 表示主线，`1..4` 表示不同的支线楼层（墓穴、挑战房、BOSS 入口等）。
- **楼层键值**：通过 `depth_branch` 组合定位楼层，例如 `depth_5_0`（主线第 5 层）、`depth_5_2`（第 5 层的第二个分支）。
- **存档结构**：在 `SaveManager` 中，各个楼层保存于 `Bundle levels` 中，对应键 `depth_<depth>_<branch>`。

整体结构仍是「线性主线 + 若干分支」，并非真正的图。

---

## 2. 关键数据

| 字段 | 说明 | 位置 |
|------|------|------|
| `Dungeon.depth` | 当前主线楼层编号 | `Dungeon` |
| `Dungeon.branch` | 当前分支编号 | `Dungeon` |
| `Dungeon.generatedLevels` | 已生成过的主线楼层序号，用于控制主题与掉落 | `Dungeon` |
| `Statistics.deepestFloor` | 玩家达成的最深楼层 | `Statistics` |
| `levels` | 存档中的楼层数据集合 | `SaveManager` -> `save-xxx.json` |

---

## 3. 存档读写流程

1. **保存**（`Dungeon.saveAll`）  
   - 创建楼层 `Bundle`，写入 `depth`、`branch`、`hero`、掉落状态等信息。  
   - 调用 `SaveManager.saveGame(slot, bundle)` 保存全局状态。  
   - `SaveManager.saveLevel(slot, depth, branch, levelBundle)` 将当前 `Level` 写入 `levels` 字典。

2. **加载**（`Dungeon.loadGame`）  
   - 读取 `SaveManager.loadGame(slot)`，恢复 `depth`、`branch` 等字段。  
   - `Dungeon.loadLevel` 根据键 `depth_<depth>_<branch>` 读取对应 `Level`。若不存在则生成新楼层。

---

## 4. 楼层转换机制

1. **触发入口/出口**：玩家与楼梯、传送门、井等交互。
2. **过场处理**：`InterlevelScene` 负责配置新楼层目标（上楼、下楼、支线入口等）。
3. **更新坐标**：  
   - 主线下楼：`depth++`，`branch = 0`。  
   - 主线上楼：`depth--`。  
   - 进入支线：设置 `branch = 1..4`，保存返回点。  
   - 离开支线：恢复到原主线 `depth`、`branch=0`。
4. **生成或读取楼层**：若 `levels` 中已有数据则反序列化，否则调用 `LevelFactory` 根据 `depth`/`branch` 生成，并记录到 `generatedLevels`。

辅助系统：
- `SpecialRoom` / `SecretRoom`：在生成时为特定 `depth` 注入特殊房间。
- `LimitedDrops`：按区域（每 5 层）控制掉落表循环使用。
- `Quest` 系列：在特定楼层插入任务房、传送门等支线。

---

## 5. 模型限制

Current limitations:
- `depth` 仍然线性递增，`branch` 数量固定，无法描述任意多分支或循环结构。
- 分支返回路径写死，不能形成独立的图节点或多入口。
- 存档键依赖 `depth` / `branch` 组合，难以表达非线性的楼层编号。
- `Statistics` / `Rankings` 等依赖深度对比，在图结构中可能需要新的评估方式。

---

## 6. 结论与后续方向

若要扩展为“图状楼层系统”，需要：
1. **节点 ID 重构**：为每个楼层节点分配独立 ID，取代 `depth`/`branch`。
2. **存档结构扩展**：存储邻接关系（多出口、多入口），记录当前节点与返回节点。
3. **楼层生成器改造**：根据图结构的拓扑构建 `Level`，并在传送后正确定位目标节点。
4. **UI 与逻辑调整**：更新 `InterlevelScene`、`GamesInProgress`、统计界面，使其理解图结构。

本文件仅用于描述现状，具体设计将在后续文档/方案中继续推进。

