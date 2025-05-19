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
import com.coladungeon.items.Item;
import com.coladungeon.items.Waterskin;
import com.coladungeon.items.armor.ClothArmor;
import com.coladungeon.items.artifacts.CloakOfShadows;
import com.coladungeon.items.artifacts.HolyTome;
import com.coladungeon.items.bags.VelvetPouch;
import com.coladungeon.items.food.Food;
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
import com.coladungeon.items.food.CustomFood;
//#+ Minecraft_GoldenApple
import com.coladungeon.items.food.GoldenApple;
//#- Minecraft_GoldenApple


public enum HeroClass {

	WARRIOR(HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR),
	MAGE(HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK),
	ROGUE(HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER),
	HUNTRESS(HeroSubClass.SNIPER, HeroSubClass.WARDEN),
	DUELIST(HeroSubClass.CHAMPION, HeroSubClass.MONK),
	CLERIC(HeroSubClass.PRIEST, HeroSubClass.PALADIN);

	private HeroSubClass[] subClasses;

	HeroClass(HeroSubClass... subClasses) {
		this.subClasses = subClasses;
	}

	public void initHero(Hero hero) {

		hero.heroClass = this;
		Talent.initClassTalents(hero);

		Item i = new ClothArmor().identify();
		if (!Challenges.isItemBlocked(i))
			hero.belongings.armor = (ClothArmor) i;

		i = new Food();
		if (!Challenges.isItemBlocked(i))
			i.collect();

		new VelvetPouch().collect();
		Dungeon.LimitedDrops.VELVET_POUCH.drop();

		Waterskin waterskin = new Waterskin();
		waterskin.collect();

		new ScrollOfIdentify().identify();

		switch (this) {
			case WARRIOR:
				initWarrior(hero);
				break;

			case MAGE:
				initMage(hero);
				break;

			case ROGUE:
				initRogue(hero);
				break;

			case HUNTRESS:
				initHuntress(hero);
				break;

			case DUELIST:
				initDuelist(hero);
				break;

			case CLERIC:
				initCleric(hero);
				break;
		}
		// #+ Minecraft_GoldenApple_gain
		new GoldenApple().quantity(14).collect();
		// #- Minecraft_GoldenApple_gain
		new CustomFood("p").quantity(1).collect();

		if (CDSettings.quickslotWaterskin()) {
			for (int s = 0; s < QuickSlot.SIZE; s++) {
				if (Dungeon.quickslot.getItem(s) == null) {
					Dungeon.quickslot.setSlot(s, waterskin);
					break;
				}
			}
		}

	}

	public Badges.Badge masteryBadge() {
		switch (this) {
			case WARRIOR:
				return Badges.Badge.MASTERY_WARRIOR;
			case MAGE:
				return Badges.Badge.MASTERY_MAGE;
			case ROGUE:
				return Badges.Badge.MASTERY_ROGUE;
			case HUNTRESS:
				return Badges.Badge.MASTERY_HUNTRESS;
			case DUELIST:
				return Badges.Badge.MASTERY_DUELIST;
			case CLERIC:
				return Badges.Badge.MASTERY_CLERIC;
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

	public String title() {
		return Messages.get(HeroClass.class, name());
	}

	public String desc() {
		return Messages.get(HeroClass.class, name() + "_desc");
	}

	public String shortDesc() {
		return Messages.get(HeroClass.class, name() + "_desc_short");
	}

	public HeroSubClass[] subClasses() {
		return subClasses;
	}

	public ArmorAbility[] armorAbilities() {
		switch (this) {
			case WARRIOR:
			default:
				return new ArmorAbility[] { new HeroicLeap(), new Shockwave(), new Endure() };
			case MAGE:
				return new ArmorAbility[] { new ElementalBlast(), new WildMagic(), new WarpBeacon() };
			case ROGUE:
				return new ArmorAbility[] { new SmokeBomb(), new DeathMark(), new ShadowClone() };
			case HUNTRESS:
				return new ArmorAbility[] { new SpectralBlades(), new NaturesPower(), new SpiritHawk() };
			case DUELIST:
				return new ArmorAbility[] { new Challenge(), new ElementalStrike(), new Feint() };
			case CLERIC:
				return new ArmorAbility[] { new AscendedForm(), new Trinity(), new PowerOfMany() };
		}
	}

	public String spritesheet() {
		switch (this) {
			case WARRIOR:
			default:
				return Assets.Sprites.WARRIOR;
			case MAGE:
				return Assets.Sprites.MAGE;
			case ROGUE:
				return Assets.Sprites.ROGUE;
			case HUNTRESS:
				return Assets.Sprites.HUNTRESS;
			case DUELIST:
				return Assets.Sprites.DUELIST;
			case CLERIC:
				return Assets.Sprites.CLERIC;
		}
	}

	public String splashArt() {
		switch (this) {
			case WARRIOR:
			default:
				return Assets.Splashes.WARRIOR;
			case MAGE:
				return Assets.Splashes.MAGE;
			case ROGUE:
				return Assets.Splashes.ROGUE;
			case HUNTRESS:
				return Assets.Splashes.HUNTRESS;
			case DUELIST:
				return Assets.Splashes.DUELIST;
			case CLERIC:
				return Assets.Splashes.CLERIC;
		}
	}

	public boolean isUnlocked() {
		// always unlock on debug builds
		if (DeviceCompat.isDebug())
			return true;

		switch (this) {
			case WARRIOR:
			default:
				return true;
			case MAGE:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE);
			case ROGUE:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE);
			case HUNTRESS:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS);
			case DUELIST:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_DUELIST);
			case CLERIC:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_CLERIC);
		}
	}

	public String unlockMsg() {
		return shortDesc() + "\n\n" + Messages.get(HeroClass.class, name() + "_unlock");
	}

}
