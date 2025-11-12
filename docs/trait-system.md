## Trait 系统使用说明

本系统为可扩展的“特质”机制，支持：
- 不限量的 Trait，运行时动态声明/更新
- 每个 Trait 可包含任意键值属性（常用 1-2 个）
- 与旧有 `Char.Property` 并行，且通过桥接保持兼容

核心类：
- `com.coladungeon.traits.Trait`：简洁的静态 API（推荐）
- `com.coladungeon.traits.TraitDefinition`：Trait 类型定义
- `com.coladungeon.traits.TraitInstance`：Trait 实例（可序列化）
- `com.coladungeon.traits.TraitRegistry`：全局注册表
- `com.coladungeon.traits.PropertyTraitBridge`：旧 `Property` 与新 Trait 的桥接

现状：`Property` 尚未被完全替代。老代码继续使用 `properties().contains(Property.X)`；新代码建议使用 `hasTrait("x")`。桥接保证两边一致性。

### 快速上手

1) 声明 Trait 定义（运行时可多次调用覆盖）：

```java
Trait.let("Wartorn", "int1", Integer.class, "int2", Integer.class);
Trait.let("Overfertilized", "birthRate", Integer.class, "birthClass", String.class);
```

2) 创建 Trait 实例并附加到角色：

```java
// 创建并设置属性（键值成对）
addTrait(Trait.of("Wartorn", "int1", 0, "int2", 0));
addTrait(Trait.of("Overfertilized", "birthRate", 3, "birthClass", Rat.class));
```

3) 检查/获取 Trait：

```java
if (hasTrait("Wartorn")) { /* ... */ }
for (TraitInstance ti : traits()) { /* 遍历所有特质 */ }
```

4) 删除 Trait：

```java
removeTrait("Wartorn");
```

5) 与旧 Property 的兼容：

```java
// 仍可使用旧写法（桥接会把已知 trait 映射成等价 Property）
if (properties().contains(Char.Property.IMMOVABLE)) { /* ... */ }
```

### API 详解

- 声明/注册定义
  - `Trait.let(String id, Object... schemaKv)`
    - `schemaKv` 为扁平键值对：`"属性名", Class<?> 类型提示`
    - 多次调用相同 `id` 会更新定义
  - `Trait.let(TraitDefinition def)` 直接注册定义

- 构造实例
  - `Trait.of(String id, Object... keyValues)`
    - `keyValues` 为扁平键值对：键为 `String`，值支持
      - 基本类型、`String`
      - `Class`（保存为类名字符串）
      - `Class[]`、`Collection<Class<?>>`
      - 其它集合/数组会按元素原样或映射为字符串存储
    - 特殊键：
      - `"resistances"`：接受 `Class` / `Class[]` / `Collection<Class<?>>`，参与抗性计算
      - `"immunities"`：同上，参与免疫计算

- 角色侧 API（`Char`）
  - `addTrait(TraitInstance instance)`
  - `removeTrait(String traitId)`
  - `hasTrait(String traitId)`
  - `traits()` 获取副本集合
  - 序列化：`Char` 已自动在 `storeInBundle`/`restoreFromBundle` 读写 `traits`
  - 抗性/免疫：`resist`/`isImmune` 已整合 trait 的 `"resistances"`/`"immunities"`
  - 兼容：`properties()` 会通过桥接将已知 trait 映射回 `Property`

### 示例：为 Rat 添加特质

`Rat` 初始化中：

```java
Trait.let("Wartorn", "int1", Integer.class, "int2", Integer.class);
Trait.let("Overfertilized", "birthRate", Integer.class, "birthClass", String.class);
addTrait(Trait.of("Wartorn", "int1", 0, "int2", 0));
addTrait(Trait.of("Overfertilized", "birthRate", 0, "birthClass", Rat.class));
```

### 为特质添加抗性/免疫

```java
addTrait(Trait.of(
    "FieryOverride",
    "resistances", new Class<?>[]{ WandOfFireblast.class, Elemental.FireElemental.class },
    "immunities", java.util.Arrays.asList(Burning.class, Blazing.class)
));
```

这些会直接参与到 `Char.resist`/`Char.isImmune` 的计算逻辑。

### 动态与持久化
- Trait 定义与实例均可在运行时动态声明/附加/移除
- 实例会随 `Char` 一起存档和读档
- 定义层建议在模块初始化时集中 `Trait.let(...)`，便于工具/校验

### 迁移建议
- 新增判定优先使用 `hasTrait("id")`
- 逐步用等价 trait 替代旧 `Property` 的写法（桥接确保过渡期行为一致）
- 若需要 trait 生效于其它系统（例如速度、攻击），在对应计算点读取 `TraitInstance` 的自定义键值并整合

### 命名与规范
- `id` 使用小写或 lowerCamelCase，语义清晰，如：`immovable`、`overfertilized`
- 属性键使用 lowerCamelCase，如：`birthRate`、`birthClass`
- 跨类引用用 `Class` 传入（会保存为类名），避免强耦合

### 常见问题
- 未注册的 trait 是否可用？
  - 可以。`Trait.of` 创建实例不强制要求已有定义，但建议使用 `Trait.let` 提前声明，利于工具与协作
- 与老 `Property` 的关系？
  - 桥接已内置常见 `Property` 对应的 trait，旧逻辑兼容；长远建议统一转向 trait


