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

package com.coladungeon.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

import com.coladungeon.Dungeon;
import com.coladungeon.items.armor.Armor;
import com.coladungeon.items.armor.ClericArmor;
import com.coladungeon.items.armor.ClothArmor;
import com.coladungeon.items.armor.DuelistArmor;
import com.coladungeon.items.armor.HuntressArmor;
import com.coladungeon.items.armor.LeatherArmor;
import com.coladungeon.items.armor.MageArmor;
import com.coladungeon.items.armor.MailArmor;
import com.coladungeon.items.armor.PlateArmor;
import com.coladungeon.items.armor.RogueArmor;
import com.coladungeon.items.armor.ScaleArmor;
import com.coladungeon.items.armor.WarriorArmor;
import com.coladungeon.items.artifacts.AlchemistsToolkit;
import com.coladungeon.items.artifacts.Artifact;
import com.coladungeon.items.artifacts.ChaliceOfBlood;
import com.coladungeon.items.artifacts.CloakOfShadows;
import com.coladungeon.items.artifacts.DriedRose;
import com.coladungeon.items.artifacts.EtherealChains;
import com.coladungeon.items.artifacts.HolyTome;
import com.coladungeon.items.artifacts.HornOfPlenty;
import com.coladungeon.items.artifacts.MasterThievesArmband;
import com.coladungeon.items.artifacts.SandalsOfNature;
import com.coladungeon.items.artifacts.TalismanOfForesight;
import com.coladungeon.items.artifacts.TimekeepersHourglass;
import com.coladungeon.items.artifacts.UnstableSpellbook;
import com.coladungeon.items.food.Food;
import com.coladungeon.items.food.MysteryMeat;
import com.coladungeon.items.food.Pasty;
import com.coladungeon.items.potions.Potion;
import com.coladungeon.items.potions.PotionOfExperience;
import com.coladungeon.items.potions.PotionOfFrost;
import com.coladungeon.items.potions.PotionOfHaste;
import com.coladungeon.items.potions.PotionOfHealing;
import com.coladungeon.items.potions.PotionOfInvisibility;
import com.coladungeon.items.potions.PotionOfLevitation;
import com.coladungeon.items.potions.PotionOfLiquidFlame;
import com.coladungeon.items.potions.PotionOfMindVision;
import com.coladungeon.items.potions.PotionOfParalyticGas;
import com.coladungeon.items.potions.PotionOfPurity;
import com.coladungeon.items.potions.PotionOfStrength;
import com.coladungeon.items.potions.PotionOfToxicGas;
import com.coladungeon.items.potions.exotic.ExoticPotion;
import com.coladungeon.items.rings.Ring;
import com.coladungeon.items.rings.RingOfAccuracy;
import com.coladungeon.items.rings.RingOfArcana;
import com.coladungeon.items.rings.RingOfElements;
import com.coladungeon.items.rings.RingOfEnergy;
import com.coladungeon.items.rings.RingOfEvasion;
import com.coladungeon.items.rings.RingOfForce;
import com.coladungeon.items.rings.RingOfFuror;
import com.coladungeon.items.rings.RingOfHaste;
import com.coladungeon.items.rings.RingOfMight;
import com.coladungeon.items.rings.RingOfSharpshooting;
import com.coladungeon.items.rings.RingOfTenacity;
import com.coladungeon.items.rings.RingOfWealth;
import com.coladungeon.items.scrolls.Scroll;
import com.coladungeon.items.scrolls.ScrollOfIdentify;
import com.coladungeon.items.scrolls.ScrollOfLullaby;
import com.coladungeon.items.scrolls.ScrollOfMagicMapping;
import com.coladungeon.items.scrolls.ScrollOfMirrorImage;
import com.coladungeon.items.scrolls.ScrollOfRage;
import com.coladungeon.items.scrolls.ScrollOfRecharging;
import com.coladungeon.items.scrolls.ScrollOfRemoveCurse;
import com.coladungeon.items.scrolls.ScrollOfRetribution;
import com.coladungeon.items.scrolls.ScrollOfTeleportation;
import com.coladungeon.items.scrolls.ScrollOfTerror;
import com.coladungeon.items.scrolls.ScrollOfTransmutation;
import com.coladungeon.items.scrolls.ScrollOfUpgrade;
import com.coladungeon.items.scrolls.exotic.ExoticScroll;
import com.coladungeon.items.stones.Runestone;
import com.coladungeon.items.stones.StoneOfAggression;
import com.coladungeon.items.stones.StoneOfAugmentation;
import com.coladungeon.items.stones.StoneOfBlast;
import com.coladungeon.items.stones.StoneOfBlink;
import com.coladungeon.items.stones.StoneOfClairvoyance;
import com.coladungeon.items.stones.StoneOfDeepSleep;
import com.coladungeon.items.stones.StoneOfDetectMagic;
import com.coladungeon.items.stones.StoneOfEnchantment;
import com.coladungeon.items.stones.StoneOfFear;
import com.coladungeon.items.stones.StoneOfFlock;
import com.coladungeon.items.stones.StoneOfIntuition;
import com.coladungeon.items.stones.StoneOfShock;
import com.coladungeon.items.trinkets.ChaoticCenser;
import com.coladungeon.items.trinkets.DimensionalSundial;
import com.coladungeon.items.trinkets.ExoticCrystals;
import com.coladungeon.items.trinkets.EyeOfNewt;
import com.coladungeon.items.trinkets.MimicTooth;
import com.coladungeon.items.trinkets.MossyClump;
import com.coladungeon.items.trinkets.ParchmentScrap;
import com.coladungeon.items.trinkets.PetrifiedSeed;
import com.coladungeon.items.trinkets.RatSkull;
import com.coladungeon.items.trinkets.SaltCube;
import com.coladungeon.items.trinkets.ShardOfOblivion;
import com.coladungeon.items.trinkets.ThirteenLeafClover;
import com.coladungeon.items.trinkets.TrapMechanism;
import com.coladungeon.items.trinkets.Trinket;
import com.coladungeon.items.trinkets.VialOfBlood;
import com.coladungeon.items.trinkets.WondrousResin;
import com.coladungeon.items.wands.Wand;
import com.coladungeon.items.wands.WandOfBlastWave;
import com.coladungeon.items.wands.WandOfCorrosion;
import com.coladungeon.items.wands.WandOfCorruption;
import com.coladungeon.items.wands.WandOfDisintegration;
import com.coladungeon.items.wands.WandOfFireblast;
import com.coladungeon.items.wands.WandOfFrost;
import com.coladungeon.items.wands.WandOfLightning;
import com.coladungeon.items.wands.WandOfLivingEarth;
import com.coladungeon.items.wands.WandOfMagicMissile;
import com.coladungeon.items.wands.WandOfPrismaticLight;
import com.coladungeon.items.wands.WandOfRegrowth;
import com.coladungeon.items.wands.WandOfTransfusion;
import com.coladungeon.items.wands.WandOfWarding;
import com.coladungeon.items.weapon.melee.BattleAxe;
import com.coladungeon.items.weapon.melee.Crossbow;
import com.coladungeon.items.weapon.melee.Cudgel;
import com.coladungeon.items.weapon.melee.Flail;
import com.coladungeon.items.weapon.melee.Gauntlet;
import com.coladungeon.items.weapon.melee.Glaive;
import com.coladungeon.items.weapon.melee.Gloves;
import com.coladungeon.items.weapon.melee.Greataxe;
import com.coladungeon.items.weapon.melee.Greatshield;
import com.coladungeon.items.weapon.melee.Greatsword;
import com.coladungeon.items.weapon.melee.HandAxe;
import com.coladungeon.items.weapon.melee.Katana;
import com.coladungeon.items.weapon.melee.Longsword;
import com.coladungeon.items.weapon.melee.Mace;
import com.coladungeon.items.weapon.melee.MagesStaff;
import com.coladungeon.items.weapon.melee.MeleeWeapon;
import com.coladungeon.items.weapon.melee.Quarterstaff;
import com.coladungeon.items.weapon.melee.Rapier;
import com.coladungeon.items.weapon.melee.RoundShield;
import com.coladungeon.items.weapon.melee.RunicBlade;
import com.coladungeon.items.weapon.melee.Sai;
import com.coladungeon.items.weapon.melee.Scimitar;
import com.coladungeon.items.weapon.melee.Shortsword;
import com.coladungeon.items.weapon.melee.Sickle;
import com.coladungeon.items.weapon.melee.Spear;
import com.coladungeon.items.weapon.melee.Sword;
import com.coladungeon.items.weapon.melee.WarHammer;
import com.coladungeon.items.weapon.melee.WarScythe;
import com.coladungeon.items.weapon.melee.Whip;
import com.coladungeon.items.weapon.melee.WornShortsword;
import com.coladungeon.items.weapon.melee.AssassinsBlade;
import com.coladungeon.items.weapon.melee.Dagger;
import com.coladungeon.items.weapon.melee.Dirk;
import com.coladungeon.items.weapon.missiles.Bolas;
import com.coladungeon.items.weapon.missiles.FishingSpear;
import com.coladungeon.items.weapon.missiles.ForceCube;
import com.coladungeon.items.weapon.missiles.HeavyBoomerang;
import com.coladungeon.items.weapon.missiles.Javelin;
import com.coladungeon.items.weapon.missiles.Kunai;
import com.coladungeon.items.weapon.missiles.MissileWeapon;
import com.coladungeon.items.weapon.missiles.Shuriken;
import com.coladungeon.items.weapon.missiles.ThrowingClub;
import com.coladungeon.items.weapon.missiles.ThrowingHammer;
import com.coladungeon.items.weapon.missiles.ThrowingKnife;
import com.coladungeon.items.weapon.missiles.ThrowingSpear;
import com.coladungeon.items.weapon.missiles.ThrowingSpike;
import com.coladungeon.items.weapon.missiles.ThrowingStone;
import com.coladungeon.items.weapon.missiles.Tomahawk;
import com.coladungeon.items.weapon.missiles.Trident;
import com.coladungeon.items.weapon.missiles.darts.Dart;
import com.coladungeon.plants.Blindweed;
import com.coladungeon.plants.Earthroot;
import com.coladungeon.plants.Fadeleaf;
import com.coladungeon.plants.Firebloom;
import com.coladungeon.plants.Icecap;
import com.coladungeon.plants.Mageroyal;
import com.coladungeon.plants.Plant;
import com.coladungeon.plants.Rotberry;
import com.coladungeon.plants.Sorrowmoss;
import com.coladungeon.plants.Starflower;
import com.coladungeon.plants.Stormvine;
import com.coladungeon.plants.Sungrass;
import com.coladungeon.plants.Swiftthistle;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;


public class Generator {
	
	public enum Category {
		WEAPON	( 2, 2, MeleeWeapon.class, null),
		WEP_T1	( 0, 0, MeleeWeapon.class, null),
		WEP_T2	( 0, 0, MeleeWeapon.class, null),
		WEP_T3	( 0, 0, MeleeWeapon.class, null),
		WEP_T4	( 0, 0, MeleeWeapon.class, null),
		WEP_T5	( 0, 0, MeleeWeapon.class, null),
		
		ARMOR	( 2, 1, Armor.class, null),
		
		MISSILE ( 1, 2, MissileWeapon.class, null),
		MIS_T1  ( 0, 0, MissileWeapon.class, null),
		MIS_T2  ( 0, 0, MissileWeapon.class, null),
		MIS_T3  ( 0, 0, MissileWeapon.class, null),
		MIS_T4  ( 0, 0, MissileWeapon.class, null),
		MIS_T5  ( 0, 0, MissileWeapon.class, null),
		
		WAND	( 1, 1, Wand.class, null),
		RING	( 1, 0, Ring.class, null),
		ARTIFACT( 0, 1, Artifact.class, null),
		
		FOOD	( 0, 0, Food.class, null),
		
		POTION	( 8, 8, Potion.class, null),
		SEED	( 1, 1, Plant.Seed.class, null),
		
		SCROLL	( 8, 8, Scroll.class, null),
		STONE   ( 1, 1, Runestone.class, null),
		
		GOLD	( 10, 10, Gold.class, ()->new Gold()),
		
		// Add missing categories
		TRINKET ( 1, 1, Trinket.class, null),
		CUSTOM_ITEM ( 0, 0, Item.class, null),
		CUSTOM_FOOD ( 0, 0, Food.class, null);
		
		public Class<?>[] classes;

		//some item types use a deck-based system, where the probs decrement as items are picked
		// until they are all 0, and then they reset. Those generator classes should define
		// defaultProbs. If defaultProbs is null then a deck system isn't used.
		//Artifacts in particular don't reset, no duplicates!
		public float[] probs;
		public float[] defaultProbs = null;

		//some items types have two decks and swap between them
		// this enforces more consistency while still allowing for better precision
		public float[] defaultProbs2 = null;
		public boolean using2ndProbs = false;
		//but in such cases we still need a reference to the full deck in case of non-deck generation
		public float[] defaultProbsTotal = null;

		//These variables are used as a part of the deck system, to ensure that drops are consistent
		// regardless of when they occur (either as part of seeded levelgen, or random item drops)
		public Long seed = null;
		public int dropped = 0;

		//game has two decks of 35 items for overall category probs
		//one deck has a ring and extra armor, the other has an artifact and extra thrown weapon
		//Note that pure random drops only happen as part of levelgen atm, so no seed is needed here
		public float firstProb;
		public float secondProb;
		public Class<? extends Item> superClass;
		public Supplier<Item> make; // 新增：物品生成函数
		
		private Category(float firstProb, float secondProb, Class<? extends Item> superClass, Supplier<Item> make) {
			this.firstProb = firstProb;
			this.secondProb = secondProb;
			this.superClass = superClass;
			this.make = make;
			// Initialize empty arrays that will be populated by registerItem
			this.classes = new Class<?>[0];
			this.probs = new float[0];
		}

		//some generator categories can have ordering within that category as well
		// note that sub category ordering doesn't need to always include items that belong
		// to that categories superclass, e.g. bombs are ordered within thrown weapons
		private static HashMap<Class, ArrayList<Class>> subOrderings = new HashMap<>();
		static {
			subOrderings.put(MissileWeapon.class, new ArrayList<>(Arrays.asList(MissileWeapon.class)));
			subOrderings.put(Potion.class, new ArrayList<>(Arrays.asList(Waterskin.class, Potion.class, ExoticPotion.class)));
			subOrderings.put(Scroll.class, new ArrayList<>(Arrays.asList(Scroll.class, ExoticScroll.class)));
		}

		//in case there are multiple matches, this will return the latest match
		public static int order( Item item ) {
			int catResult = -1, subResult = 0;
			for (int i=0; i < values().length; i++) {
				ArrayList<Class> subOrdering = subOrderings.get(values()[i].superClass);
				if (subOrdering != null){
					for (int j=0; j < subOrdering.size(); j++){
						if (subOrdering.get(j).isInstance(item)){
							catResult = i;
							subResult = j;
						}
					}
				} else {
					if (values()[i].superClass.isInstance(item)) {
						catResult = i;
						subResult = 0;
					}
				}
			}
			if (catResult != -1) return catResult*100 + subResult;

			//items without a category-defined order are sorted based on the spritesheet
			return Short.MAX_VALUE+item.image();
		}
	}

	private static final float[][] floorSetTierProbs = new float[][] {
			{0, 75, 20,  4,  1},
			{0, 25, 50, 20,  5},
			{0,  0, 40, 50, 10},
			{0,  0, 20, 40, 40},
			{0,  0,  0, 20, 80}
	};

	private static boolean usingFirstDeck = false;
	private static HashMap<Category,Float> defaultCatProbs = new LinkedHashMap<>();
	private static HashMap<Category,Float> categoryProbs = new LinkedHashMap<>();
	
	/**
	 * 游戏启动时调用此方法初始化所有物品
	 */
	public static void initializeItems() {
		// 初始化金币
		registerItem(Category.GOLD, Gold.class, 1f);
		
		// 初始化药水（使用双概率一次性注册）
		registerItem(Category.POTION, PotionOfStrength.class, 0f, 0f);
		registerItem(Category.POTION, PotionOfHealing.class, 3f, 3f);
		registerItem(Category.POTION, PotionOfMindVision.class, 2f, 2f);
		registerItem(Category.POTION, PotionOfFrost.class, 1f, 2f);
		registerItem(Category.POTION, PotionOfLiquidFlame.class, 2f, 1f);
		registerItem(Category.POTION, PotionOfToxicGas.class, 1f, 2f);
		registerItem(Category.POTION, PotionOfHaste.class, 1f, 1f);
		registerItem(Category.POTION, PotionOfInvisibility.class, 1f, 1f);
		registerItem(Category.POTION, PotionOfLevitation.class, 1f, 1f);
		registerItem(Category.POTION, PotionOfParalyticGas.class, 1f, 1f);
		registerItem(Category.POTION, PotionOfPurity.class, 1f, 1f);
		registerItem(Category.POTION, PotionOfExperience.class, 1f, 0f);
		
		// 初始化种子
		registerItem(Category.SEED, Rotberry.Seed.class, 0f);
		registerItem(Category.SEED, Sungrass.Seed.class, 2f);
		registerItem(Category.SEED, Fadeleaf.Seed.class, 2f);
		registerItem(Category.SEED, Icecap.Seed.class, 2f);
		registerItem(Category.SEED, Firebloom.Seed.class, 2f);
		registerItem(Category.SEED, Sorrowmoss.Seed.class, 2f);
		registerItem(Category.SEED, Swiftthistle.Seed.class, 2f);
		registerItem(Category.SEED, Blindweed.Seed.class, 2f);
		registerItem(Category.SEED, Stormvine.Seed.class, 2f);
		registerItem(Category.SEED, Earthroot.Seed.class, 2f);
		registerItem(Category.SEED, Mageroyal.Seed.class, 2f);
		registerItem(Category.SEED, Starflower.Seed.class, 1f);
		
		// 初始化卷轴（使用双概率一次性注册）
		registerItem(Category.SCROLL, ScrollOfUpgrade.class, 0f, 0f);
		registerItem(Category.SCROLL, ScrollOfIdentify.class, 3f, 3f);
		registerItem(Category.SCROLL, ScrollOfRemoveCurse.class, 2f, 2f);
		registerItem(Category.SCROLL, ScrollOfMirrorImage.class, 1f, 2f);
		registerItem(Category.SCROLL, ScrollOfRecharging.class, 2f, 1f);
		registerItem(Category.SCROLL, ScrollOfTeleportation.class, 1f, 2f);
		registerItem(Category.SCROLL, ScrollOfLullaby.class, 1f, 1f);
		registerItem(Category.SCROLL, ScrollOfMagicMapping.class, 1f, 1f);
		registerItem(Category.SCROLL, ScrollOfRage.class, 1f, 1f);
		registerItem(Category.SCROLL, ScrollOfRetribution.class, 1f, 1f);
		registerItem(Category.SCROLL, ScrollOfTerror.class, 1f, 1f);
		registerItem(Category.SCROLL, ScrollOfTransmutation.class, 1f, 0f);
		
		// 初始化石头
		registerItem(Category.STONE, StoneOfEnchantment.class, 0f);
		registerItem(Category.STONE, StoneOfIntuition.class, 2f);
		registerItem(Category.STONE, StoneOfDetectMagic.class, 2f);
		registerItem(Category.STONE, StoneOfFlock.class, 2f);
		registerItem(Category.STONE, StoneOfShock.class, 2f);
		registerItem(Category.STONE, StoneOfBlink.class, 2f);
		registerItem(Category.STONE, StoneOfDeepSleep.class, 2f);
		registerItem(Category.STONE, StoneOfClairvoyance.class, 2f);
		registerItem(Category.STONE, StoneOfAggression.class, 2f);
		registerItem(Category.STONE, StoneOfBlast.class, 2f);
		registerItem(Category.STONE, StoneOfFear.class, 2f);
		registerItem(Category.STONE, StoneOfAugmentation.class, 0f);

		
		// 初始化法杖
		registerItem(Category.WAND, WandOfMagicMissile.class, 3f);
		registerItem(Category.WAND, WandOfLightning.class, 3f);
		registerItem(Category.WAND, WandOfDisintegration.class, 3f);
		registerItem(Category.WAND, WandOfFireblast.class, 3f);
		registerItem(Category.WAND, WandOfCorrosion.class, 3f);
		registerItem(Category.WAND, WandOfBlastWave.class, 3f);
		registerItem(Category.WAND, WandOfLivingEarth.class, 3f);
		registerItem(Category.WAND, WandOfFrost.class, 3f);
		registerItem(Category.WAND, WandOfPrismaticLight.class, 3f);
		registerItem(Category.WAND, WandOfWarding.class, 3f);
		registerItem(Category.WAND, WandOfTransfusion.class, 3f);
		registerItem(Category.WAND, WandOfCorruption.class, 3f);
		registerItem(Category.WAND, WandOfRegrowth.class, 3f);

		// 初始化T1武器
		registerTieredItem(Category.WEP_T1, WornShortsword.class, 2f);
		registerTieredItem(Category.WEP_T1, MagesStaff.class, 0f);
		registerTieredItem(Category.WEP_T1, Dagger.class, 2f);
		registerTieredItem(Category.WEP_T1, Gloves.class, 2f);
		registerTieredItem(Category.WEP_T1, Rapier.class, 2f);
		registerTieredItem(Category.WEP_T1, Cudgel.class, 2f);
		
		// 初始化T2武器
		registerTieredItem(Category.WEP_T2, Shortsword.class, 2f);
		registerTieredItem(Category.WEP_T2, HandAxe.class, 2f);
		registerTieredItem(Category.WEP_T2, Spear.class, 2f);
		registerTieredItem(Category.WEP_T2, Quarterstaff.class, 2f);
		registerTieredItem(Category.WEP_T2, Dirk.class, 2f);
		registerTieredItem(Category.WEP_T2, Sickle.class, 2f);
		// registerTieredItem(Category.WEP_T2, Pickaxe.class, 0f);
		
		// 初始化T3武器
		registerTieredItem(Category.WEP_T3, Sword.class, 2f);
		registerTieredItem(Category.WEP_T3, Mace.class, 2f);
		registerTieredItem(Category.WEP_T3, Scimitar.class, 2f);
		registerTieredItem(Category.WEP_T3, RoundShield.class, 2f);
		registerTieredItem(Category.WEP_T3, Sai.class, 2f);
		registerTieredItem(Category.WEP_T3, Whip.class, 2f);

		
		// 初始化T4武器
		registerTieredItem(Category.WEP_T4, Longsword.class, 2f);
		registerTieredItem(Category.WEP_T4, BattleAxe.class, 2f);
		registerTieredItem(Category.WEP_T4, Flail.class, 2f);
		registerTieredItem(Category.WEP_T4, RunicBlade.class, 2f);
		registerTieredItem(Category.WEP_T4, AssassinsBlade.class, 2f);
		registerTieredItem(Category.WEP_T4, Crossbow.class, 2f);
		registerTieredItem(Category.WEP_T4, Katana.class, 2f);
		
		// 初始化T5武器
		registerTieredItem(Category.WEP_T5, Greatsword.class, 2f);
		registerTieredItem(Category.WEP_T5, WarHammer.class, 2f);
		registerTieredItem(Category.WEP_T5, Glaive.class, 2f);
		registerTieredItem(Category.WEP_T5, Greataxe.class, 2f);
		registerTieredItem(Category.WEP_T5, Greatshield.class, 2f);
		registerTieredItem(Category.WEP_T5, Gauntlet.class, 2f);
		registerTieredItem(Category.WEP_T5, WarScythe.class, 2f);
		
		// 初始化防具
		registerItem(Category.ARMOR, ClothArmor.class, 1f);
		registerItem(Category.ARMOR, LeatherArmor.class, 1f);
		registerItem(Category.ARMOR, MailArmor.class, 1f);
		registerItem(Category.ARMOR, ScaleArmor.class, 1f);
		registerItem(Category.ARMOR, PlateArmor.class, 1f);
		registerItem(Category.ARMOR, WarriorArmor.class, 0f);
		registerItem(Category.ARMOR, MageArmor.class, 0f);
		registerItem(Category.ARMOR, RogueArmor.class, 0f);
		registerItem(Category.ARMOR, HuntressArmor.class, 0f);
		registerItem(Category.ARMOR, DuelistArmor.class, 0f);
		registerItem(Category.ARMOR, ClericArmor.class, 0f);
		
		// 初始化T1远程武器
		registerTieredItem(Category.MIS_T1, ThrowingStone.class, 3f);
		registerTieredItem(Category.MIS_T1, ThrowingKnife.class, 3f);
		registerTieredItem(Category.MIS_T1, ThrowingSpike.class, 3f);
		registerTieredItem(Category.MIS_T1, Dart.class, 0f);
		
		// 初始化T2远程武器
		registerTieredItem(Category.MIS_T2, FishingSpear.class, 3f);
		registerTieredItem(Category.MIS_T2, ThrowingClub.class, 3f);
		registerTieredItem(Category.MIS_T2, Shuriken.class, 3f);
		
		// 初始化T3远程武器
		registerTieredItem(Category.MIS_T3, ThrowingSpear.class, 3f);
		registerTieredItem(Category.MIS_T3, Kunai.class, 3f);
		registerTieredItem(Category.MIS_T3, Bolas.class, 3f);
		
		// 初始化T4远程武器
		registerTieredItem(Category.MIS_T4, Javelin.class, 3f);
		registerTieredItem(Category.MIS_T4, Tomahawk.class, 3f);
		registerTieredItem(Category.MIS_T4, HeavyBoomerang.class, 3f);
		
		// 初始化T5远程武器
		registerTieredItem(Category.MIS_T5, Trident.class, 3f);
		registerTieredItem(Category.MIS_T5, ThrowingHammer.class, 3f);
		registerTieredItem(Category.MIS_T5, ForceCube.class, 3f);
		
		// 初始化食物
		registerItem(Category.FOOD, Food.class, 4f);
		registerItem(Category.FOOD, Pasty.class, 1f);
		registerItem(Category.FOOD, MysteryMeat.class, 0f);
		
		// 初始化戒指
		registerItem(Category.RING, RingOfAccuracy.class, 3f);
		registerItem(Category.RING, RingOfArcana.class, 3f);
		registerItem(Category.RING, RingOfElements.class, 3f);
		registerItem(Category.RING, RingOfEnergy.class, 3f);
		registerItem(Category.RING, RingOfEvasion.class, 3f);
		registerItem(Category.RING, RingOfForce.class, 3f);
		registerItem(Category.RING, RingOfFuror.class, 3f);
		registerItem(Category.RING, RingOfHaste.class, 3f);
		registerItem(Category.RING, RingOfMight.class, 3f);
		registerItem(Category.RING, RingOfSharpshooting.class, 3f);
		registerItem(Category.RING, RingOfTenacity.class, 3f);
		registerItem(Category.RING, RingOfWealth.class, 3f);
		
		// 初始化神器
		registerItem(Category.ARTIFACT, AlchemistsToolkit.class, 1f);
		registerItem(Category.ARTIFACT, ChaliceOfBlood.class, 1f);
		registerItem(Category.ARTIFACT, CloakOfShadows.class, 0f);
		registerItem(Category.ARTIFACT, DriedRose.class, 1f);
		registerItem(Category.ARTIFACT, EtherealChains.class, 1f);
		registerItem(Category.ARTIFACT, HolyTome.class, 0f);
		registerItem(Category.ARTIFACT, HornOfPlenty.class, 1f);
		registerItem(Category.ARTIFACT, MasterThievesArmband.class, 1f);
		registerItem(Category.ARTIFACT, SandalsOfNature.class, 1f);
		registerItem(Category.ARTIFACT, TalismanOfForesight.class, 1f);
		registerItem(Category.ARTIFACT, TimekeepersHourglass.class, 1f);
		registerItem(Category.ARTIFACT, UnstableSpellbook.class, 1f);
		
		// 初始化饰品
		registerItem(Category.TRINKET, ExoticCrystals.class, 1f);
		registerItem(Category.TRINKET, RatSkull.class, 1f);
		registerItem(Category.TRINKET, ParchmentScrap.class, 1f);
		registerItem(Category.TRINKET, PetrifiedSeed.class, 1f);
		registerItem(Category.TRINKET, MossyClump.class, 1f);
		registerItem(Category.TRINKET, DimensionalSundial.class, 1f);
		registerItem(Category.TRINKET, ThirteenLeafClover.class, 1f);
		registerItem(Category.TRINKET, TrapMechanism.class, 1f);
		registerItem(Category.TRINKET, MimicTooth.class, 1f);
		registerItem(Category.TRINKET, WondrousResin.class, 1f);
		registerItem(Category.TRINKET, EyeOfNewt.class, 1f);
		registerItem(Category.TRINKET, SaltCube.class, 1f);
		registerItem(Category.TRINKET, VialOfBlood.class, 1f);
		registerItem(Category.TRINKET, ShardOfOblivion.class, 1f);
		registerItem(Category.TRINKET, ChaoticCenser.class, 1f);
		
		// 初始化自定义物品类别
		// 注意：自定义物品已经在 CustomItem 类的静态初始化块中注册了
		// 这里我们需要将每个自定义物品单独添加到生成器系统中
		/*
		String[] customItemKeys = CustomItem.item_records.keySet().toArray(new String[0]);
		for (String key : customItemKeys) {
			final String itemKey = key; // Create final copy for lambda
			registerItem(Category.CUSTOM_ITEM, CustomItem.class, 1f, 0f, () -> {
				return new CustomItem(itemKey);
			});
		}
		
		// 初始化自定义食物类别
		// 注意：自定义食物已经在 CustomFood 类的静态初始化块中注册了
		String[] customFoodKeys = com.coladungeon.items.food.CustomFood.food_records.keySet().toArray(new String[0]);
		for (String key : customFoodKeys) {
			final String foodKey = key; // Create final copy for lambda
			registerItem(Category.CUSTOM_FOOD, com.coladungeon.items.food.CustomFood.class, 1f, 0f, () -> {
				return new com.coladungeon.items.food.CustomFood(foodKey);
			});
		}
		*/
		
		// 更新有两套概率的类别的总概率
		updateCategoryTotalProbs();
	}
	
	/**
	 * 更新所有有两套概率的类别的总概率
	 */
	private static void updateCategoryTotalProbs() {
			for (Category cat : Category.values()){
			if (cat.defaultProbs2 != null && cat.defaultProbs != null){
					cat.defaultProbsTotal = new float[cat.defaultProbs.length];
					for (int i = 0; i < cat.defaultProbs.length; i++){
						cat.defaultProbsTotal[i] = cat.defaultProbs[i] + cat.defaultProbs2[i];
					}
				}
			}
		}
	
	/**
	 * 注册带有双概率的物品（有些物品有两种概率分布）
	 */
	public static boolean registerItemWithDualProbabilities(Category category, Class<? extends Item> itemClass, float probability1, float probability2) {
		return registerItem(category, itemClass, probability1, probability2);
	}

	public static void fullReset() {
		usingFirstDeck = Random.Int(2) == 0;
		generalReset();
		
		// 清空并初始化所有物品类别
		initializeItems();
		
		for (Category cat : Category.values()) {
			cat.using2ndProbs =  cat.defaultProbs2 != null && Random.Int(2) == 0;
			reset(cat);
			if (cat.defaultProbs != null) {
				cat.seed = Random.Long();
				cat.dropped = 0;
			}
		}
	}

	public static void generalReset(){
		for (Category cat : Category.values()) {
			categoryProbs.put( cat, usingFirstDeck ? cat.firstProb : cat.secondProb );
			defaultCatProbs.put( cat, cat.firstProb + cat.secondProb );
		}
	}

	public static void reset(Category cat){
		if (cat.defaultProbs != null) {
			if (cat.defaultProbs2 != null){
				cat.using2ndProbs = !cat.using2ndProbs;
				cat.probs = cat.using2ndProbs ? cat.defaultProbs2.clone() : cat.defaultProbs.clone();
			} else {
				cat.probs = cat.defaultProbs.clone();
			}
		}
	}

	//reverts changes to drop chances generates by this item
	//equivalent of shuffling the card back into the deck, does not preserve order!
	public static void undoDrop(Item item){
		undoDrop(item.getClass());
	}

	public static void undoDrop(Class cls){
		for (Category cat : Category.values()){
			if (cls.isAssignableFrom(cat.superClass)){
				if (cat.defaultProbs == null) continue;
				for (int i = 0; i < cat.classes.length; i++){
					if (cls == cat.classes[i]){
						cat.probs[i]++;
					}
				}
			}
		}
	}
	
	public static Item random() {
		Category cat = Random.chances( categoryProbs );
		if (cat == null){
			usingFirstDeck = !usingFirstDeck;
			generalReset();
			cat = Random.chances( categoryProbs );
		}
		categoryProbs.put( cat, categoryProbs.get( cat ) - 1);

		if (cat == Category.SEED) {
			//We specifically use defaults for seeds here because, unlike other item categories
			// their predominant source of drops is grass, not levelgen. This way the majority
			// of seed drops still use a deck, but the few that are spawned by levelgen are consistent
			return randomUsingDefaults(cat);
		} else {
			return random(cat);
		}
	}

	public static Item randomUsingDefaults(){
		return randomUsingDefaults(Random.chances( defaultCatProbs ));
	}
	
	public static Item random( Category cat ) {
		switch (cat) {
			case ARMOR:
				return randomArmor();
			case WEAPON:
				return randomWeapon();
			case MISSILE:
				return randomMissile();
			case ARTIFACT:
				Item item = randomArtifact();
				//if we're out of artifacts, return a ring instead.
				return item != null ? item : random(Category.RING);
			case CUSTOM_ITEM:
				return randomCustomItem();
			case CUSTOM_FOOD:
				return randomCustomFood();
			case TRINKET:
				// For now, return a basic item from the trinket category
				// This will be improved in future updates
				if (cat.classes.length > 0) {
					int idx = Random.chances(cat.probs);
					if (idx != -1) {
						@SuppressWarnings("unchecked")
						Class<? extends Item> cls = (Class<? extends Item>) cat.classes[idx];
						return random(cls);
					}
				}
				return new Gold(); // Fallback
			default:
				if (cat.defaultProbs != null && cat.seed != null){
					Random.pushGenerator(cat.seed);
					for (int i = 0; i < cat.dropped; i++) Random.Long();
				}

				int i = Random.chances(cat.probs);
				if (i == -1) {
					reset(cat);
					i = Random.chances(cat.probs);
				}
				if (cat.defaultProbs != null) cat.probs[i]--;
				Class<?> itemCls = cat.classes[i];

				if (cat.defaultProbs != null && cat.seed != null){
					Random.popGenerator();
					cat.dropped++;
				}

				if (ExoticPotion.regToExo.containsKey(itemCls)){
					if (Random.Float() < ExoticCrystals.consumableExoticChance()){
						itemCls = ExoticPotion.regToExo.get(itemCls);
					}
				} else if (ExoticScroll.regToExo.containsKey(itemCls)){
					if (Random.Float() < ExoticCrystals.consumableExoticChance()){
						itemCls = ExoticScroll.regToExo.get(itemCls);
					}
				}

				// 使用make生成函数（如果存在且适合当前选中的itemCls）
				// 重要：make供应器是类别级别的，但我们选择的是特定的itemCls
				// 所以只有当make是专门为当前itemCls设置的时候才使用它
				try {
					// 优先使用反射安全创建物品实例，这是最可靠的方法
					return ((Item) Reflection.newInstance(itemCls)).random();
				} catch (Exception e) {
					// 如果反射失败（不太可能），尝试使用make供应器作为备选
					if (cat.make != null) {
						return cat.make.get();
					} else {
						// 如果都失败，返回一个安全的默认物品
						return new Gold();
					}
				}
		}
	}

	//overrides any deck systems and always uses default probs
	// except for artifacts, which must always use a deck
	public static Item randomUsingDefaults( Category cat ){
		if (cat == Category.WEAPON){
			return randomWeapon(true);
		} else if (cat == Category.MISSILE){
			return randomMissile(true);
		} else if (cat.defaultProbs == null || cat == Category.ARTIFACT) {
			return random(cat);
		} else if (cat.defaultProbsTotal != null){
			try {
				Class<?> itemCls = cat.classes[Random.chances(cat.defaultProbsTotal)];
				return ((Item) Reflection.newInstance(itemCls)).random();
			} catch (Exception e) {
				// 如果反射失败，使用备选生成方法
				if (cat.make != null) {
					return cat.make.get();
				} else {
					return new Gold();
				}
			}
		} else {
			Class<?> itemCls = cat.classes[Random.chances(cat.defaultProbs)];

			if (ExoticPotion.regToExo.containsKey(itemCls)){
				if (Random.Float() < ExoticCrystals.consumableExoticChance()){
					itemCls = ExoticPotion.regToExo.get(itemCls);
				}
			} else if (ExoticScroll.regToExo.containsKey(itemCls)){
				if (Random.Float() < ExoticCrystals.consumableExoticChance()){
					itemCls = ExoticScroll.regToExo.get(itemCls);
				}
			}

			try {
				return ((Item) Reflection.newInstance(itemCls)).random();
			} catch (Exception e) {
				// 如果反射失败，使用备选生成方法
				if (cat.make != null) {
					return cat.make.get();
				} else {
					return new Gold();
				}
			}
		}
	}
	
	public static Item random( Class<? extends Item> cl ) {
		return Reflection.newInstance(cl).random();
	}

	public static Armor randomArmor(){
		return randomArmor(Dungeon.depth / 5);
	}
	
	public static Armor randomArmor(int floorSet) {

		floorSet = (int)GameMath.gate(0, floorSet, floorSetTierProbs.length-1);
		
		Armor a = (Armor)Reflection.newInstance(Category.ARMOR.classes[Random.chances(floorSetTierProbs[floorSet])]);
		a.random();
		return a;
	}

	public static final Category[] wepTiers = new Category[]{
			Category.WEP_T1,
			Category.WEP_T2,
			Category.WEP_T3,
			Category.WEP_T4,
			Category.WEP_T5
	};

	public static MeleeWeapon randomWeapon(){
		return randomWeapon(Dungeon.depth / 5);
	}

	public static MeleeWeapon randomWeapon(int floorSet) {
		return randomWeapon(floorSet, false);
	}

	public static MeleeWeapon randomWeapon(boolean useDefaults) {
		return randomWeapon(Dungeon.depth / 5, useDefaults);
	}
	
	public static MeleeWeapon randomWeapon(int floorSet, boolean useDefaults) {

		floorSet = (int)GameMath.gate(0, floorSet, floorSetTierProbs.length-1);

		MeleeWeapon w;
		if (useDefaults){
			w = (MeleeWeapon) randomUsingDefaults(wepTiers[Random.chances(floorSetTierProbs[floorSet])]);
		} else {
			w = (MeleeWeapon) random(wepTiers[Random.chances(floorSetTierProbs[floorSet])]);
		}
		return w;
	}
	
	public static final Category[] misTiers = new Category[]{
			Category.MIS_T1,
			Category.MIS_T2,
			Category.MIS_T3,
			Category.MIS_T4,
			Category.MIS_T5
	};
	
	public static MissileWeapon randomMissile(){
		return randomMissile(Dungeon.depth / 5);
	}

	public static MissileWeapon randomMissile(int floorSet) {
		return randomMissile(floorSet, false);
	}

	public static MissileWeapon randomMissile(boolean useDefaults) {
		return randomMissile(Dungeon.depth / 5, useDefaults);
	}

	public static MissileWeapon randomMissile(int floorSet, boolean useDefaults) {
		
		floorSet = (int)GameMath.gate(0, floorSet, floorSetTierProbs.length-1);

		MissileWeapon w;
		if (useDefaults){
			w = (MissileWeapon)randomUsingDefaults(misTiers[Random.chances(floorSetTierProbs[floorSet])]);
		} else {
			w = (MissileWeapon)random(misTiers[Random.chances(floorSetTierProbs[floorSet])]);
		}
		return w;
	}

	//enforces uniqueness of artifacts throughout a run.
	public static Artifact randomArtifact() {

		Category cat = Category.ARTIFACT;

		if (cat.defaultProbs != null && cat.seed != null){
			Random.pushGenerator(cat.seed);
			for (int i = 0; i < cat.dropped; i++) Random.Long();
		}

		int i = Random.chances( cat.probs );

		if (cat.defaultProbs != null && cat.seed != null){
			Random.popGenerator();
			cat.dropped++;
		}

		//if no artifacts are left, return null
		if (i == -1){
			return null;
		}

		cat.probs[i]--;
		return (Artifact) Reflection.newInstance((Class<? extends Artifact>) cat.classes[i]).random();

	}

	public static boolean removeArtifact(Class<?extends Artifact> artifact) {
		Category cat = Category.ARTIFACT;
		for (int i = 0; i < cat.classes.length; i++){
			if (cat.classes[i].equals(artifact) && cat.probs[i] > 0) {
				cat.probs[i] = 0;
				return true;
			}
		}
		return false;
	}

	private static final String FIRST_DECK = "first_deck";
	private static final String GENERAL_PROBS = "general_probs";
	private static final String CATEGORY_PROBS = "_probs";
	private static final String CATEGORY_USING_PROBS2 = "_using_probs2";
	private static final String CATEGORY_SEED = "_seed";
	private static final String CATEGORY_DROPPED = "_dropped";

	public static void storeInBundle(Bundle bundle) {
		bundle.put(FIRST_DECK, usingFirstDeck);

		Float[] genProbs = categoryProbs.values().toArray(new Float[0]);
		float[] storeProbs = new float[genProbs.length];
		for (int i = 0; i < storeProbs.length; i++){
			storeProbs[i] = genProbs[i];
		}
		bundle.put( GENERAL_PROBS, storeProbs);

		for (Category cat : Category.values()){
			if (cat.defaultProbs == null) continue;

			bundle.put(cat.name().toLowerCase() + CATEGORY_PROBS, cat.probs);

			if (cat.defaultProbs2 != null){
				bundle.put(cat.name().toLowerCase() + CATEGORY_USING_PROBS2, cat.using2ndProbs);
			}

			if (cat.seed != null) {
				bundle.put(cat.name().toLowerCase() + CATEGORY_SEED, cat.seed);
				bundle.put(cat.name().toLowerCase() + CATEGORY_DROPPED, cat.dropped);
			}
		}
	}

	public static void restoreFromBundle(Bundle bundle) {
		fullReset();

		usingFirstDeck = bundle.getBoolean(FIRST_DECK);

		//restore category probs
		for (Category cat : Category.values()){
			if (bundle.contains(cat.name().toLowerCase() + CATEGORY_PROBS)){
				cat.probs = bundle.getFloatArray(cat.name().toLowerCase() + CATEGORY_PROBS);
			}
			
			if (bundle.contains(cat.name().toLowerCase() + CATEGORY_USING_PROBS2)){
				cat.using2ndProbs = bundle.getBoolean(cat.name().toLowerCase() + CATEGORY_USING_PROBS2);
			}
			
			if (bundle.contains(cat.name().toLowerCase() + CATEGORY_SEED)){
				cat.seed = bundle.getLong(cat.name().toLowerCase() + CATEGORY_SEED);
			}
			
			if (bundle.contains(cat.name().toLowerCase() + CATEGORY_DROPPED)){
				cat.dropped = bundle.getInt(cat.name().toLowerCase() + CATEGORY_DROPPED);
			}
		}
		
		//restore general category probs
		if (bundle.contains(GENERAL_PROBS)){
			float[] probs = bundle.getFloatArray(GENERAL_PROBS);
			if (probs.length == Category.values().length) {
				for (int i = 0; i < probs.length; i++) {
					categoryProbs.put(Category.values()[i], probs[i]);
				}
			}
		}
	}
	
	/**
	 * Registers a new item to a specific category at runtime.
	 * This allows for adding custom items to the generator system.
	 * 
	 * @param category the category to add the item to
	 * @param itemClass the class of the item to add
	 * @param probability the probability of the item appearing (relative to other items in the category)
	 * @return true if the item was successfully registered, false otherwise
	 */
	public static boolean registerItem(Category category, Class<? extends Item> itemClass, float probability) {
		return registerItem(category, itemClass, probability, 0);
	}
	
	/**
	 * Registers a new item to a specific tier category for weapons or missiles.
	 * 
	 * @param tierCategory the tier category to add to (e.g., WEP_T1, MIS_T3)
	 * @param itemClass the class of the item to add
	 * @param probability the probability of the item appearing
	 * @return true if registered successfully, false otherwise
	 */
	public static boolean registerTieredItem(Category tierCategory, Class<? extends Item> itemClass, float probability) {
		// 验证这是一个层级类别
		boolean isWeaponTier = Arrays.asList(wepTiers).contains(tierCategory);
		boolean isMissileTier = Arrays.asList(misTiers).contains(tierCategory);
		
		if (!isWeaponTier && !isMissileTier) {
			return false;
		}
		
		return registerItem(tierCategory, itemClass, probability);
	}



	/**
	 * 检查指定的类别是否包含指定的类型
	 * @param category 要检查的类别
	 * @param type 要检查的类型
	 * @return 如果类别包含该类型，则返回 true
	 */
	public static boolean categoryContainsType(Category category, Class<?> type) {
		if (category == null || type == null || category.classes == null) {
			return false;
		}
		
		for (Class<?> cls : category.classes) {
			if (cls.equals(type)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * 注册一个物品到生成系统
	 * @param category 物品类别
	 * @param itemClass 物品类型
	 * @param probability1 第一套概率
	 * @param probability2 第二套概率（如果类别使用双概率系统）
	 * @return 是否成功注册
	 */
	public static boolean registerItem(Category category, Class<? extends Item> itemClass, float probability1, float probability2) {
		if (category == null || itemClass == null || probability1 < 0 || probability2 < 0) {
			return false;
		}
		
		// 设置物品生成函数，包含错误处理
		category.make = () -> {
			try {
				return (Item)Reflection.newInstance(itemClass).random();
			} catch (Exception e) {
				// 如果实例化失败，返回一个安全的默认物品
				return new Gold();
			}
		};
		
		// 检查物品是否已经存在
		for (int i = 0; i < category.classes.length; i++) {
			if (category.classes[i].equals(itemClass)) {
				// 更新概率
				category.probs[i] = probability1;
				if (category.defaultProbs != null) {
					category.defaultProbs[i] = probability1;
				}
				
				// 如果有第二组概率，也更新
				if (category.defaultProbs2 != null) {
					category.defaultProbs2[i] = probability2;
					
					// 更新总概率
					if (category.defaultProbsTotal != null) {
						category.defaultProbsTotal[i] = probability1 + probability2;
					}
				}
				
				return true;
			}
		}
		
		// 创建新数组，增加一个位置
		Class<?>[] newClasses = new Class<?>[category.classes.length + 1];
		float[] newProbs = new float[category.probs.length + 1];
		
		// 复制现有元素
		System.arraycopy(category.classes, 0, newClasses, 0, category.classes.length);
		System.arraycopy(category.probs, 0, newProbs, 0, category.probs.length);
		
		// 添加新项目
		newClasses[newClasses.length - 1] = itemClass;
		newProbs[newProbs.length - 1] = probability1;
		
		// 更新类别
		category.classes = newClasses;
		category.probs = newProbs;
		
		// 更新默认概率
		if (category.defaultProbs == null) {
			category.defaultProbs = new float[newProbs.length];
			System.arraycopy(newProbs, 0, category.defaultProbs, 0, newProbs.length);
		} else {
			float[] newDefaultProbs = new float[category.defaultProbs.length + 1];
			System.arraycopy(category.defaultProbs, 0, newDefaultProbs, 0, category.defaultProbs.length);
			newDefaultProbs[newDefaultProbs.length - 1] = probability1;
			category.defaultProbs = newDefaultProbs;
		}
		
		// 处理第二组概率（如果需要）
		if (category.defaultProbs2 != null || probability2 > 0) {
			if (category.defaultProbs2 == null) {
				category.defaultProbs2 = new float[newProbs.length];
				// 如果之前没有第二组概率，复制第一组作为基础
				System.arraycopy(category.defaultProbs, 0, category.defaultProbs2, 0, category.defaultProbs.length - 1);
				category.defaultProbs2[category.defaultProbs2.length - 1] = probability2;
			} else {
				float[] newDefaultProbs2 = new float[category.defaultProbs2.length + 1];
				System.arraycopy(category.defaultProbs2, 0, newDefaultProbs2, 0, category.defaultProbs2.length);
				newDefaultProbs2[newDefaultProbs2.length - 1] = probability2;
				category.defaultProbs2 = newDefaultProbs2;
			}
			
			// 更新总概率
			if (category.defaultProbsTotal == null) {
				category.defaultProbsTotal = new float[newProbs.length];
			} else {
				float[] newTotalProbs = new float[category.defaultProbsTotal.length + 1];
				System.arraycopy(category.defaultProbsTotal, 0, newTotalProbs, 0, category.defaultProbsTotal.length);
				category.defaultProbsTotal = newTotalProbs;
			}
			
			category.defaultProbsTotal[category.defaultProbsTotal.length - 1] = probability1 + probability2;
		}
		
		return true;
	}

	/**
	 * 使用自定义生成函数注册物品
	 */
	public static boolean registerItem(Category category, Class<? extends Item> itemClass, float probability1, float probability2, Supplier<Item> makeFunction) {
		boolean result = registerItem(category, itemClass, probability1, probability2);
		if (result) {
			category.make = makeFunction;
		}
		return result;
	}

	/**
	 * 简化版的注册方法，直接使用生成函数
	 */
	public static boolean registerItem(Category category, String itemId, Supplier<Item> makeFunction, float probability1, float probability2) {
		try {
			// 创建一个虚拟类来保持与现有系统的兼容性
			Class<?> dummyClass = Gold.class; // Use Gold class instead of CustomItem
			boolean result = registerItem(category, (Class<? extends Item>)dummyClass, probability1, probability2);
			if (result) {
				category.make = makeFunction;
			}
			return result;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Placeholder method for future implementation of custom item generation
	 */
	private static Item randomCustomItem() {
		// This will be implemented in future updates
		return new Gold();
	}

	/**
	 * Placeholder method for future implementation of custom food generation
	 */
	private static Item randomCustomFood() {
		// This will be implemented in future updates
		return new Food();
	}
}
