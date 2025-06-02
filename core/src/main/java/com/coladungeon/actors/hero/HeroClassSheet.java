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
import com.coladungeon.items.Ankh;
import com.coladungeon.items.BrokenSeal;
import com.coladungeon.items.Waterskin;
import com.coladungeon.items.armor.ClothArmor;
import com.coladungeon.items.artifacts.CloakOfShadows;
import com.coladungeon.items.artifacts.HolyTome;
import com.coladungeon.items.bags.AmmoHolder;
import com.coladungeon.items.bags.PotionBandolier;
import com.coladungeon.items.bags.VelvetPouch;
import com.coladungeon.items.food.Food;
import com.coladungeon.items.potions.PotionOfHealing;
import com.coladungeon.items.potions.PotionOfStrength;
import com.coladungeon.items.scrolls.ScrollOfIdentify;
import com.coladungeon.items.scrolls.ScrollOfUpgrade;
import com.coladungeon.items.stones.StoneOfGeneration;
import com.coladungeon.items.wands.WandOfMagicMissile;
import com.coladungeon.items.weapon.ammo.Ammo;
import com.coladungeon.items.weapon.ammo.BreakingDawn;
import com.coladungeon.items.weapon.ammo.ExplosiveAmmo;
import com.coladungeon.items.weapon.bfg.BigFockingGun;
import com.coladungeon.items.weapon.chakram.Chakram;
import com.coladungeon.items.weapon.grenade.GrenadeLauncher;
import com.coladungeon.items.weapon.gun.Gun;
import com.coladungeon.items.weapon.heavysword.HeavySword;
import com.coladungeon.items.weapon.melee.MagesStaff;
import com.coladungeon.items.weapon.melee.WornShortsword;
import com.coladungeon.items.weapon.melee.assassin.Dagger;
import com.coladungeon.items.weapon.melee.knuckles.Knuckles;
import com.coladungeon.items.weapon.melee.vambrace.Vambrace;
import com.coladungeon.items.weapon.missiles.ThrowingStone;
import com.coladungeon.items.weapon.proj.Proj;
import com.coladungeon.items.weapon.rifle.Rifle;
import com.coladungeon.items.weapon.shotgun.Shotgun;
import com.coladungeon.items.weapon.sniper.SniperGun;
import com.coladungeon.items.Supply;
import com.coladungeon.journal.Catalog;
import com.coladungeon.utils.EventBus;

public final class HeroClassSheet {
    private static final Map<String, HeroClass> registeredClasses = new LinkedHashMap<>();
    
    // 标准职业
    public static final HeroClass WARRIOR = registerStandardClass("warrior")
        .subClasses(HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR)
        .title("战士")
        .desc("战士是一个强大的近战职业，擅长使用各种武器和防具。\n\n战士的特点：\n- 高生命值和防御力\n- 可以使用所有武器和防具\n- 起始装备：短剑和破损印记\n- 可以自动识别治疗药水和升级卷轴")
        .shortDesc("战士是一个强大的近战职业，擅长使用各种武器和防具。")
        .spritesheet(Assets.Sprites.WARRIOR)
        .splashArt(Assets.Splashes.WARRIOR)
        .abilities(new HeroicLeap(), new Shockwave(), new Endure())
        .masteryBadge(Badges.Badge.MASTERY_WARRIOR)
        .unlocked(true)
        .initializer(hero -> {
            hero.heroClass = HeroClass.WARRIOR;
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
        .title("法师")
        .desc("法师是一个强大的远程职业，擅长使用魔法和法杖。\n\n法师的特点：\n- 高魔法伤害\n- 起始装备：魔法法杖\n- 可以自动识别升级卷轴和力量药水")
        .shortDesc("法师是一个强大的远程职业，擅长使用魔法和法杖。")
        .spritesheet(Assets.Sprites.MAGE)
        .splashArt(Assets.Splashes.MAGE)
        .abilities(new ElementalBlast(), new WildMagic(), new WarpBeacon())
        .masteryBadge(Badges.Badge.MASTERY_MAGE)
        .unlocked(() -> Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE))
        .initializer(hero -> {
            hero.heroClass = HeroClass.MAGE;
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
        .title("盗贼")
        .desc("盗贼是一个灵活的近战职业，擅长隐身和暗杀。\n\n盗贼的特点：\n- 高敏捷和暴击率\n- 起始装备：短剑和隐身斗篷\n- 可以探测陷阱和隐藏的门")
        .shortDesc("盗贼是一个灵活的近战职业，擅长隐身和暗杀。")
        .spritesheet(Assets.Sprites.ROGUE)
        .splashArt(Assets.Splashes.ROGUE)
        .abilities(new SmokeBomb(), new DeathMark(), new ShadowClone())
        .masteryBadge(Badges.Badge.MASTERY_ROGUE)
        .unlocked(() -> Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE))
        .initializer(hero -> {
            hero.heroClass = HeroClass.ROGUE;
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
        .title("猎人")
        .desc("猎人是一个灵活的远程职业，擅长使用弓箭和陷阱。\n\n猎人的特点：\n- 高命中率和闪避率\n- 起始装备：弓箭\n- 可以在草丛中获得额外效果")
        .shortDesc("猎人是一个灵活的远程职业，擅长使用弓箭和陷阱。")
        .spritesheet(Assets.Sprites.HUNTRESS)
        .splashArt(Assets.Splashes.HUNTRESS)
        .abilities(new SpectralBlades(), new NaturesPower(), new SpiritHawk())
        .masteryBadge(Badges.Badge.MASTERY_HUNTRESS)
        .unlocked(() -> Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS))
        .initializer(hero -> {
            hero.heroClass = HeroClass.HUNTRESS;
            initCommon(hero);
            // TODO: 实现猎人初始化
        })
        .register();

    public static final HeroClass DUELIST = registerStandardClass("duelist")
        .subClasses(HeroSubClass.CHAMPION, HeroSubClass.MONK)
        .title("决斗者")
        .desc("决斗者是一个专精的近战职业，擅长单挑和格斗。\n\n决斗者的特点：\n- 高单体伤害\n- 起始装备：细剑\n- 可以挑战敌人进行决斗")
        .shortDesc("决斗者是一个专精的近战职业，擅长单挑和格斗。")
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
            hero.heroClass = HeroClass.DUELIST;
            initCommon(hero);
            // TODO: 实现决斗者初始化
        })
        .register();

    public static final HeroClass CLERIC = registerStandardClass("cleric")
        .subClasses(HeroSubClass.PRIEST, HeroSubClass.PALADIN)
        .title("牧师")
        .desc("牧师是一个支援型职业，擅长治疗和祝福。\n\n牧师的特点：\n- 高治疗能力\n- 起始装备：短剑和圣典\n- 可以使用神圣魔法")
        .shortDesc("牧师是一个支援型职业，擅长治疗和祝福。")
        .spritesheet(Assets.Sprites.CLERIC)
        .splashArt(Assets.Splashes.CLERIC)
        .abilities(new AscendedForm(), new Trinity(), new PowerOfMany())
        .masteryBadge(Badges.Badge.MASTERY_CLERIC)
        .unlocked(() -> Badges.isUnlocked(Badges.Badge.UNLOCK_CLERIC))
        .initializer(hero -> {
            hero.heroClass = HeroClass.CLERIC;
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

        static{
            registerStandardClass("Peter")
            .title("Peter")
            .desc("from family guy").initializer(hero->{
                hero.heroClass = registeredClasses.get("Peter");
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

        // 空白补给包仅在创建英雄时有用
        Supply meleeSupply = new Supply();
        meleeSupply.put_in(Dagger.class)
                   .put_in(Vambrace.class)
                   .put_in(Knuckles.class)
                   .put_in(HeavySword.class)
                   .put_in(WornShortsword.class)
                   .name("近战武器补给包")
                   .desc("一个装满了近战武器的包，可以从中获取到各种近战武器。")
                   .identify()
                   .collect();


        // 添加法杖（魔法飞弹）
        WandOfMagicMissile wand = new WandOfMagicMissile();
        wand.level(1);
        wand.identify().collect();
        Dungeon.quickslot.setSlot(0, wand);

        // 创建枪械补给包
        Supply gunSupply = new Supply();
        gunSupply.put_in(Shotgun.class)
                 .put_in(SniperGun.class)
                 .put_in(Rifle.class)
                 .put_in(GrenadeLauncher.class)
                 .put_in(Chakram.class)
                 .put_in(Proj.class)
                 .put_in(BigFockingGun.class)
                 .put_in(Gun.class)
                 .put_in(AmmoHolder.class)
                 .put_in(() -> {
                     Ammo ammo = new Ammo();
                     ammo.quantity(1145);
                     return ammo;
                 })
                 .put_in(() -> {
                     ExplosiveAmmo ammo = new ExplosiveAmmo();
                     ammo.quantity(233);
                     return ammo;
                 })
                 .name("枪械补给包")
                 .desc("一个装满了枪械的包，可以从中获取到各种枪械。")
                 .identify()
                 .collect();

        // 添加5个已祝福的重生十字架
        for (int i = 0; i < 5; i++) {
            Ankh ankh = new Ankh();
            ankh.bless();
            ankh.identify().collect();
        }

        //添加绒布包和药剂包
        VelvetPouch velvetPouch = new VelvetPouch();
        velvetPouch.identify().collect();
        
        PotionBandolier potionBandolier = new PotionBandolier();
        potionBandolier.identify().collect();

        // 事件通知
        EventBus.fire("Hero:created", "hero", hero);
        new BreakingDawn().quantity(3).identify().collect();
        new StoneOfGeneration().quantity(120).identify().collect();
        new ScrollOfIdentify().quantity(1200).identify().collect();
        new ScrollOfUpgrade().quantity(1200).identify().collect();
    }
};