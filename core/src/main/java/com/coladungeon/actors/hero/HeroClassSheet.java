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
import com.coladungeon.items.food.Food;
import com.coladungeon.items.potions.PotionOfHealing;
import com.coladungeon.items.potions.PotionOfStrength;
import com.coladungeon.items.scrolls.ScrollOfIdentify;
import com.coladungeon.items.scrolls.ScrollOfUpgrade;
import com.coladungeon.items.wands.WandOfMagicMissile;
import com.coladungeon.items.weapon.ammo.ExplosiveAmmo;
import com.coladungeon.items.weapon.ammo.NormalAmmo;
import com.coladungeon.items.weapon.bfg.BigFockingGun;
import com.coladungeon.items.weapon.chakram.Chakram;
import com.coladungeon.items.weapon.grenade.GrenadeLauncher;
import com.coladungeon.items.weapon.handgun.HandGun;
import com.coladungeon.items.weapon.heavysword.HeavySword;
import com.coladungeon.items.weapon.melee.MagesStaff;
import com.coladungeon.items.weapon.melee.WornShortsword;
import com.coladungeon.items.weapon.missiles.ThrowingStone;
import com.coladungeon.items.weapon.proj.Proj;
import com.coladungeon.items.weapon.rifle.Rifle;
import com.coladungeon.items.weapon.shotgun.Shotgun;
import com.coladungeon.items.weapon.sniper.SniperGun;
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

    public static final HeroClass HEAVY_SQUAD = registerStandardClass("heavy_squad")
        .subClasses(HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR)
        .title("重装部队")
        .desc("重装部队是一个特殊的近战职业，起始装备更好。\n\n重装部队的特点：\n- 起始装备：+1短剑和+1布甲（带印记）\n- 额外的治疗药水和投掷石头\n- 自动识别：\n  * 鉴定卷轴\n  * 力量药水\n  * 升级卷轴")
        .shortDesc("重装部队是一个特殊的近战职业，起始装备更好。")
        .unlockMsg("The Heavy Squad starts with upgraded equipment including a +1 short sword and +1 cloth armor with a broken seal. They also carry more healing potions and throwing stones.\n\nThe Heavy Squad is automatically unlocked.")
        .spritesheet(Assets.Sprites.WARRIOR)
        .splashArt(Assets.Splashes.WARRIOR)
        .abilities(new HeroicLeap(), new Shockwave(), new Endure())
        .masteryBadge(Badges.Badge.MASTERY_HEAVY_SQUAD)
        .unlocked(true)
        .initializer(hero -> {
            hero.heroClass = HeroClass.HEAVY_SQUAD;
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
                hero.heroClass = HeroClass.WARRIOR; // 使用战士作为基础职业
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

        // 添加弹药包
        AmmoHolder ammoHolder = new AmmoHolder();
        ammoHolder.identify().collect();

        // 添加法杖（魔法飞弹）
        WandOfMagicMissile wand = new WandOfMagicMissile();
        wand.level(1);
        wand.identify().collect();
        Dungeon.quickslot.setSlot(0, wand);

        // 添加重剑
        HeavySword heavySword = new HeavySword();
        heavySword.level(1);
        heavySword.identify().collect();

        // 添加散弹枪作为主武器
        Shotgun shotgun = new Shotgun();
        shotgun.level(1);
        (hero.belongings.weapon = shotgun).identify();

        // 添加狙击枪到快捷栏
        SniperGun sniperGun = new SniperGun();
        sniperGun.level(1);
        sniperGun.identify().collect();
        Dungeon.quickslot.setSlot(1, sniperGun);

        // 添加手枪到快捷栏
        HandGun handGun = new HandGun();
        handGun.level(1);
        handGun.identify().collect();
        Dungeon.quickslot.setSlot(2, handGun);

        // 添加步枪到快捷栏
        Rifle rifle = new Rifle();
        rifle.level(1);
        rifle.identify().collect();

        // 添加榴弹发射器到快捷栏
        GrenadeLauncher grenadeLauncher = new GrenadeLauncher();
        grenadeLauncher.level(1);
        grenadeLauncher.identify().collect();

        // 添加巨型飞镖到快捷栏
        Chakram chakram = new Chakram();
        chakram.level(1);
        chakram.identify().collect();

        // 添加投射器到快捷栏
        Proj proj = new Proj();
        proj.level(1);
        proj.identify().collect();

        // 添加BFG到快捷栏
        BigFockingGun bfg = new BigFockingGun();
        bfg.identify().collect();

        // 添加弹药
        // 普通弹药
        NormalAmmo normalAmmo = new NormalAmmo();
        normalAmmo.identify().collect();
        
        // 爆炸弹药
        ExplosiveAmmo explosiveAmmo = new ExplosiveAmmo();
        explosiveAmmo.identify().collect();

        // 添加500个已祝福的重生十字架
        for (int i = 0; i < 5; i++) {
            Ankh ankh = new Ankh();
            ankh.bless();
            ankh.identify().collect();
        }

        // 事件通知
        EventBus.fire("Hero:created", "hero", hero);
    }
} 