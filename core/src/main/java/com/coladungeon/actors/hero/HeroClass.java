package com.coladungeon.actors.hero;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.coladungeon.Badges;
import com.coladungeon.actors.hero.abilities.ArmorAbility;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class HeroClass implements Comparable<HeroClass>, Bundlable {

	// 标准职业的静态引用
	public static final HeroClass WARRIOR = HeroClassSheet.WARRIOR;
	public static final HeroClass MAGE = HeroClassSheet.MAGE;
	public static final HeroClass ROGUE = HeroClassSheet.ROGUE;
	public static final HeroClass HUNTRESS = HeroClassSheet.HUNTRESS;
	public static final HeroClass DUELIST = HeroClassSheet.valueOf("duelist");
	public static final HeroClass CLERIC = HeroClassSheet.CLERIC;
	// public static final HeroClass HEAVY_SQUAD = HeroClassSheet.HEAVY_SQUAD;

	// 职业属性
	public String id;
	public HeroSubClass[] subClasses;
	public Supplier<String> titleSupplier;
	public Supplier<String> descSupplier;
	public Supplier<String> shortDescSupplier;
	public Supplier<String> unlockMsgSupplier;
	public Supplier<Boolean> unlockedSupplier;
	public Supplier<String> spritesheetSupplier;
	public Supplier<String> splashArtSupplier;
	public Supplier<ArmorAbility[]> armorAbilitiesSupplier;
	public Supplier<Badges.Badge> masteryBadgeSupplier;
	public Consumer<Hero> initializer=(hero)->{
		hero.heroClass=this;
	};
	public ArrayList<Talent> classTalentsTier1;
	public ArrayList<Talent> classTalentsTier2;
	// public ArrayList<Talent> subclassTalents;  // 子职业天赋
	// public ArrayList<Talent> armorTalents;     // 护甲天赋

	// 包级别访问权限的构造函数
	public HeroClass(String id) {
		this.id = id;
	}

	// 静态工厂方法
	public static HeroClassBuilder builder(String id) {
		return new HeroClassBuilder(id);
	}

	// 获取所有职业
	public static HeroClass[] values() {
		return HeroClassSheet.values();
	}

	// 根据ID获取职业
	public static HeroClass valueOf(String id) {
		for (HeroClass cls : values()) {
			if (cls.id.equals(id)) {
				return cls;
			}
		}
		return null;
	}

	// 初始化英雄
	public void initHero(Hero hero) {
		hero.heroClass = this;
		if (initializer != null) {
			initializer.accept(hero);
		}
	}

	/**
	 * 将 HeroClass 转换为整数常量。
	 * 使用 ordinal() 作为常量值，这样新增职业时不需要修改此方法。
	 * 
	 * @param heroClass 要转换的英雄职业
	 * @return 对应的整数常量，如果职业不存在则返回 -1
	 */
	public static int toC(HeroClass heroClass) {
		if (heroClass == null) return -1;
		return heroClass.ordinal();
	}

	/**
	 * 将整数常量转换回 HeroClass。
	 * 使用 values() 数组进行转换，这样新增职业时不需要修改此方法。
	 * 
	 * @param constant 要转换的整数常量
	 * @return 对应的英雄职业，如果常量无效则返回 null
	 */
	public static HeroClass fromC(int constant) {
		HeroClass[] classes = values();
		if (constant >= 0 && constant < classes.length) {
			return classes[constant];
		}
		return null;
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
		HeroClass[] values = values();
		for (int i = 0; i < values.length; i++) {
			if (this.equals(values[i])) {
				return i;
			}
		}
		return -1;
	}

	public Badges.Badge masteryBadge() {
		return masteryBadgeSupplier.get();
	}

	public String title() {
		return titleSupplier.get();
	}

	public String desc() {
		return descSupplier.get();
	}

	public String shortDesc() {
		return shortDescSupplier.get();
	}

	public HeroSubClass[] subClasses() {
		return subClasses;
	}

	public ArmorAbility[] armorAbilities() {
		return armorAbilitiesSupplier.get();
	}

	public String spritesheet() {
		return spritesheetSupplier.get();
	}

	public String splashArt() {
		return splashArtSupplier.get();
	}

	public boolean isUnlocked() {
		return unlockedSupplier.get();
	}

	public String unlockMsg() {
		return shortDesc() + "\n\n" + unlockMsgSupplier.get();
	}

	// 用于比较的equals和hashCode方法
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
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
}
