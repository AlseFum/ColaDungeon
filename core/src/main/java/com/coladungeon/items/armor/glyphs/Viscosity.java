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

package com.coladungeon.items.armor.glyphs;

import com.coladungeon.Badges;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.hero.Talent;
import com.coladungeon.items.armor.Armor;
import com.coladungeon.items.armor.Armor.Glyph;
import com.coladungeon.messages.Messages;
import com.coladungeon.sprites.CharSprite;
import com.coladungeon.sprites.ItemSprite;
import com.coladungeon.sprites.ItemSprite.Glowing;
import com.coladungeon.ui.BuffIndicator;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Viscosity extends Glyph {
	
	private static ItemSprite.Glowing PURPLE = new ItemSprite.Glowing( 0x8844CC );
	
	@Override
	public int proc( Armor armor, Char attacker, Char defender, int damage ) {

		//we use a tracker so that this glyph can apply after armor
		Buff.affect(defender, ViscosityTracker.class).level = armor.buffedLvl();

		return damage;
		
	}

	@Override
	public Glowing glowing() {
		return PURPLE;
	}

	public static class ViscosityTracker extends Buff {

		{
			actPriority = Actor.VFX_PRIO;
		}

		private int level = 0;

		public int deferDamage(int dmg){
			//account for icon stomach (just skip the glyph)
			if (target.buff(Talent.WarriorFoodImmunity.class) != null){
				return dmg;
			}

			int level = Math.max( 0, this.level );

			float percent = (level+1)/(float)(level+6);
			percent *= genericProcChanceMultiplier(target);

			int amount;
			if (percent > 1f){
				dmg = Math.round(dmg / percent);
				amount = dmg;
			} else {
				amount = (int)Math.ceil(dmg * percent);
			}

			if (amount > 0){
				DeferedDamage deferred = Buff.affect( target, DeferedDamage.class );
				deferred.extend( amount );

				target.sprite.showStatus( CharSprite.WARNING, Messages.get(Viscosity.class, "deferred", amount) );
			}

			return dmg - amount;
		}

		@Override
		public boolean act() {
			detach();
			return true;
		}
	};
	
	public static class DeferedDamage extends Buff {
		
		{
			type = buffType.NEGATIVE;
		}
		
		protected int damage = 0;
		
		private static final String DAMAGE	= "damage";
		
		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( DAMAGE, damage );
			
		}
		
		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			damage = bundle.getInt( DAMAGE );
		}
		
		public void extend( float damage ) {
			if (this.damage == 0){
				//wait 1 turn before damaging if this is freshly applied
				postpone(TICK);
			}
			this.damage += damage;
		}
		
		@Override
		public int icon() {
			return BuffIndicator.DEFERRED;
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString(damage);
		}
		
		@Override
		public boolean act() {
			if (target.isAlive()) {

				int damageThisTick = Math.max(1, (int)(damage*0.1f));
				target.damage( damageThisTick, this );
				if (target == Dungeon.hero && !target.isAlive()) {

					Badges.validateDeathFromFriendlyMagic();

					Dungeon.fail( this );
					GLog.n( Messages.get(this, "ondeath") );
				}
				spend( TICK );

				damage -= damageThisTick;
				if (damage <= 0) {
					detach();
				}
				
			} else {
				
				detach();
				
			}
			
			return true;
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", damage);
		}
	}
}
