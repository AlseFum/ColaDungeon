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

package com.coladungeon.items.food;

import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Hunger;
import com.coladungeon.actors.buffs.WellFed;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Item;
import com.coladungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class MeatPie extends Food {
	
	{
		image = ItemSpriteSheet.MEAT_PIE;
		energy = Hunger.STARVING*2f;
	}
	
	@Override
	protected void satisfy(Hero hero) {
		super.satisfy( hero );
		Buff.affect(hero, WellFed.class).reset();
	}
	
	@Override
	public int value() {
		return 40 * quantity;
	}
	
	public static class Recipe extends com.coladungeon.items.Recipe {
		
		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			boolean pasty = false;
			boolean ration = false;
			boolean meat = false;
			
			for (Item ingredient : ingredients){
				if (ingredient.quantity() > 0) {
					if (ingredient instanceof Pasty || ingredient instanceof PhantomMeat) {
						pasty = true;
					} else if (ingredient.getClass() == Food.class) {
						ration = true;
					} else if (ingredient instanceof MysteryMeat
							|| ingredient instanceof StewedMeat
							|| ingredient instanceof ChargrilledMeat
							|| ingredient instanceof FrozenCarpaccio) {
						meat = true;
					}
				}
			}
			
			return pasty && ration && meat;
		}
		
		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 6;
		}
		
		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			
			for (Item ingredient : ingredients){
				ingredient.quantity(ingredient.quantity() - 1);
			}
			
			return sampleOutput(null);
		}
		
		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			return new MeatPie();
		}
	}
}
