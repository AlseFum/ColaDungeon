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

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Bless;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.effects.particles.ShadowParticle;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class HolyDart extends TippedDart {

	{
		image = ItemSpriteSheet.HOLY_DART;
	}
	
	@Override
	public int proc(Char attacker, Char defender, int damage) {

		//do nothing to the hero when processing charged shot
		if (processingChargedShot && defender == attacker){
			return super.proc(attacker, defender, damage);
		}

		if (attacker.alignment == defender.alignment){
			Buff.affect(defender, Bless.class, Math.round(Bless.DURATION));
			return 0;
		}

		if (Char.hasProp(defender, Char.Property.UNDEAD) || Char.hasProp(defender, Char.Property.DEMONIC)){
			defender.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10+buffedLvl() );
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
			defender.damage(Random.NormalIntRange(10 + Dungeon.scalingDepth()/3, 20 + Dungeon.scalingDepth()/3), this);
		//also do not bless enemies if processing charged shot
		} else if (!processingChargedShot){
			Buff.affect(defender, Bless.class, Math.round(Bless.DURATION));
		}
		
		return super.proc(attacker, defender, damage);
	}
}
