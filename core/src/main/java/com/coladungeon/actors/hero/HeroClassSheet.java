/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.coladungeon.actors.hero;

import java.util.LinkedHashMap;
import java.util.Map;

import com.coladungeon.Assets;
import com.coladungeon.Badges;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.hero.abilities.cleric.AscendedForm;
import com.coladungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.coladungeon.actors.hero.abilities.cleric.Trinity;
import com.coladungeon.actors.hero.abilities.duelist.Challenge;
import com.coladungeon.actors.hero.abilities.duelist.ElementalStrike;
import com.coladungeon.actors.hero.abilities.duelist.Feint;
import com.coladungeon.actors.hero.abilities.huntress.NaturesPower;
import com.coladungeon.actors.hero.abilities.huntress.SpectralBlades;
import com.coladungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.coladungeon.actors.hero.abilities.mage.ElementalBlast;
import com.coladungeon.actors.hero.abilities.mage.WarpBeacon;
import com.coladungeon.actors.hero.abilities.mage.WildMagic;
import com.coladungeon.actors.hero.abilities.rogue.DeathMark;
import com.coladungeon.actors.hero.abilities.rogue.ShadowClone;
import com.coladungeon.actors.hero.abilities.rogue.SmokeBomb;
import com.coladungeon.actors.hero.abilities.warrior.Endure;
import com.coladungeon.actors.hero.abilities.warrior.HeroicLeap;
import com.coladungeon.actors.hero.abilities.warrior.Shockwave;
import com.coladungeon.items.BrokenSeal;
import com.coladungeon.items.Waterskin;
import com.coladungeon.items.armor.ClothArmor;
import com.coladungeon.items.artifacts.CloakOfShadows;
import com.coladungeon.items.artifacts.HolyTome;
import com.coladungeon.items.food.Food;
import com.coladungeon.items.potions.PotionOfHealing;
import com.coladungeon.items.potions.PotionOfStrength;
import com.coladungeon.items.scrolls.ScrollOfIdentify;
import com.coladungeon.items.scrolls.ScrollOfUpgrade;
import com.coladungeon.items.stones.StoneOfGeneration;
import com.coladungeon.items.wands.WandOfMagicMissile;
import com.coladungeon.items.weapon.drone.Drone;
import com.coladungeon.items.weapon.melee.MagesStaff;
import com.coladungeon.items.weapon.melee.WornShortsword;
import com.coladungeon.items.weapon.missiles.ThrowingStone;
import com.coladungeon.journal.Catalog;
import com.coladungeon.utils.EventBus;
/**
 * HeroClassSheet 定义了游戏中所有标准职业及其初始化逻辑。
 */
public final class HeroClassSheet {
    
    // 使用 LinkedHashMap 保持注册顺序
    private static final Map<String, HeroClass> registeredClasses = new LinkedHashMap<>();
    
    // 标准职业
    public static final HeroClass WARRIOR = registerStandardClass("warrior")
        .subClasses(HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR)
        .spritesheet(Assets.Sprites.WARRIOR)
        .splashArt(Assets.Splashes.WARRIOR)
        .abilities(new HeroicLeap(), new Shockwave(), new Endure())
        .masteryBadge(Badges.Badge.MASTERY_WARRIOR)
        .unlocked(true)
        .initializer(hero -> {
            initCommon(hero);
            
            // 基础武器
            (hero.belongings.weapon = new WornShortsword()).identify();

            // 投掷石头
            ThrowingStone stones = new ThrowingStone();
            stones.quantity(3).collect();
            Dungeon.quickslot.setSlot(0, stones);

            // 破损印记
            if (hero.belongings.armor != null) {
                hero.belongings.armor.affixSeal(new BrokenSeal());
                Catalog.setSeen(BrokenSeal.class);
            }

            // 自动识别
            new PotionOfHealing().identify();
            new ScrollOfUpgrade().identify();
        })
        .register();

    public static final HeroClass MAGE = registerStandardClass("mage")
        .subClasses(HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK)
        .spritesheet(Assets.Sprites.MAGE)
        .splashArt(Assets.Splashes.MAGE)
        .abilities(new ElementalBlast(), new WildMagic(), new WarpBeacon())
        .masteryBadge(Badges.Badge.MASTERY_MAGE)
        .unlocked(() -> Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE))
        .initializer(hero -> {
            initCommon(hero);
            
            // 法师法杖
            MagesStaff staff = new MagesStaff(new WandOfMagicMissile());
            (hero.belongings.weapon = staff).identify();
            hero.belongings.weapon.activate(hero);

            // 快捷栏
            Dungeon.quickslot.setSlot(0, staff);

            // 自动识别
            new ScrollOfUpgrade().identify();
            new PotionOfStrength().identify();
        })
        .register();

    public static final HeroClass ROGUE = registerStandardClass("rogue")
        .subClasses(HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER)
        .spritesheet(Assets.Sprites.ROGUE)
        .splashArt(Assets.Splashes.ROGUE)
        .abilities(new SmokeBomb(), new DeathMark(), new ShadowClone())
        .masteryBadge(Badges.Badge.MASTERY_ROGUE)
        .unlocked(() -> Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE))
        .initializer(hero -> {
            initCommon(hero);
            
            // 基础武器
            (hero.belongings.weapon = new WornShortsword()).identify();

            // 隐身斗篷
            CloakOfShadows cloak = new CloakOfShadows();
            (hero.belongings.artifact = cloak).identify();
            hero.belongings.artifact.activate(hero);

            // 快捷栏
            Dungeon.quickslot.setSlot(0, cloak);
        })
        .register();

    public static final HeroClass HUNTRESS = registerStandardClass("huntress")
        .subClasses(HeroSubClass.SNIPER, HeroSubClass.WARDEN)
        .spritesheet(Assets.Sprites.HUNTRESS)
        .splashArt(Assets.Splashes.HUNTRESS)
        .abilities(new SpectralBlades(), new NaturesPower(), new SpiritHawk())
        .masteryBadge(Badges.Badge.MASTERY_HUNTRESS)
        .unlocked(() -> Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS))
        .initializer(hero -> {
            initCommon(hero);
            // TODO: 实现猎人初始化
        })
        .register();

    public static final HeroClass DUELIST = registerStandardClass("duelist")
        .subClasses(HeroSubClass.CHAMPION, HeroSubClass.MONK)
        .spritesheet(Assets.Sprites.DUELIST)
        .splashArt(Assets.Splashes.DUELIST)
        .abilities(new Challenge(), new ElementalStrike(), new Feint())
        .masteryBadge(Badges.Badge.MASTERY_DUELIST)
        .unlocked(() -> {
            if (Badges.isUnlocked(Badges.Badge.UNLOCK_DUELIST)) {
                return true;
            }
            int unlockedChars = 0;
            for (Badges.Badge b : Badges.Badge.values()) {
                if (b.name().startsWith("UNLOCK_") && Badges.isUnlocked(b)) {
                    unlockedChars++;
                }
                if (unlockedChars >= 2) {
                    return true;
                }
            }
            return false;
        })
        .initializer(hero -> {
            initCommon(hero);
            // TODO: 实现决斗者初始化
        })
        .register();

    public static final HeroClass CLERIC = registerStandardClass("cleric")
        .subClasses(HeroSubClass.PRIEST, HeroSubClass.PALADIN)
        .spritesheet(Assets.Sprites.CLERIC)
        .splashArt(Assets.Splashes.CLERIC)
        .abilities(new AscendedForm(), new Trinity(), new PowerOfMany())
        .masteryBadge(Badges.Badge.MASTERY_CLERIC)
        .unlocked(() -> Badges.isUnlocked(Badges.Badge.UNLOCK_CLERIC))
        .initializer(hero -> {
            initCommon(hero);
            
            // 基础武器
            (hero.belongings.weapon = new WornShortsword()).identify();

            // 圣典
            HolyTome tome = new HolyTome();
            (hero.belongings.artifact = tome).identify();
            hero.belongings.artifact.activate(hero);

            // 快捷栏
            Dungeon.quickslot.setSlot(0, tome);
        })
        .register();

    public static final HeroClass HEAVY_SQUAD = registerStandardClass("heavy_squad")
        .subClasses(HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR)
        .title("Heavy Squad")
        .desc("The Heavy Squad starts with pre-upgraded equipment including a +1 short sword and +1 cloth armor with the broken seal attached. This makes them more resilient and better equipped for beginners.\n\nHeavy Squad also starts with five throwing stones and extra healing potions, cloth armor, and a waterskin.\n\nHeavy Squad automatically identifies:\n- Scrolls of Identify\n- Potions of Strength\n- Scrolls of Upgrade")
        .shortDesc("The Heavy Squad starts with upgraded equipment including a +1 short sword and +1 cloth armor with a broken seal. They also carry more healing potions and throwing stones.")
        .unlockMsg("The Heavy Squad starts with upgraded equipment including a +1 short sword and +1 cloth armor with a broken seal. They also carry more healing potions and throwing stones.\n\nThe Heavy Squad is automatically unlocked.")
        .spritesheet(Assets.Sprites.WARRIOR)
        .splashArt(Assets.Splashes.WARRIOR)
        .abilities(new HeroicLeap(), new Shockwave(), new Endure())
        .masteryBadge(Badges.Badge.MASTERY_HEAVY_SQUAD)
        .unlocked(true)
        .initializer(hero -> {
            initCommon(hero);
            
            // 升级的短剑
            WornShortsword sword = new WornShortsword();
            sword.level(1);
            (hero.belongings.weapon = sword).identify();

            // 投掷石头
            ThrowingStone stones = new ThrowingStone();
            stones.quantity(5).collect();
            Dungeon.quickslot.setSlot(0, stones);

            // 升级的护甲和印记
            if (hero.belongings.armor != null) {
                hero.belongings.armor.affixSeal(new BrokenSeal());
                hero.belongings.armor.level(1);
                Catalog.setSeen(BrokenSeal.class);
            }

            // 自动识别和额外物品
            new PotionOfStrength().identify();
            new ScrollOfUpgrade().identify();
            new PotionOfHealing().quantity(2).collect();
        })
        .register();

        static{
            registerStandardClass("Peter")
            .title("Peter")
            .desc("from family guy").initializer(hero->{
                initCommon(hero);
                
            })
            .register();
        }
    // 私有构造函数，防止实例化
    private HeroClassSheet() {
        throw new AssertionError("HeroClassSheet is a utility class and should not be instantiated");
    }

    /**
     * 注册一个新的英雄职业
     * @param heroClass 要注册的英雄职业
     * @return 注册的英雄职业
     */
    public static HeroClass register(HeroClass heroClass) {
        registeredClasses.put(heroClass.id(), heroClass);
        return heroClass;
    }

    /**
     * 创建并返回一个新的标准职业构建器
     * @param id 职业ID
     * @return 职业构建器
     */
    private static HeroClassBuilder registerStandardClass(String id) {
        return HeroClass.builder(id);
    }

    /**
     * 获取所有注册的职业
     * @return 职业数组
     */
    public static HeroClass[] values() {
        return registeredClasses.values().toArray(new HeroClass[0]);
    }

    /**
     * 根据ID获取职业
     * @param id 职业ID
     * @return 对应的职业，如果不存在则返回null
     */
    public static HeroClass valueOf(String id) {
        return registeredClasses.get(id);
    }

    // 通用初始化
    private static void initCommon(Hero hero) {
        // 基础装备
        (hero.belongings.armor = new ClothArmor()).identify();
        new Food().identify().collect();
        new PotionOfHealing().identify();
        new ScrollOfIdentify().identify();
        new Waterskin().collect();

        new StoneOfGeneration().quantity(5).collect();

        // 添加Drone
        new Drone().collect();

        // 事件通知
        EventBus.fire("Hero:created", Hero.class, "hero", hero);
    }
} 