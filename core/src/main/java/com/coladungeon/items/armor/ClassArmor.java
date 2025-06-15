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

package com.coladungeon.items.armor;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.Badges;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Regeneration;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.hero.HeroClass;
import com.coladungeon.actors.hero.HeroClassSheet;
import com.coladungeon.effects.Speck;
import com.coladungeon.items.Item;
import com.coladungeon.items.rings.RingOfEnergy;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.HeroSprite;
import com.coladungeon.sprites.ItemSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndBag;
import com.coladungeon.windows.WndChooseAbility;
import com.coladungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

abstract public class ClassArmor extends Armor {

	private static final String AC_ABILITY = "ABILITY";
	private static final String AC_TRANSFER = "TRANSFER";
	
	protected HeroClass owner;
	
	{
		levelKnown = true;
		cursedKnown = true;
		defaultAction = AC_ABILITY;

		bones = false;
	}

	private Charger charger;
	public float charge = 0;
	
	public ClassArmor() {
		super( 5 );
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (ch instanceof Hero) {
			owner = ((Hero)ch).heroClass;
		}
		charger = new Charger();
		charger.attachTo(ch);
	}

	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		if (super.doUnequip( hero, collect, single )) {
			if (charger != null){
				charger.detach();
				charger = null;
			}
			return true;

		} else {
			return false;

		}
	}

	@Override
	public int targetingPos(Hero user, int dst) {
		return user.armorAbility.targetedPos(user, dst);
	}

	public static ClassArmor upgrade (Hero owner, Armor armor ) {
		
		ClassArmor classArmor = null;
		
		if (owner.heroClass == HeroClass.WARRIOR) {
				classArmor = new WarriorArmor();
		} else if (owner.heroClass == HeroClassSheet.ROGUE) {
				classArmor = new RogueArmor();
		} else if (owner.heroClass == HeroClassSheet.MAGE) {
				classArmor = new MageArmor();
		} else if (owner.heroClass == HeroClassSheet.HUNTRESS) {
				classArmor = new HuntressArmor();
		} else if (owner.heroClass == HeroClassSheet.DUELIST) {
				classArmor = new DuelistArmor();
		} else if (owner.heroClass == HeroClassSheet.CLERIC) {
				classArmor = new ClericArmor();
		} else{
			System.out.println("[ClassArmor::upgrade]Unknown hero class: " + owner.heroClass);
			classArmor = new WarriorArmor();
		}
		
		classArmor.level(armor.trueLevel());
		classArmor.tier = armor.tier;
		classArmor.augment = armor.augment;
		classArmor.inscribe(armor.glyph);
		if (armor.seal != null) {
			classArmor.seal = armor.seal;
		}
		classArmor.glyphHardened = armor.glyphHardened;
		classArmor.cursed = armor.cursed;
		classArmor.curseInfusionBonus = armor.curseInfusionBonus;
		classArmor.masteryPotionBonus = armor.masteryPotionBonus;
		if (armor.levelKnown && armor.cursedKnown) {
			classArmor.identify();
		} else {
			classArmor.levelKnown = armor.levelKnown;
			classArmor.cursedKnown = true;
		}

		classArmor.charge = 50;
		
		classArmor.owner = owner.heroClass;
		
		return classArmor;
	}

	private static final String ARMOR_TIER	= "armortier";
	private static final String CHARGE	    = "charge";
	private static final String OWNER       = "owner";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ARMOR_TIER, tier );
		bundle.put( CHARGE, charge );
		if (owner != null) {
			bundle.put( OWNER, owner );
		}
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		tier = bundle.getInt( ARMOR_TIER );
		charge = bundle.getFloat(CHARGE);
		if (bundle.contains(OWNER)) {
			owner = (HeroClass) bundle.get(OWNER);
		}
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero )) {
			actions.add( AC_ABILITY );
		}
		actions.add( AC_TRANSFER );
		return actions;
	}

	@Override
	public String actionName(String action, Hero hero) {
		if (hero.armorAbility != null && action.equals(AC_ABILITY)){
			return Messages.upperCase(hero.armorAbility.name());
		} else {
			return super.actionName(action, hero);
		}
	}

	@Override
	public String status() {
		return Messages.format( "%.0f%%", Math.floor(charge) );
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals(AC_ABILITY)){

			if (hero.armorAbility == null){
				GameScene.show(new WndChooseAbility(null, this, hero));
			} else if (!isEquipped( hero )) {
				usesTargeting = false;
				GLog.w( Messages.get(this, "not_equipped") );
			} else if (charge < hero.armorAbility.chargeUse(hero)) {
				usesTargeting = false;
				GLog.w( Messages.get(this, "low_charge") );
			} else  {
				usesTargeting = hero.armorAbility.useTargeting();
				hero.armorAbility.use(this, hero);
			}
			
		} else if (action.equals(AC_TRANSFER)){

			GameScene.show(new WndOptions(new ItemSprite(ItemSpriteSheet.CROWN),
					Messages.get(ClassArmor.class, "transfer_title"),
					Messages.get(ClassArmor.class, "transfer_desc"),
					Messages.get(ClassArmor.class, "transfer_prompt"),
					Messages.get(ClassArmor.class, "transfer_cancel")){
				@Override
				protected void onSelect(int index) {
					if (index == 0){
						GameScene.selectItem(new WndBag.ItemSelector() {
							@Override
							public String textPrompt() {
								return Messages.get(ClassArmor.class, "transfer_prompt");
							}

							@Override
							public boolean itemSelectable(Item item) {
								return item instanceof Armor;
							}

							@Override
							public void onSelect(Item item) {
								if (item == null || item == ClassArmor.this) return;

								Armor armor = (Armor)item;
								armor.detach(hero.belongings.backpack);
								if (hero.belongings.armor == armor){
									hero.belongings.armor = null;
									if (hero.sprite instanceof HeroSprite) {
										((HeroSprite) hero.sprite).updateArmor();
									}
								}
								level(armor.trueLevel());
								tier = armor.tier;
								augment = armor.augment;
								cursed = armor.cursed;
								curseInfusionBonus = armor.curseInfusionBonus;
								masteryPotionBonus = armor.masteryPotionBonus;
								if (armor.checkSeal() != null) {
									inscribe(armor.glyph);
									seal = armor.checkSeal();
								} else if (checkSeal() != null){
									//automates the process of detaching the seal manually
									// and re-affixing it to the new armor
									if (seal.level() > 0){
										int newLevel = trueLevel() + 1;
										level(newLevel);
										Badges.validateItemLevelAquired(ClassArmor.this);
									}

									//if both source and destination armor have glyphs
									// we assume the player wants the glyph on the destination armor
									// they can always manually detach first if they don't.
									// otherwise we automate glyph transfer just like upgrades
									if (armor.glyph == null && seal.canTransferGlyph()){
										//do nothing, keep our glyph
									} else {
										inscribe(armor.glyph);
										seal.setGlyph(null);
									}
								} else {
									inscribe(armor.glyph);
								}

								if (armor.levelKnown && armor.cursedKnown) {
									identify();
								} else {
									levelKnown = armor.levelKnown;
									cursedKnown = true;
								}

								GLog.p( Messages.get(ClassArmor.class, "transfer_complete") );
								hero.sprite.operate(hero.pos);
								hero.sprite.emitter().burst( Speck.factory( Speck.CROWN), 12 );
								Sample.INSTANCE.play( Assets.Sounds.EVOKE );
								hero.spend(Actor.TICK);
								hero.busy();

							}
						});
					}
				}
			});

		}
	}

	public Badges.Badge masteryBadge() {
		if (owner.equals(HeroClass.WARRIOR)) {
			return Badges.Badge.MASTERY_WARRIOR;
		} else if (owner.equals(HeroClass.MAGE)) {
			return Badges.Badge.MASTERY_MAGE;
		} else if (owner.equals(HeroClass.ROGUE)) {
			return Badges.Badge.MASTERY_ROGUE;
		} else if (owner.equals(HeroClass.HUNTRESS)) {
			return Badges.Badge.MASTERY_HUNTRESS;
		} else if (owner.equals(HeroClass.DUELIST)) {
			return Badges.Badge.MASTERY_DUELIST;
		} else if (owner.equals(HeroClass.CLERIC)) {
			return Badges.Badge.MASTERY_CLERIC;
		} 
		return null;
	}

	@Override
	public String desc() {
		String desc = Messages.get(this, "desc");

		if (Badges.isUnlocked(masteryBadge()) && owner != null){
			if (owner.equals(HeroClass.WARRIOR)) {
				desc += "\n\n" + Messages.get(this, "desc_warrior");
			} else if (owner.equals(HeroClass.ROGUE)) {
				desc += "\n\n" + Messages.get(this, "desc_rogue");
			} else if (owner.equals(HeroClass.MAGE)) {
				desc += "\n\n" + Messages.get(this, "desc_mage");
			} else if (owner.equals(HeroClass.HUNTRESS)) {
				desc += "\n\n" + Messages.get(this, "desc_huntress");
			} else if (owner.equals(HeroClass.DUELIST)) {
				desc += "\n\n" + Messages.get(this, "desc_duelist");
			} else if (owner.equals(HeroClass.CLERIC)) {
				desc += "\n\n" + Messages.get(this, "desc_cleric");
				}

		} else if (masteryBadge() != null) {
			desc += "\n\n" + Messages.get(Badges.class, "need_to_win");
		}

		return desc;
	}
	
	@Override
	public int value() {
		return 0;
	}

	public class Charger extends Buff {

		@Override
		public boolean attachTo( Char target ) {
			if (super.attachTo( target )) {
				//if we're loading in and the hero has partially spent a turn, delay for 1 turn
				if (target instanceof Hero && Dungeon.hero == null && cooldown() == 0 && target.cooldown() > 0) {
					spend(TICK);
				}
				return true;
			}
			return false;
		}

		@Override
		public boolean act() {
			if (Regeneration.regenOn()) {
				float chargeGain = 100 / 500f; //500 turns to full charge
				chargeGain *= RingOfEnergy.armorChargeMultiplier(target);
				charge += chargeGain;
				updateQuickslot();
				if (charge > 100) {
					charge = 100;
				}
			}
			spend(TICK);
			return true;
		}
	}

	public boolean canUse( Hero hero ) {
		if (hero.belongings.armor != this) {
			return false;
		}
		
		if (hero.heroClass == HeroClass.WARRIOR) {
			return charge >= 35;
		} else if (hero.heroClass == HeroClass.ROGUE) {
			return charge >= 20;
		} else if (hero.heroClass == HeroClass.MAGE) {
			return charge >= 25;
		} else if (hero.heroClass == HeroClass.HUNTRESS) {
			return charge >= 20;
		} else if (hero.heroClass == HeroClass.DUELIST) {
			return charge >= 20;
		} else if (hero.heroClass == HeroClass.CLERIC) {
			return charge >= 20;
		}  else {
			return charge >= 30;
		}
	}
}
