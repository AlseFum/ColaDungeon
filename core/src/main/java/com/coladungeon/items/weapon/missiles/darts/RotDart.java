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
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Corrosion;
import com.coladungeon.sprites.ItemSpriteSheet;


public class RotDart extends TippedDart {
	
	{
		image = ItemSpriteSheet.ROT_DART;
	}
	
	@Override
	public int proc(Char attacker, Char defender, int damage) {

		//when processing charged shot, only corrode enemies
		if (processingChargedShot && attacker.alignment == defender.alignment) {
			//do nothing
		} else if (defender.properties().contains(Char.Property.BOSS)
				|| defender.properties().contains(Char.Property.MINIBOSS)){
			Buff.affect(defender, Corrosion.class).set(5f, Dungeon.scalingDepth()/3);
		} else {
			Buff.affect(defender, Corrosion.class).set(10f, Dungeon.scalingDepth());
		}
		
		return super.proc(attacker, defender, damage);
	}
	
	@Override
	public float durabilityPerUse() {
		return MAX_DURABILITY/5f; //always 5 uses
	}
}
