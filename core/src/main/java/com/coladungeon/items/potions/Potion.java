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

package com.coladungeon.items.potions;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.Statistics;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.blobs.Fire;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Burning;
import com.coladungeon.actors.buffs.Ooze;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.hero.Talent;
import com.coladungeon.effects.Splash;
import com.coladungeon.items.Generator;
import com.coladungeon.items.Item;
import com.coladungeon.items.ItemStatusHandler;
import com.coladungeon.items.Recipe;
import com.coladungeon.items.potions.brews.AquaBrew;
import com.coladungeon.items.potions.elixirs.ElixirOfHoneyedHealing;
import com.coladungeon.items.potions.exotic.ExoticPotion;
import com.coladungeon.items.potions.exotic.PotionOfCleansing;
import com.coladungeon.items.potions.exotic.PotionOfCorrosiveGas;
import com.coladungeon.items.potions.exotic.PotionOfShroudingFog;
import com.coladungeon.items.potions.exotic.PotionOfSnapFreeze;
import com.coladungeon.items.potions.exotic.PotionOfStormClouds;
import com.coladungeon.journal.Catalog;
import com.coladungeon.levels.Terrain;
import com.coladungeon.messages.Messages;
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
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndBag;
import com.coladungeon.windows.WndOptions;
import com.coladungeon.windows.WndUseItem;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class Potion extends Item {

	public static final String AC_DRINK = "DRINK";
	
	//used internally for potions that can be drunk or thrown
	public static final String AC_CHOOSE = "CHOOSE";

	private static final float TIME_TO_DRINK = 1f;

	private static final LinkedHashMap<String, Integer> colors = new LinkedHashMap<String, Integer>() {
		{
			put("crimson",ItemSpriteSheet.POTION_CRIMSON);
			put("amber",ItemSpriteSheet.POTION_AMBER);
			put("golden",ItemSpriteSheet.POTION_GOLDEN);
			put("jade",ItemSpriteSheet.POTION_JADE);
			put("turquoise",ItemSpriteSheet.POTION_TURQUOISE);
			put("azure",ItemSpriteSheet.POTION_AZURE);
			put("indigo",ItemSpriteSheet.POTION_INDIGO);
			put("magenta",ItemSpriteSheet.POTION_MAGENTA);
			put("bistre",ItemSpriteSheet.POTION_BISTRE);
			put("charcoal",ItemSpriteSheet.POTION_CHARCOAL);
			put("silver",ItemSpriteSheet.POTION_SILVER);
			put("ivory",ItemSpriteSheet.POTION_IVORY);
		}
	};

	protected static final HashSet<Class<?extends Potion>> mustThrowPots = new HashSet<>();
	static{
		mustThrowPots.add(PotionOfToxicGas.class);
		mustThrowPots.add(PotionOfLiquidFlame.class);
		mustThrowPots.add(PotionOfParalyticGas.class);
		mustThrowPots.add(PotionOfFrost.class);
		
		//exotic
		mustThrowPots.add(PotionOfCorrosiveGas.class);
		mustThrowPots.add(PotionOfSnapFreeze.class);
		mustThrowPots.add(PotionOfShroudingFog.class);
		mustThrowPots.add(PotionOfStormClouds.class);
		
		//also all brews except unstable, hardcoded
	}
	
	protected static final HashSet<Class<?extends Potion>> canThrowPots = new HashSet<>();
	static{
		canThrowPots.add(PotionOfPurity.class);
		canThrowPots.add(PotionOfLevitation.class);
		
		//exotic
		canThrowPots.add(PotionOfCleansing.class);
		
		//elixirs
		canThrowPots.add(ElixirOfHoneyedHealing.class);
	}
	
	protected static ItemStatusHandler<Potion> handler;
	
	protected String color;

	//affects how strongly on-potion talents trigger from this potion
	protected float talentFactor = 1;
	//the chance (0-1) of whether on-potion talents trigger from this potion
	protected float talentChance = 1;
	
	{
		stackable = true;
		defaultAction = AC_DRINK;
	}
	
	@SuppressWarnings("unchecked")
	public static void initColors() {
		handler = new ItemStatusHandler<>( (Class<? extends Potion>[])Generator.Category.POTION.classes, colors );
	}

	public static void clearColors() {
		handler = null;
	}
	
	public static void save( Bundle bundle ) {
		handler.save( bundle );
	}

	public static void saveSelectively( Bundle bundle, ArrayList<Item> items ) {
		ArrayList<Class<?extends Item>> classes = new ArrayList<>();
		for (Item i : items){
			if (i instanceof ExoticPotion){
				if (!classes.contains(
					ExoticPotion.exoToReg.get(i.getClass()))){
					classes.add(ExoticPotion.exoToReg.get(i.getClass()));
				}
			} else if (i instanceof Potion){
				if (!classes.contains(i.getClass())){
					classes.add(i.getClass());
				}
			}
		}
		handler.saveClassesSelectively( bundle, classes );
	}
	
	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<>( (Class<? extends Potion>[])Generator.Category.POTION.classes, colors, bundle );
	}
	
	public Potion() {
		super();
		reset();
	}
	
	//anonymous potions are always IDed, do not affect ID status,
	//and their sprite is replaced by a placeholder if they are not known,
	//useful for items that appear in UIs, or which are only spawned for their effects
	protected boolean anonymous = false;
	public void anonymize(){
		if (!isKnown()) image = ItemSpriteSheet.POTION_HOLDER;
		anonymous = true;
	}

	@Override
	public void reset(){
		super.reset();
		if (handler != null && handler.contains(this)) {
			image = handler.image(this);
			color = handler.label(this);
		} else {
			//here, we need to init handler
			image = ItemSpriteSheet.POTION_CRIMSON;
			color = "crimson";
		}
	}

	@Override
	public String defaultAction() {
		if (isKnown() && mustThrowPots.contains(this.getClass())) {
			return AC_THROW;
		} else if (isKnown() &&canThrowPots.contains(this.getClass())){
			return AC_CHOOSE;
		} else {
			return AC_DRINK;
		}
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_DRINK );
		return actions;
	}
	
	@Override
	public void execute( final Hero hero, String action ) {

		super.execute( hero, action );
		
		if (action.equals( AC_CHOOSE )){
			
			GameScene.show(new WndUseItem(null, this) );
			
		} else if (action.equals( AC_DRINK )) {
			
			if (isKnown() && mustThrowPots.contains(getClass())) {
				
					GameScene.show(
						new WndOptions(new ItemSprite(this),
								Messages.get(Potion.class, "harmful"),
								Messages.get(Potion.class, "sure_drink"),
								Messages.get(Potion.class, "yes"), Messages.get(Potion.class, "no") ) {
							@Override
							protected void onSelect(int index) {
								if (index == 0) {
									drink( hero );
								}
							}
						}
					);
					
				} else {
					drink( hero );
				}
			
		}
	}
	
	@Override
	public void doThrow( final Hero hero ) {

		if (isKnown()
				&& !mustThrowPots.contains(this.getClass())
				&& !canThrowPots.contains(this.getClass())) {
		
			GameScene.show(
				new WndOptions(new ItemSprite(this),
						Messages.get(Potion.class, "beneficial"),
						Messages.get(Potion.class, "sure_throw"),
						Messages.get(Potion.class, "yes"), Messages.get(Potion.class, "no") ) {
					@Override
					protected void onSelect(int index) {
						if (index == 0) {
							Potion.super.doThrow( hero );
						}
					}
				}
			);
			
		} else {
			super.doThrow( hero );
		}
	}
	
	protected void drink( Hero hero ) {
		
		detach( hero.belongings.backpack );
		
		hero.spend( TIME_TO_DRINK );
		hero.busy();
		apply( hero );
		
		Sample.INSTANCE.play( Assets.Sounds.DRINK );
		
		hero.sprite.operate( hero.pos );

		if (!anonymous) {
			Catalog.countUse(getClass());
			if (Random.Float() < talentChance) {
				Talent.onPotionUsed(curUser, curUser.pos, talentFactor);
			}
		}
	}
	
	@Override
	protected void onThrow( int cell ) {
		if (Dungeon.level.map[cell] == Terrain.WELL || Dungeon.level.pit[cell]) {
			
			super.onThrow( cell );
			
		} else  {

			//aqua brew and storm clouds specifically don't press cells, so they can disarm traps
			if (!(this instanceof AquaBrew) && !(this instanceof PotionOfStormClouds)){
				Dungeon.level.pressCell( cell );
			}
			shatter( cell );

			if (!anonymous) {
				Catalog.countUse(getClass());
				if (Random.Float() < talentChance) {
					Talent.onPotionUsed(curUser, cell, talentFactor);
				}
			}
			
		}
	}
	
	public void apply( Hero hero ) {
		shatter( hero.pos );
	}
	
	public void shatter( int cell ) {
		splash( cell );
		if (Dungeon.level.heroFOV[cell]) {
			GLog.i( Messages.get(Potion.class, "shatter") );
			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
		}
	}

	@Override
	public void cast( final Hero user, int dst ) {
			super.cast(user, dst);
	}
	
	public boolean isKnown() {
		return anonymous || (handler != null && handler.isKnown( this ));
	}
	
	public void setKnown() {
		if (!anonymous) {
			if (!isKnown()) {
				handler.know(this);
				updateQuickslot();
			}
			
			if (Dungeon.hero.isAlive()) {
				Catalog.setSeen(getClass());
				Statistics.itemTypesDiscovered.add(getClass());
			}
		}
	}
	
	@Override
	public Item identify( boolean byHero ) {
		super.identify(byHero);

		if (!isKnown()) {
			setKnown();
		}
		return this;
	}
	
	@Override
	public String name() {
		return isKnown() ? super.name() : Messages.get(this, color);
	}

	@Override
	public String info() {
		//skip custom notes if anonymized and un-Ided
		return (anonymous && (handler == null || !handler.isKnown( this ))) ? desc() : super.info();
	}

	@Override
	public String desc() {
		return isKnown() ? super.desc() : Messages.get(this, "unknown_desc");
	}
	
	@Override
	public boolean isIdentified() {
		return isKnown();
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	public static HashSet<Class<? extends Potion>> getKnown() {
		return handler.known();
	}
	
	public static HashSet<Class<? extends Potion>> getUnknown() {
		return handler.unknown();
	}
	
	public static boolean allKnown() {
		return handler != null && handler.known().size() == Generator.Category.POTION.classes.length;
	}
	
	protected int splashColor(){
		return anonymous ? 0x00AAFF : ItemSprite.pick( image, 5, 9 );
	}
	
	protected void splash( int cell ) {
		Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );
		if (fire != null) {
			fire.clear(cell);
		}

		Char ch = Actor.findChar(cell);
		if (ch != null && ch.alignment == Char.Alignment.ALLY) {
			Buff.detach(ch, Burning.class);
			Buff.detach(ch, Ooze.class);
		}

		if (Dungeon.level.heroFOV[cell]) {
			if (ch != null) {
				Splash.at(ch.sprite.center(), splashColor(), 5);
			} else {
				Splash.at(cell, splashColor(), 5);
			}
		}
	}
	
	@Override
	public int value() {
		return 30 * quantity;
	}

	@Override
	public int energyVal() {
		return 6 * quantity;
	}

	public static class PlaceHolder extends Potion {
		
		{
			image = ItemSpriteSheet.POTION_HOLDER;
		}
		
		@Override
		public boolean isSimilar(Item item) {
			return ExoticPotion.regToExo.containsKey(item.getClass())
					|| ExoticPotion.regToExo.containsValue(item.getClass());
		}
		
		@Override
		public String info() {
			return "";
		}
	}
	
	public static class SeedToPotion extends Recipe {
		
		public static HashMap<Class<?extends Plant.Seed>, Class<?extends Potion>> types = new HashMap<>();
		static {
			types.put(Blindweed.Seed.class,     PotionOfInvisibility.class);
			types.put(Mageroyal.Seed.class,     PotionOfPurity.class);
			types.put(Earthroot.Seed.class,     PotionOfParalyticGas.class);
			types.put(Fadeleaf.Seed.class,      PotionOfMindVision.class);
			types.put(Firebloom.Seed.class,     PotionOfLiquidFlame.class);
			types.put(Icecap.Seed.class,        PotionOfFrost.class);
			types.put(Rotberry.Seed.class,      PotionOfStrength.class);
			types.put(Sorrowmoss.Seed.class,    PotionOfToxicGas.class);
			types.put(Starflower.Seed.class,    PotionOfExperience.class);
			types.put(Stormvine.Seed.class,     PotionOfLevitation.class);
			types.put(Sungrass.Seed.class,      PotionOfHealing.class);
			types.put(Swiftthistle.Seed.class,  PotionOfHaste.class);
		}
		
		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			if (ingredients.size() != 3) {
				return false;
			}
			
			for (Item ingredient : ingredients){
				if (!(ingredient instanceof Plant.Seed
						&& ingredient.quantity() >= 1
						&& types.containsKey(ingredient.getClass()))){
					return false;
				}
			}
			return true;
		}
		
		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 0;
		}
		
		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			
			for (Item ingredient : ingredients){
				ingredient.quantity(ingredient.quantity() - 1);
			}
			
			ArrayList<Class<?extends Plant.Seed>> seeds = new ArrayList<>();
			for (Item i : ingredients) {
				if (!seeds.contains(i.getClass())) {
					seeds.add((Class<? extends Plant.Seed>) i.getClass());
				}
			}
			
			Potion result;
			
			if ( (seeds.size() == 2 && Random.Int(4) == 0)
					|| (seeds.size() == 3 && Random.Int(2) == 0)) {
				
				result = (Potion) Generator.randomUsingDefaults( Generator.Category.POTION );
				
			} else {
				result = Reflection.newInstance(types.get(Random.element(ingredients).getClass()));
				
			}
			
			if (seeds.size() == 1){
				result.identify();
			}

			while (result instanceof PotionOfHealing
					&& Random.Int(10) < Dungeon.LimitedDrops.COOKING_HP.count) {

				result = (Potion) Generator.randomUsingDefaults(Generator.Category.POTION);
			}
			
			if (result instanceof PotionOfHealing) {
				Dungeon.LimitedDrops.COOKING_HP.count++;
			}
			
			return result;
		}
		
		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			return new WndBag.Placeholder(ItemSpriteSheet.POTION_HOLDER){

				@Override
				public String name() {
					return Messages.get(Potion.SeedToPotion.class, "name");
				}
				
				@Override
				public String info() {
					return "";
				}
			};
		}
	}
}
