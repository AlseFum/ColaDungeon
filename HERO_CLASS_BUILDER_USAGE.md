# HeroClass Builder 模式使用指南

## 概述

新的 `HeroClass` 系统采用了函数式编程和 Builder 模式，参考了 Theme 系统的设计。这种设计提供了更大的灵活性和可扩展性，使得创建自定义英雄职业变得简单而强大。

## 主要特性

### 1. 函数式编程
- 使用 `Supplier<T>` 和 `Consumer<T>` 接口
- 支持 Lambda 表达式和方法引用
- 延迟计算和动态行为

### 2. Builder 模式
- 流畅的 API 设计
- 链式方法调用
- 可选参数配置

### 3. 统一化设计
- 所有职业使用相同的创建模式
- 消除了大量的 if-else 分支
- 更好的代码维护性

## 基本用法

### 创建简单的自定义职业

```java
HeroClass CUSTOM_WARRIOR = new HeroClass.Builder("custom_warrior")
    .withSubClasses(HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR)
    .withTitle("Custom Warrior")
    .withDesc("A custom warrior class with special abilities.")
    .withShortDesc("Enhanced warrior with custom equipment.")
    .withUnlocked(true)
    .register();
```

### 使用函数式编程

```java
HeroClass DYNAMIC_CLASS = new HeroClass.Builder("dynamic_class")
    .withTitleSupplier(() -> "Dynamic " + System.currentTimeMillis())
    .withUnlockedSupplier(() -> Badges.isUnlocked(Badges.Badge.MASTERY_WARRIOR))
    .withInitFunction(hero -> {
        // 自定义初始化逻辑
        (hero.belongings.weapon = new WornShortsword()).identify();
        hero.belongings.weapon.level(2);
    })
    .register();
```

### 复杂的解锁条件

```java
HeroClass MASTER_CLASS = new HeroClass.Builder("master_class")
    .withUnlockedSupplier(() -> {
        // 需要掌握至少3个职业
        int masteredClasses = 0;
        for (Badges.Badge badge : Badges.Badge.values()) {
            if (badge.name().startsWith("MASTERY_") && Badges.isUnlocked(badge)) {
                masteredClasses++;
            }
        }
        return masteredClasses >= 3;
    })
    .register();
```

## 高级用法

### 动态能力配置

```java
HeroClass ADAPTIVE_CLASS = new HeroClass.Builder("adaptive_class")
    .withArmorAbilitiesSupplier(() -> {
        // 根据游戏状态动态选择能力
        if (Dungeon.depth > 10) {
            return new ArmorAbility[] { new Endure(), new Shockwave() };
        } else {
            return new ArmorAbility[] { new HeroicLeap() };
        }
    })
    .register();
```

### 条件性资源配置

```java
HeroClass RESOURCE_AWARE_CLASS = new HeroClass.Builder("resource_aware")
    .withSpritesheetSupplier(() -> {
        // 根据设备性能选择不同的精灵图
        return DeviceCompat.isHighPerformance() ? 
            Assets.Sprites.WARRIOR_HD : Assets.Sprites.WARRIOR;
    })
    .register();
```

### 本地化支持

```java
HeroClass LOCALIZED_CLASS = new HeroClass.Builder("localized_class")
    .withTitleSupplier(() -> Messages.get(HeroClass.class, "localized_class_title"))
    .withDescSupplier(() -> Messages.get(HeroClass.class, "localized_class_desc"))
    .withShortDescSupplier(() -> Messages.get(HeroClass.class, "localized_class_desc_short"))
    .register();
```

## 便捷方法

### quickRegister 方法

对于简单的自定义职业，可以使用 `quickRegister` 便捷方法：

```java
HeroClass QUICK_CLASS = HeroClass.quickRegister(
    "quick_class",
    "Quick Class",
    "A quickly created class for testing purposes.",
    "Quick test class",
    "This class is for testing only.",
    true, // 总是解锁
    HeroClass::initWarrior, // 使用战士的初始化
    HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR
);
```

## 示例：完整的自定义职业

```java
public static final HeroClass ARCANE_KNIGHT = new HeroClass.Builder("arcane_knight")
    .withSubClasses(HeroSubClass.BATTLEMAGE, HeroSubClass.PALADIN)
    .withTitle("Arcane Knight")
    .withDesc("The Arcane Knight combines martial prowess with magical abilities. " +
             "They start with enchanted equipment and can cast spells while fighting.\n\n" +
             "Starting equipment:\n" +
             "- +1 Enchanted Sword\n" +
             "- +1 Magical Armor\n" +
             "- Spell Scroll Collection\n\n" +
             "Special abilities:\n" +
             "- Spell Sword techniques\n" +
             "- Magical resistance\n" +
             "- Enhanced mana regeneration")
    .withShortDesc("A warrior-mage hybrid with enchanted equipment and spell-casting abilities.")
    .withUnlockMsg("Master both combat and magic to unlock the Arcane Knight.")
    .withInitFunction(hero -> {
        // 魔法剑
        WornShortsword sword = new WornShortsword();
        sword.level(1);
        sword.enchant(); // 假设有附魔方法
        (hero.belongings.weapon = sword).identify();
        
        // 魔法防具
        if (hero.belongings.armor != null) {
            hero.belongings.armor.level(1);
        }
        
        // 法术卷轴
        new ScrollOfUpgrade().quantity(2).collect();
        new ScrollOfMagicMapping().identify();
        
        // 魔法药剂
        new PotionOfLiquidFlame().identify();
        new PotionOfHealing().quantity(2).collect();
    })
    .withArmorAbilities(new HeroicLeap(), new ElementalBlast(), new WarpBeacon())
    .withSpritesheet(Assets.Sprites.DUELIST) // 使用决斗者的外观
    .withSplashArt(Assets.Splashes.DUELIST)
    .withMasteryBadge(Badges.Badge.MASTERY_DUELIST)
    .withUnlockedSupplier(() -> 
        Badges.isUnlocked(Badges.Badge.MASTERY_WARRIOR) && 
        Badges.isUnlocked(Badges.Badge.MASTERY_MAGE) &&
        Dungeon.hero != null && Dungeon.hero.lvl >= 20
    )
    .register();
```

## 最佳实践

1. **使用方法引用**：优先使用 `HeroClass::initWarrior` 而不是 Lambda 表达式
2. **延迟计算**：使用 Supplier 接口进行延迟计算，避免过早的资源加载
3. **条件性配置**：利用函数式编程实现动态行为
4. **本地化支持**：使用消息系统而不是硬编码字符串
5. **资源管理**：合理使用 Supplier 避免重复创建对象

## 迁移指南

### 从旧系统迁移

旧代码：
```java
public static final HeroClass OLD_CLASS = new HeroClass("old_class", HeroSubClass.BERSERKER);
```

新代码：
```java
public static final HeroClass NEW_CLASS = new HeroClass.Builder("new_class")
    .withSubClasses(HeroSubClass.BERSERKER)
    .register();
```

### 兼容性

新系统完全向后兼容，所有现有的 API 调用都能正常工作：
- `HeroClass.values()`
- `HeroClass.valueOf(String)`
- `heroClass.initHero(Hero)`
- `heroClass.title()`
- 等等...

## 总结

新的 HeroClass Builder 模式提供了：
- 更好的代码组织
- 更强的可扩展性
- 更简单的自定义职业创建
- 更好的性能（延迟计算）
- 更好的维护性（消除分支逻辑）

这种设计使得添加新职业变得非常简单，同时保持了代码的清洁和可维护性。 