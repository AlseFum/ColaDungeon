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

package com.coladungeon.levels.traps;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.actors.mobs.Statue;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.Speck;
import com.coladungeon.items.Generator;
import com.coladungeon.items.weapon.melee.MeleeWeapon;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.StatueSprite;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class GuardianTrap extends Trap {

	{
		color = RED;
		shape = STARS;
	}

	@Override
	public void activate() {

		for (Mob mob : Dungeon.level.mobs) {
			mob.beckon( pos );
		}

		if (Dungeon.level.heroFOV[pos]) {
			GLog.w( Messages.get(this, "alarm") );
			CellEmitter.center(pos).start( Speck.factory(Speck.SCREAM), 0.3f, 3 );
		}

		Sample.INSTANCE.play( Assets.Sounds.ALERT );

		for (int i = 0; i < (scalingDepth() - 5)/5; i++){
			Guardian guardian = new Guardian();
			guardian.createWeapon(false);
			guardian.state = guardian.WANDERING;
			guardian.pos = Dungeon.level.randomRespawnCell( guardian );
			if (guardian.pos != -1) {
				GameScene.add(guardian);
				guardian.beckon(Dungeon.hero.pos);
			}
		}

	}

	public static class Guardian extends Statue {

		{
			spriteClass = GuardianSprite.class;

			EXP = 0;
			state = WANDERING;

			levelGenStatue = false;
		}

		@Override
		public void createWeapon( boolean useDecks ) {
			weapon = (MeleeWeapon) Generator.randomUsingDefaults(Generator.Category.WEAPON);
			weapon.cursed = false;
			weapon.enchant(null);
			weapon.level(0);
		}

		@Override
		public void beckon(int cell) {
			//Beckon works on these ones, unlike their superclass.
			notice();

			if (state != HUNTING) {
				state = WANDERING;
			}
			target = cell;
		}

	}

	public static class GuardianSprite extends StatueSprite {

		public GuardianSprite(){
			super();
			tint(0, 0, 1, 0.2f);
		}

		@Override
		public void resetColor() {
			super.resetColor();
			tint(0, 0, 1, 0.2f);
		}
	}
}
