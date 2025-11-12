# 贴图系统整合 TODO（实施清单）

目标：将物品与怪物的贴图系统统一为“静态（编译期 ID）+ 动态（运行期 key，强制 fallback）”的双层架构，旧代码可渐进迁移，动态资源必须提供 fallback，保证渲染稳定。

---

## A. 基础设施（Registry）

1) 实现统一入口（已创建，继续完善）
- [x] 新建 `com.coladungeon.sprites.SpriteRegistry`
  - [x] Items：`itemImageId(key, elseId)`、`itemMapping(key, elseId)`、`staticItemMapping(id)`
  - [x] Mobs：`registerMob(key, MobDef)`、`mobTextureOr(fallback, key)`、`mobFilm(key, fallback, w, h)`
  - [x] 任何动态查询都要求 fallback
- [ ] 增加错误贴图占位方案（fallback 也缺失时显示默认问号/红框）
- [ ] 增加日志/诊断：当 key 未命中或 fallback 也无效时打印一次性告警

2) 静态 Atlas 收敛
- [ ] Items：梳理常用 ID → `StaticAtlas`（内部仍复用 `ItemSpriteSheet`）
- [ ] Mobs：建立 `MobSpriteId` 表（ID → 纹理路径、帧大小、默认动画切片）
- [ ] 以工具方法暴露：`SpriteRegistry.staticItemMapping(id)`、`SpriteRegistry.mobFilm(null, Assets.Sprites.X, w, h)`

---

## B. 物品整合（不破坏旧逻辑）

3) 最小落地（不改现有静态调用）
- [ ] 在需要动态 key 的地方改用：
  - `image = SpriteRegistry.itemImageId("mod:key", ItemSpriteSheet.SWORD)`
  - 或 `SpriteRegistry.itemMapping("mod:key", ItemSpriteSheet.SWORD)`
- [ ] 校验：动态未命中 → 显示 `elseId` 静态贴图

4) 渐进替换（可选）
- [ ] 将局部使用 `ItemSpriteManager.getImageMapping(...)` 的代码替换为 `SpriteRegistry.itemMapping(...)`
- [ ] 在 `ItemSprite` 内部增加一个便捷入口（如需要）：`setByKey(key, elseId)`

5) 动态注册（mods）
- [ ] 保持 `ItemSpriteManager.registerTexture(...)` 兼容
- [ ] 增加文档强调：动态 key 使用方必须传 `elseId`

---

## C. 怪物整合（逐类替换）

6) 辅助方法（已添加）
- [x] 在 `MobSprite` 中新增：
  - `protected TextureFilm textureWithFallback(String key, Object fallbackTexture, int w, int h)`

7) 试点改造（挑 2-3 个）
- [ ] 将如 `RatSprite`、`GnollSprite`、`BatSprite` 构造函数改为：
  - `TextureFilm frames = textureWithFallback("mod:rat", Assets.Sprites.RAT, 16, 15)`
  - 其余动画逻辑不变
- [ ] 验证：不注册 key → 使用静态贴图；注册 key → 使用动态贴图

8) 批量迁移计划
- [ ] 扫描所有 `texture(Assets.Sprites.*)` 的 `MobSprite` 子类
- [ ] 批次改造，保证每处都具备 fallback
- [ ] 可选：为少数复杂 sprite 建立 `MobDef` 用于更丰富的动画元数据

---

## D. 动态注册与校验

9) 注册 API 与校验
- [ ] `SpriteRegistry.registerMob(key, def)`：在 `def` 上增加最小字段校验（尺寸>0、资源可加载）
- [ ] Items 动态注册沿用 `ItemSpriteManager`，在调用处统一通过 `SpriteRegistry` 访问

10) JSON/外部配置（可选）
- [ ] 设计简单 JSON：`{ "items": {...}, "mobs": {...} }`
- [ ] 启动时加载到 `SpriteRegistry`（mods 可复用）

---

## E. 测试矩阵

11) 基本用例
- [ ] 静态物品显示（旧 ID）：不回归
- [ ] 动态物品 key 命中：显示动态
- [ ] 动态物品 key 未命中：回退 elseId
- [ ] 动态怪物 key 命中：显示动态
- [ ] 动态怪物 key 未命中：回退静态纹理

12) 边界用例
- [ ] fallback 也缺失：显示错误占位/打印日志
- [ ] 多尺寸贴图（16/32/64）混用是否渲染正常
- [ ] 性能回归（启动、进入楼层时纹理加载）

---

## F. 文档与示例

13) Mod 开发示例
- [ ] 文档：如何注册一个自定义怪物贴图并提供 fallback
- [ ] 文档：如何在物品/UI 代码中通过 key 使用动态贴图
- [ ] 说明：动态必须提供 `elseId/elseTexture` 的原因与错误处理方式

14) 现有指南更新
- [ ] 更新 `docs/save-manager-guide.md` 相关示例（如引用贴图的地方）
- [ ] 新增 `docs/sprite-quick-start.md`（简洁示例）

---

## G. 推进顺序建议

Step 1（已做/继续）：落地 `SpriteRegistry` + `MobSprite.textureWithFallback`（基础功能齐备）  
Step 2：在一小批 mob 与 item 处实际替换，验证 fallback 正常  
Step 3：扩展注册 API 与日志/错误占位机制  
Step 4：批量迁移 mob sprite 类与若干 item 使用点  
Step 5：补齐文档、提供 mod 示例与测试用例  

---

## H. 相关文件（参考）

- `core/src/main/java/com/coladungeon/sprites/SpriteRegistry.java`（统一入口）
- `core/src/main/java/com/coladungeon/sprites/MobSprite.java`（`textureWithFallback`）
- `core/src/main/java/com/coladungeon/sprites/ItemSpriteSheet.java`（静态 item ID）
- `core/src/main/java/com/coladungeon/sprites/ItemSpriteManager.java`（动态 item 注册/查询）
- `SPD-classes/src/main/java/com/watabou/noosa/TextureFilm.java`（帧切片）

---

备注：本清单为执行手册，优先完成 A/B/C/9，保证架构到位与可用的 fallback，然后再逐步推广与补文档。*** End Patch



