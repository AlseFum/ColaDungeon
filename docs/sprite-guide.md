## 贴图与物品整合指南（Sprite + Item）

本指南介绍如何在当前整合后的系统中：
- 增加 sprite（静态与动态）
- 使用 sprite（在渲染与物品类中）
- 修改 sprite（替换图片、重新标注、动态生成）
- 与物品 Item 的对接方式

系统已统一到 `SpriteRegistry`，`ItemSpriteManager` 作为兼容层仍可用但仅做委托。所有新代码优先使用 `SpriteRegistry`。


### 名词说明
- 静态 item 贴图：在 `Assets.Sprites.ITEMS` 大图中按 16x16 切片，由 `ItemSpriteSheet` 提供 ID 常量及切片信息。
- 动态 item 贴图：在运行期注册，使用字符串 key（如 `mod:my_item`）访问，必须提供静态 fallback（后备）ID。
- 动态 mob 贴图：按 key 注册，使用 `TextureFilm` 切片，取不到 key 时使用 fallback 纹理与帧大小。


## 一、增加 sprite

### 1) 增加静态 item 贴图（编译期）
适用于合并到主物品大图 `Assets.Sprites.ITEMS` 的小图标（16x16）。

步骤：
1. 把素材整合到 `Assets.Sprites.ITEMS`（通常是打包在同一张 items 大图中）。
2. 在 `ItemSpriteSheet` 中为它分配一个整数 ID 并完成切片（若是全尺寸 16x16 通常无需额外代码）。
3. 在物品类中使用该 ID（见下文“与 Item 对接”）。

优点：加载简单、性能好；缺点：需要改大图与打包。适合核心物品。


### 2) 增加动态 item 贴图（运行期）
适用于独立纹理或 Mod 化内容，按 key 使用，且必须提供 fallback 静态 ID。

注册：在任意初始化时机（建议在游戏启动或 Mod 初始化）调用：

```java
import com.coladungeon.sprites.SpriteRegistry;

// 注册一张 16x16 的纹理并按顺序分配 ID 段
SpriteRegistry.registerItemTexture("my_mod/my_items.png", 16)
    .label("mod:my_item")       // 该 key 将映射到该纹理段的第一个格子
    .label("mod:my_item_alt");  // 第二个格子
```

使用时请通过 `SpriteRegistry` 的接口获取，并提供静态 ID 作为后备：

```java
// 仅需要一个整数 ID 的场景
int imageId = SpriteRegistry.itemImageId("mod:my_item", ItemSpriteSheet.SOMETHING);

// 需要贴图矩形等信息（例如自定义渲染）
SpriteRegistry.ImageMapping mapping =
        SpriteRegistry.itemMapping("mod:my_item", ItemSpriteSheet.SOMETHING);
```

注意：动态范围的内部 ID 从 120000 起按段分配，无需手动维护。


### 3) 增加动态 mob 贴图
注册：

```java
import com.coladungeon.sprites.SpriteRegistry;

SpriteRegistry.registerMob(
    "mod:slime_king",
    new SpriteRegistry.MobDef(Assets.Sprites.SLIME, 16, 16) // 可是路径或 Assets.Sprites 的句柄
);
```

使用（获取 `TextureFilm`，取不到 key 时回落到给定的纹理与帧尺寸）：

```java
TextureFilm film = SpriteRegistry.mobFilm(
    "mod:slime_king",
    Assets.Sprites.SLIME, // fallback 纹理
    16, 16                // fallback 帧大小
);
```


## 二、使用 sprite

### 1) 在 `ItemSprite` 中的自动处理
`ItemSprite` 会根据传入的 `image` 值自动处理：
- 若 `image` 在动态范围（>= 6000 的你的工程逻辑或 >= 114514 的扩展逻辑），会调用 `SpriteRegistry.mapItemImage(image)` 获取对应贴图与缩放。
- 否则按静态 `ItemSpriteSheet` 切片处理。

因此，你只需确保为物品提供正确的 `image`（见下文）。


### 2) 在自定义渲染中直接使用动态映射
当你需要获得贴图、矩形、缩放建议等：

```java
SpriteRegistry.ImageMapping map =
    SpriteRegistry.itemMapping("mod:my_item", ItemSpriteSheet.SOMETHING);

SmartTexture tex = map.texture;
RectF rect = map.rect;
int size = map.size; // 若不是 16 会建议缩放到 16 的比例
```


## 三、修改 sprite

### 1) 替换动态纹理文件
- 替换文件本身（如 `my_mod/my_items.png`）。
- 重新运行游戏或在支持的场景下刷新纹理缓存即可生效。

### 2) 重新标注/增删 key
- 在初始化注册处调整 `.label("key")` 的顺序与数量；
- key→ID 是顺序分配的，增删会改变后续 key 的动态 ID，请避免在上线后随意改变已有 key 的顺序。

### 3) 动态生成纹理
使用 `TextureBuilder` 即时创建纹理并返回 `ImageMapping`：

```java
import com.coladungeon.sprites.TextureBuilder;
import com.coladungeon.sprites.SpriteRegistry;

TextureBuilder builder = new TextureBuilder(16, 16);
builder.setColor(0xFFFF0000).fillCircle(8, 8, 6);
SpriteRegistry.ImageMapping map = builder.build(); // 直接拿到贴图使用
```

如需同时注册为某个 key，可参考 `DynamicSpriteExample` 或在外部把 key 与动态 ID 进行绑定（通常建议统一用 `registerItemTexture + label` 的方式来管理 key）。


## 四、与 Item 的对接

`ItemSprite` 的 `view(Item)` 会读取：
- `item.image()`：整数贴图 ID（可为静态 ID，也可用 `SpriteRegistry.itemImageId(key, fallbackId)` 获取）
- `item.glowing()`：决定发光效果
- `item.emitter()`：粒子发射器

最常见的做法是在物品类中设置 `image` 字段：

```java
import com.coladungeon.sprites.SpriteRegistry;
import com.coladungeon.sprites.ItemSpriteSheet;

public class MyItem extends Item {
    {
        // 动态 key，提供静态 fallback
        image = SpriteRegistry.itemImageId("mod:my_item", ItemSpriteSheet.SOMETHING);
    }
}
```

需要更细粒度控制时（如自定义显示），你也可以直接在渲染端使用 `SpriteRegistry.itemMapping(...)` 获取 `ImageMapping` 并手动绘制。


## 五、常见问题与建议

- 一定要提供 fallback：所有基于 key 的动态获取均应带上静态 fallback ID（items）或纹理与帧大小（mobs），保证运行稳定性。
- 迁移旧代码：旧有使用 `ItemSpriteManager` 的代码仍可工作，它现在是对 `SpriteRegistry` 的轻量代理。新代码建议直接使用 `SpriteRegistry`。
- 动态 ID 的范围：内部从 120000 起按段分配，你无需关心具体值，仅需确保 key 唯一。
- 诊断与占位：建议在美术资源未准备好时先使用静态占位图（如 `ItemSpriteSheet.SOMETHING`）。


## 六、API 速查

- Items（动态注册与查询）
  - 注册：`SpriteRegistry.registerItemTexture(path, size).label(key)`
  - 取 ID：`SpriteRegistry.itemImageId(key, elseId)`
  - 取映射：`SpriteRegistry.itemMapping(key, elseId)`
  - 动态整型映射：`SpriteRegistry.mapItemImage(imageInt)`

- Items（静态）
  - `ItemSpriteSheet`（ID 与切片）
  - 静态映射：`SpriteRegistry.staticItemMapping(id)`

- Mobs（动态）
  - 注册：`SpriteRegistry.registerMob(key, new MobDef(texture, w, h))`
  - 取 Film：`SpriteRegistry.mobFilm(key, fallbackTexture, w, h)`

- 动态生成：
  - `TextureBuilder`：`new TextureBuilder(w,h).fillRect(...).build()`


## 七、示例：给枪械与枪口火焰增加动态贴图

注册（一次性初始化即可）：

```java
SpriteRegistry.registerItemTexture("sprites/gun.png", 16).label("gun");
SpriteRegistry.registerItemTexture("effects/gunfire.png", 16).label("gunfire");
```

在物品类中使用：

```java
public class Gun extends Item {
    {
        image = SpriteRegistry.itemImageId("gun", ItemSpriteSheet.SOMETHING);
    }
}
```

在开火效果中取 `ImageMapping`：

```java
SpriteRegistry.ImageMapping fire = SpriteRegistry.itemMapping("gunfire", ItemSpriteSheet.SOMETHING);
// 使用 fire.texture 与 fire.rect 渲染枪口火焰
```


