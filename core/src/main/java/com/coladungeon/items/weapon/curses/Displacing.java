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

package com.coladungeon.items.weapon.curses;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.Speck;
import com.coladungeon.items.scrolls.ScrollOfTeleportation;
import com.coladungeon.items.weapon.Weapon;
import com.coladungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Displacing extends Weapon.Enchantment {

	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage ) {

		float procChance = 1/12f * procChanceMultiplier(attacker);
		if (Random.Float() < procChance && !defender.properties().contains(Char.Property.IMMOVABLE)){

			int oldpos = defender.pos;
			if (ScrollOfTeleportation.teleportChar(defender)){
				if (Dungeon.level.heroFOV[oldpos]) {
					CellEmitter.get( oldpos ).start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );
				}

				if (defender instanceof Mob && ((Mob) defender).state == ((Mob) defender).HUNTING){
					((Mob) defender).state = ((Mob) defender).WANDERING;
				}
			}
		}

		return damage;
	}

	@Override
	public boolean curse() {
		return true;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

}
