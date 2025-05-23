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

package com.coladungeon.items.weapon.missiles.darts;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.PinCushion;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.hero.Talent;
import com.coladungeon.items.Generator;
import com.coladungeon.items.Item;
import com.coladungeon.items.wands.WandOfRegrowth;
import com.coladungeon.items.weapon.melee.Crossbow;
import com.coladungeon.journal.Catalog;
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
import com.coladungeon.windows.WndOptions;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class TippedDart extends Dart {
	
	{
		tier = 2;

		baseUses = 1f;
	}
	
	private static final String AC_CLEAN = "CLEAN";
	
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions( hero );
		actions.remove( AC_TIP );
		actions.add( AC_CLEAN );
		return actions;
	}
	
	@Override
	public void execute(final Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals( AC_CLEAN )){

			String[] options;
			if (quantity() > 1){
				options = new String[]{
					Messages.get(this, "clean_all"),
					Messages.get(this, "clean_one"),
					Messages.get(this, "cancel")
				};
			} else {
				options = new String[]{
					Messages.get(this, "clean_one"),
					Messages.get(this, "cancel")
				};
			}
			
			GameScene.show(new WndOptions(new ItemSprite(this),
					Messages.titleCase(name()),
					Messages.get(this, "clean_desc"),
					options){
				@Override
				protected void onSelect(int index) {
					if (index == 0){
						detachAll(hero.belongings.backpack);
						new Dart().quantity(quantity).collect();
						
						hero.spend( 1f );
						hero.busy();
						hero.sprite.operate(hero.pos);
					} else if (index == 1 && quantity() > 1){
						detach(hero.belongings.backpack);
						if (!new Dart().collect()) Dungeon.level.drop(new Dart(), hero.pos).sprite.drop();

						//reset durability if there are darts left in the stack
						durability = MAX_DURABILITY;
						
						hero.spend( 1f );
						hero.busy();
						hero.sprite.operate(hero.pos);
					}
				}
			});
			
		}
	}
	
	//exact same damage as regular darts, despite being higher tier.

	@Override
	protected void rangedHit(Char enemy, int cell) {
		targetPos = cell;
		super.rangedHit( enemy, cell);
		
		//need to spawn a dart
		if (durability <= 0 && !spawnedForEffect){
			//attempt to stick the dart to the enemy, just drop it if we can't.
			Dart d = new Dart();
			Catalog.countUse(getClass());
			if (sticky && enemy != null && enemy.isAlive() && enemy.alignment != Char.Alignment.ALLY){
				PinCushion p = Buff.affect(enemy, PinCushion.class);
				if (p.target == enemy){
					p.stick(d);
					return;
				}
			}
			Dungeon.level.drop( d, enemy.pos ).sprite.drop();
		}
	}

	//the number of regular darts lost due to merge being called
	public static int lostDarts = 0;

	@Override
	public Item merge(Item other) {
		int total = quantity() + other.quantity();
		super.merge(other);
		int extra = total - quantity();

		//need to spawn waste tipped darts as regular darts
		if (extra > 0){
			lostDarts += extra;
		}
		return this;
	}

	private static int targetPos = -1;

	@Override
	public float durabilityPerUse() {
		float use = super.durabilityPerUse(false);

		if (Dungeon.hero != null) {
			use /= (1 + Dungeon.hero.pointsInTalent(Talent.DURABLE_TIPS));

			//checks both destination and source position
			float lotusPreserve = 0f;
			if (targetPos != -1) {
				for (Char ch : Actor.chars()) {
					if (ch instanceof WandOfRegrowth.Lotus) {
						WandOfRegrowth.Lotus l = (WandOfRegrowth.Lotus) ch;
						if (l.inRange(targetPos)) {
							lotusPreserve = Math.max(lotusPreserve, l.seedPreservation());
						}
					}
				}
				targetPos = -1;
			}
			int p = curUser == null ? Dungeon.hero.pos : curUser.pos;
			for (Char ch : Actor.chars()) {
				if (ch instanceof WandOfRegrowth.Lotus) {
					WandOfRegrowth.Lotus l = (WandOfRegrowth.Lotus) ch;
					if (l.inRange(p)) {
						lotusPreserve = Math.max(lotusPreserve, l.seedPreservation());
					}
				}
			}
			use *= (1f - lotusPreserve);
		}

		float usages = Math.round(MAX_DURABILITY/use);

		//grants 3+lvl extra uses with charged shot
		if (bow != null && Dungeon.hero != null && Dungeon.hero.buff(Crossbow.ChargedShot.class) != null){
			usages += 3 + bow.buffedLvl();
		}

		//at 100 uses, items just last forever.
		if (usages >= 100f) return 0;

		//add a tiny amount to account for rounding error for calculations like 1/3
		return (MAX_DURABILITY/usages) + 0.001f;
	}
	
	@Override
	public int value() {
		//value of regular dart plus half of the seed
		return 8 * quantity;
	}
	
	public static final LinkedHashMap<Class<?extends Plant.Seed>, Class<?extends TippedDart>> types = new LinkedHashMap<>();
	static {
		types.put(Rotberry.Seed.class,      RotDart.class);
		types.put(Sungrass.Seed.class,      HealingDart.class);
		types.put(Fadeleaf.Seed.class,      DisplacingDart.class);
		types.put(Icecap.Seed.class,        ChillingDart.class);
		types.put(Firebloom.Seed.class,     IncendiaryDart.class);
		types.put(Sorrowmoss.Seed.class,    PoisonDart.class);
		types.put(Swiftthistle.Seed.class,  AdrenalineDart.class);
		types.put(Blindweed.Seed.class,     BlindingDart.class);
		types.put(Stormvine.Seed.class,     ShockingDart.class);
		types.put(Earthroot.Seed.class,     ParalyticDart.class);
		types.put(Mageroyal.Seed.class,     CleansingDart.class);
		types.put(Starflower.Seed.class,    HolyDart.class);
	}
	
	public static TippedDart getTipped( Plant.Seed s, int quantity ){
		return (TippedDart) Reflection.newInstance(types.get(s.getClass())).quantity(quantity);
	}
	
	public static TippedDart randomTipped( int quantity ){
		Plant.Seed s;
		do{
			s = (Plant.Seed) Generator.randomUsingDefaults(Generator.Category.SEED);
		} while (!types.containsKey(s.getClass()));
		
		return getTipped(s, quantity );
		
	}
	
}
