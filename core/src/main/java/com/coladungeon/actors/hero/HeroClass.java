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

import com.coladungeon.Assets;
import com.coladungeon.Badges;
import com.coladungeon.Challenges;
import com.coladungeon.Dungeon;
import com.coladungeon.QuickSlot;
import com.coladungeon.CDSettings;
import com.coladungeon.actors.hero.abilities.ArmorAbility;
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
import com.coladungeon.items.CustomItem;
import com.coladungeon.items.Item;
import com.coladungeon.items.Waterskin;
import com.coladungeon.items.armor.ClothArmor;
import com.coladungeon.items.artifacts.CloakOfShadows;
import com.coladungeon.items.artifacts.HolyTome;
import com.coladungeon.items.bags.VelvetPouch;
import com.coladungeon.items.food.Food;
import com.coladungeon.items.food.CustomFood;
//#+ Minecraft_GoldenApple
import com.coladungeon.items.food.GoldenApple;
//#- Minecraft_GoldenApple
import com.coladungeon.items.potions.PotionOfHealing;
import com.coladungeon.items.potions.PotionOfInvisibility;
import com.coladungeon.items.potions.PotionOfLiquidFlame;
import com.coladungeon.items.potions.PotionOfMindVision;
import com.coladungeon.items.potions.PotionOfPurity;
import com.coladungeon.items.potions.PotionOfStrength;
import com.coladungeon.items.scrolls.ScrollOfIdentify;
import com.coladungeon.items.scrolls.ScrollOfLullaby;
import com.coladungeon.items.scrolls.ScrollOfMagicMapping;
import com.coladungeon.items.scrolls.ScrollOfMirrorImage;
import com.coladungeon.items.scrolls.ScrollOfRage;
import com.coladungeon.items.scrolls.ScrollOfRemoveCurse;
import com.coladungeon.items.scrolls.ScrollOfUpgrade;
import com.coladungeon.items.stones.StoneOfDungeonTravel;
import com.coladungeon.items.stones.StoneOfGeneration;
import com.coladungeon.items.wands.WandOfMagicMissile;
import com.coladungeon.items.weapon.SpiritBow;
import com.coladungeon.items.weapon.melee.Cudgel;
import com.coladungeon.items.weapon.melee.Dagger;
import com.coladungeon.items.weapon.melee.Gloves;
import com.coladungeon.items.weapon.melee.MagesStaff;
import com.coladungeon.items.weapon.melee.Rapier;
import com.coladungeon.items.weapon.melee.WornShortsword;
import com.coladungeon.items.weapon.missiles.ThrowingKnife;
import com.coladungeon.items.weapon.missiles.ThrowingSpike;
import com.coladungeon.items.weapon.missiles.ThrowingStone;
import com.coladungeon.journal.Catalog;
import com.coladungeon.messages.Messages;
import com.watabou.utils.DeviceCompat;
import com.coladungeon.items.Generator;
import com.watabou.utils.Bundle;
import com.watabou.utils.Bundlable;

import java.util.ArrayList;
import java.util.HashMap;

public class HeroClass implements Comparable<HeroClass>, Bundlable {

	// 存储所有已注册的英雄职业
	private static final ArrayList<HeroClass> ALL_CLASSES = new ArrayList<>();
	private static final HashMap<String, HeroClass> CLASS_BY_ID = new HashMap<>();
	
	// 预定义的标准职业
	public static final HeroClass WARRIOR = new HeroClass("warrior", HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR);
	public static final HeroClass MAGE = new HeroClass("mage", HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK);
	public static final HeroClass ROGUE = new HeroClass("rogue", HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER);
	public static final HeroClass HUNTRESS = new HeroClass("huntress", HeroSubClass.SNIPER, HeroSubClass.WARDEN);
	public static final HeroClass DUELIST = new HeroClass("duelist", HeroSubClass.CHAMPION, HeroSubClass.MONK);
	public static final HeroClass CLERIC = new HeroClass("cleric", HeroSubClass.PRIEST, HeroSubClass.PALADIN);
	public static final HeroClass HEAVY_SQUAD = new HeroClass("heavy_squad", HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR);
	

	// For switch statement compatibility
	public static class C {
		public static final int WARRIOR = 0;
		public static final int MAGE = 1;
		public static final int ROGUE = 2;
		public static final int HUNTRESS = 3;
		public static final int DUELIST = 4;
		public static final int CLERIC = 5;
		public static final int HEAVY_SQUAD = 6;
	}
	
	// Convert from int constant to HeroClass
	public static HeroClass fromC(int constant) {
		if (constant < 0 || constant >= ALL_CLASSES.size()) return WARRIOR;
		return ALL_CLASSES.get(constant);
	}
	
	// Convert from HeroClass to int constant
	public static int toC(HeroClass cls) {
		return cls.ordinal();
	}
	
	// 初始化标准职业
	static {
		ALL_CLASSES.add(WARRIOR);
		ALL_CLASSES.add(MAGE);
		ALL_CLASSES.add(ROGUE);
		ALL_CLASSES.add(HUNTRESS);
		ALL_CLASSES.add(DUELIST);
		ALL_CLASSES.add(CLERIC);
		ALL_CLASSES.add(HEAVY_SQUAD);
		
		for (HeroClass cls : ALL_CLASSES) {
			CLASS_BY_ID.put(cls.id, cls);
		}
	}
	
	// 职业属性
	private String id;
	private HeroSubClass[] subClasses;
	
	public HeroClass(String id, HeroSubClass... subClasses) {
		this.id = id;
		this.subClasses = subClasses;
	}
	
	// 创建职业的静态方法
	public static HeroClass create(String id, HeroSubClass... subClasses) {
		HeroClass heroClass = new HeroClass(id, subClasses);
		ALL_CLASSES.add(heroClass);
		CLASS_BY_ID.put(id, heroClass);
		return heroClass;
	}
	
	// 获取所有职业 - 返回数组而不是ArrayList
	public static HeroClass[] values() {
		return ALL_CLASSES.toArray(new HeroClass[0]);
	}
	
	// 根据ID获取职业
	public static HeroClass valueOf(String id) {
		return CLASS_BY_ID.get(id);
	}
	
	// 获取职业ID
	public String id() {
		return id;
	}
	
	// 实现类似enum的name方法
	public String name() {
		return id;
	}
	
	// 实现类似enum的ordinal方法
	public int ordinal() {
		return ALL_CLASSES.indexOf(this);
	}
	
	public void initHero(Hero hero) {
		hero.heroClass = this;
		initCommon(hero);
		
		if (this == WARRIOR) {
			initWarrior(hero);
		} else if (this == MAGE) {
			initMage(hero);
		} else if (this == ROGUE) {
			initRogue(hero);
		} else if (this == HUNTRESS) {
			initHuntress(hero);
		} else if (this == DUELIST) {
			initDuelist(hero);
		} else if (this == CLERIC) {
			initCleric(hero);
		} else if (this == HEAVY_SQUAD) {
			initHeavySquad(hero);
		} else {
			// 自定义职业可以在这里添加初始化逻辑
			initCustom(hero);
		}
	}
	
	private static void initCommon(Hero hero) {
		(hero.belongings.armor = new ClothArmor()).identify();
		new Food().identify().collect();
		new PotionOfHealing().identify();
		new ScrollOfIdentify().identify();
		new Waterskin().collect();
	}

	public Badges.Badge masteryBadge() {
		if (this == WARRIOR) {
			return Badges.Badge.MASTERY_WARRIOR;
		} else if (this == MAGE) {
			return Badges.Badge.MASTERY_MAGE;
		} else if (this == ROGUE) {
			return Badges.Badge.MASTERY_ROGUE;
		} else if (this == HUNTRESS) {
			return Badges.Badge.MASTERY_HUNTRESS;
		} else if (this == DUELIST) {
			return Badges.Badge.MASTERY_DUELIST;
		} else if (this == CLERIC) {
			return Badges.Badge.MASTERY_CLERIC;
		} else if (this == HEAVY_SQUAD) {
			return Badges.Badge.MASTERY_HEAVY_SQUAD;
		}
		return null;
	}

	private static void initWarrior(Hero hero) {
		(hero.belongings.weapon = new WornShortsword()).identify();
		ThrowingStone stones = new ThrowingStone();
		stones.quantity(3).collect();
		Dungeon.quickslot.setSlot(0, stones);

		if (hero.belongings.armor != null) {
			hero.belongings.armor.affixSeal(new BrokenSeal());
			Catalog.setSeen(BrokenSeal.class); // as it's not added to the inventory
		}

		new PotionOfHealing().identify();
		new ScrollOfRage().identify();
	}

	private static void initMage(Hero hero) {
		MagesStaff staff;

		staff = new MagesStaff(new WandOfMagicMissile());

		(hero.belongings.weapon = staff).identify();
		hero.belongings.weapon.activate(hero);

		Dungeon.quickslot.setSlot(0, staff);

		new ScrollOfUpgrade().identify();
		new PotionOfLiquidFlame().identify();
	}

	private static void initRogue(Hero hero) {
		(hero.belongings.weapon = new Dagger()).identify();

		CloakOfShadows cloak = new CloakOfShadows();
		(hero.belongings.artifact = cloak).identify();
		hero.belongings.artifact.activate(hero);

		ThrowingKnife knives = new ThrowingKnife();
		knives.quantity(3).collect();

		Dungeon.quickslot.setSlot(0, cloak);
		Dungeon.quickslot.setSlot(1, knives);

		new ScrollOfMagicMapping().identify();
		new PotionOfInvisibility().identify();
	}

	private static void initHuntress(Hero hero) {

		(hero.belongings.weapon = new Gloves()).identify();
		SpiritBow bow = new SpiritBow();
		bow.identify().collect();

		Dungeon.quickslot.setSlot(0, bow);

		new PotionOfMindVision().identify();
		new ScrollOfLullaby().identify();
	}

	private static void initDuelist(Hero hero) {

		(hero.belongings.weapon = new Rapier()).identify();
		hero.belongings.weapon.activate(hero);

		ThrowingSpike spikes = new ThrowingSpike();
		spikes.quantity(2).collect();

		Dungeon.quickslot.setSlot(0, hero.belongings.weapon);
		Dungeon.quickslot.setSlot(1, spikes);

		new PotionOfStrength().identify();
		new ScrollOfMirrorImage().identify();
	}

	private static void initCleric(Hero hero) {

		(hero.belongings.weapon = new Cudgel()).identify();
		hero.belongings.weapon.activate(hero);

		HolyTome tome = new HolyTome();
		(hero.belongings.artifact = tome).identify();
		hero.belongings.artifact.activate(hero);

		Dungeon.quickslot.setSlot(0, tome);

		new PotionOfPurity().identify();
		new ScrollOfRemoveCurse().identify();
	}

	private static void initHeavySquad(Hero hero) {
		// 给重装小队特殊的起始装备
		WornShortsword sword = new WornShortsword();
		sword.level(1); // 起始武器已升级一次
		(hero.belongings.weapon = sword).identify();
		
		// 额外的初始石头数量
		ThrowingStone stones = new ThrowingStone();
		stones.quantity(5).collect();
		Dungeon.quickslot.setSlot(0, stones);

		// 起始防御加成
		if (hero.belongings.armor != null) {
			hero.belongings.armor.affixSeal(new BrokenSeal());
			hero.belongings.armor.level(1); // 起始防具已升级一次
			Catalog.setSeen(BrokenSeal.class);
		}

		// 特殊的药剂和卷轴识别
		new PotionOfStrength().identify();
		new ScrollOfUpgrade().identify();
		
		// 额外的恢复药
		new PotionOfHealing().quantity(2).collect();
	}

	// 自定义职业初始化方法，可以被子类覆盖
	protected void initCustom(Hero hero) {
		// 默认实现，为自定义职业提供基本装备
		(hero.belongings.weapon = new WornShortsword()).identify();
		hero.belongings.weapon.activate(hero);
	}

	public String title() {
		if (this == WARRIOR) {
			return Messages.get(HeroClass.class, "warrior_title");
		} else if (this == MAGE) {
			return Messages.get(HeroClass.class, "mage_title");
		} else if (this == ROGUE) {
			return Messages.get(HeroClass.class, "rogue_title");
		} else if (this == HUNTRESS) {
			return Messages.get(HeroClass.class, "huntress_title");
		} else if (this == DUELIST) {
			return Messages.get(HeroClass.class, "duelist_title");
		} else if (this == CLERIC) {
			return Messages.get(HeroClass.class, "cleric_title");
		} else if (this == HEAVY_SQUAD) {
			return "Heavy Squad"; // 硬编码字符串
		} else {
			return Messages.get(HeroClass.class, id + "_title");
		}
	}

	public String desc() {
		if (this == WARRIOR) {
			return Messages.get(HeroClass.class, "warrior_desc");
		} else if (this == MAGE) {
			return Messages.get(HeroClass.class, "mage_desc");
		} else if (this == ROGUE) {
			return Messages.get(HeroClass.class, "rogue_desc");
		} else if (this == HUNTRESS) {
			return Messages.get(HeroClass.class, "huntress_desc");
		} else if (this == DUELIST) {
			return Messages.get(HeroClass.class, "duelist_desc");
		} else if (this == CLERIC) {
			return Messages.get(HeroClass.class, "cleric_desc");
		} else if (this == HEAVY_SQUAD) {
			return "The Heavy Squad starts with pre-upgraded equipment including a +1 short sword and +1 cloth armor with the broken seal attached. This makes them more resilient and better equipped for beginners.\n\nHeavy Squad also starts with five throwing stones and extra healing potions, cloth armor, and a waterskin.\n\nHeavy Squad automatically identifies:\n- Scrolls of Identify\n- Potions of Strength\n- Scrolls of Upgrade"; // 硬编码字符串
		} else {
			return Messages.get(HeroClass.class, id + "_desc");
		}
	}

	public String shortDesc() {
		if (this == HEAVY_SQUAD) {
			return "The Heavy Squad starts with upgraded equipment including a +1 short sword and +1 cloth armor with a broken seal. They also carry more healing potions and throwing stones."; // 硬编码字符串
		}
		return Messages.get(HeroClass.class, id + "_desc_short");
	}

	public HeroSubClass[] subClasses() {
		return subClasses;
	}

	public ArmorAbility[] armorAbilities() {
		if (this == WARRIOR) {
			return new ArmorAbility[] { new HeroicLeap(), new Shockwave(), new Endure() };
		} else if (this == MAGE) {
			return new ArmorAbility[] { new ElementalBlast(), new WildMagic(), new WarpBeacon() };
		} else if (this == ROGUE) {
			return new ArmorAbility[] { new SmokeBomb(), new DeathMark(), new ShadowClone() };
		} else if (this == HUNTRESS) {
			return new ArmorAbility[] { new SpectralBlades(), new NaturesPower(), new SpiritHawk() };
		} else if (this == DUELIST) {
			return new ArmorAbility[] { new Challenge(), new ElementalStrike(), new Feint() };
		} else if (this == CLERIC) {
			return new ArmorAbility[] { new AscendedForm(), new Trinity(), new PowerOfMany() };
		} else if (this == HEAVY_SQUAD) {
			// Heavy Squad 使用战士的技能，但可以自定义为特有技能
			return new ArmorAbility[] { new HeroicLeap(), new Shockwave(), new Endure() };
		} else {
			// 默认返回战士能力作为自定义职业的能力
			return new ArmorAbility[] { new HeroicLeap(), new Shockwave(), new Endure() };
		}
	}

	public String spritesheet() {
		if (this == WARRIOR) {
			return Assets.Sprites.WARRIOR;
		} else if (this == MAGE) {
			return Assets.Sprites.MAGE;
		} else if (this == ROGUE) {
			return Assets.Sprites.ROGUE;
		} else if (this == HUNTRESS) {
			return Assets.Sprites.HUNTRESS;
		} else if (this == DUELIST) {
			return Assets.Sprites.DUELIST;
		} else if (this == CLERIC) {
			return Assets.Sprites.CLERIC;
		} else if (this == HEAVY_SQUAD) {
			// 使用战士的精灵图，实际应用中应该有专属图像
			return Assets.Sprites.WARRIOR;
		} else {
			// 默认使用战士的精灵图
			return Assets.Sprites.WARRIOR;
		}
	}

	public String splashArt() {
		if (this == WARRIOR) {
			return Assets.Splashes.WARRIOR;
		} else if (this == MAGE) {
			return Assets.Splashes.MAGE;
		} else if (this == ROGUE) {
			return Assets.Splashes.ROGUE;
		} else if (this == HUNTRESS) {
			return Assets.Splashes.HUNTRESS;
		} else if (this == DUELIST) {
			return Assets.Splashes.DUELIST;
		} else if (this == CLERIC) {
			return Assets.Splashes.CLERIC;
		} else if (this == HEAVY_SQUAD) {
			// 使用战士的启动图，实际应用中应该有专属图像
			return Assets.Splashes.WARRIOR;
		} else {
			// 默认使用战士的启动图
			return Assets.Splashes.WARRIOR;
		}
	}

	public boolean isUnlocked() {
		// 在debug模式下自动解锁所有角色
		if (DeviceCompat.isDebug()) {
			return true;
		}
		
		if (this == WARRIOR) {
			return true;
		} else if (this == MAGE) {
			return Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE);
		} else if (this == ROGUE) {
			return Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE);
		} else if (this == HUNTRESS) {
			return Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS);
		} else if (this == DUELIST) {
			//duelist is unlocked if player has won with two other characters, or has unlocked one character and duelist
			if (Badges.isUnlocked(Badges.Badge.UNLOCK_DUELIST)){
				return true;
			}

			int unlockedChars = 0;
			for (Badges.Badge b : Badges.Badge.values()){
				if (b.name().startsWith("UNLOCK_") && Badges.isUnlocked(b)){
					unlockedChars++;
				}
				
				if (unlockedChars >= 2){
					return true;
				}
			}
			return false;
		} else if (this == CLERIC) {
			return Badges.isUnlocked(Badges.Badge.UNLOCK_CLERIC);
		} else if (this == HEAVY_SQUAD) {
			// Heavy Squad 总是解锁的
			return true;
		} else {
			// 自定义职业默认锁定
			return false;
		}
	}

	public String unlockMsg() {
		if (this == HEAVY_SQUAD) {
			return shortDesc() + "\n\nThe Heavy Squad is automatically unlocked.";
		}
		return shortDesc() + "\n\n" + Messages.get(HeroClass.class, id + "_unlock");
	}

	// 用于比较的equals和hashCode方法
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		HeroClass that = (HeroClass) obj;
		return id.equals(that.id);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		return id;
	}
	
	// 实现Comparable接口
	@Override
	public int compareTo(HeroClass other) {
		return Integer.compare(ordinal(), other.ordinal());
	}
	
	// 实现Bundlable接口
	private static final String CLASS_ID = "class_id";
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		id = bundle.getString(CLASS_ID);
	}
	
	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(CLASS_ID, id);
	}
	
	// 内部类 - 职业工厂，用于构建自定义职业
	public static class ClassBuilder {
		private String id;
		private HeroSubClass[] subClasses;
		
		public ClassBuilder(String id) {
			this.id = id;
		}
		
		public ClassBuilder withSubClasses(HeroSubClass... subClasses) {
			this.subClasses = subClasses;
			return this;
		}
		
		public HeroClass build() {
			return create(id, subClasses);
		}
	}
	
	/**
	 * 快速注册一个新的英雄类
	 * 
	 * 示例用法:
	 * <pre>
	 * static {
	 *     HeroClass CUSTOM_CLASS = quickRegister(
	 *         "custom_id",                  // ID
	 *         "Custom Class",               // 标题
	 *         "详细描述...",                // 描述 
	 *         "简短描述...",                // 简短描述
	 *         null,                         // 使用默认解锁消息
	 *         true,                         // 总是解锁
	 *         HeroClass::initWarrior,       // 初始化函数
	 *         HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR  // 子职业
	 *     );
	 * }
	 * </pre>
	 * 
	 * @param id 英雄类ID
	 * @param title 英雄类标题（硬编码）
	 * @param description 英雄类描述（硬编码）
	 * @param shortDescription 英雄类简短描述（硬编码）
	 * @param unlockMessage 解锁消息（硬编码），传null则使用默认消息
	 * @param alwaysUnlocked 是否总是解锁
	 * @param initFunction 英雄初始化函数，传null则使用战士的初始化
	 * @param subClasses 子职业选项
	 * @return 新创建的英雄类
	 */
	public static HeroClass quickRegister(String id, 
										 String title,
										 String description, 
										 String shortDescription,
										 String unlockMessage,
										 boolean alwaysUnlocked,
										 java.util.function.Consumer<Hero> initFunction,
										 HeroSubClass... subClasses) {
		// 创建一个匿名HeroClass子类，覆盖必要的方法以使用硬编码字符串
		HeroClass heroClass = new HeroClass(id, subClasses) {
			@Override
			public String title() {
				return title;
			}
			
			@Override
			public String desc() {
				return description;
			}
			
			@Override
			public String shortDesc() {
				return shortDescription;
			}
			
			@Override
			public String unlockMsg() {
				return unlockMessage != null ? unlockMessage : (shortDescription + "\n\n" + "This class is automatically unlocked.");
			}
			
			@Override
			public boolean isUnlocked() {
				return DeviceCompat.isDebug() || alwaysUnlocked;
			}
			
			@Override
			public String spritesheet() {
				// 默认使用战士的精灵图，可以在子类中覆盖
				return Assets.Sprites.WARRIOR;
			}
			
			@Override
			public String splashArt() {
				// 默认使用战士的启动图，可以在子类中覆盖
				return Assets.Splashes.WARRIOR;
			}
			
			@Override
			public ArmorAbility[] armorAbilities() {
				// 默认使用战士的技能，可以在子类中覆盖
				return new ArmorAbility[] { new HeroicLeap(), new Shockwave(), new Endure() };
			}
			
			@Override
			public void initHero(Hero hero) {
				hero.heroClass = this;
				initCommon(hero);
				
				if (initFunction != null) {
					initFunction.accept(hero);
				} else {
					initWarrior(hero);
				}
			}
		};
		
		// 添加到全局列表和映射
		ALL_CLASSES.add(heroClass);
		CLASS_BY_ID.put(id, heroClass);
		
		return heroClass;
	}
	
	/**
	 * 快速注册一个新的英雄类（无自定义初始化函数）
	 */
	public static HeroClass quickRegister(String id, 
										 String title,
										 String description, 
										 String shortDescription,
										 String unlockMessage,
										 boolean alwaysUnlocked,
										 HeroSubClass... subClasses) {
		return quickRegister(id, title, description, shortDescription, unlockMessage, alwaysUnlocked, null, subClasses);
	}
}
