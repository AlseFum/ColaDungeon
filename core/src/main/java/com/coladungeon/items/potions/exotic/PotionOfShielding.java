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

package com.coladungeon.items.potions.exotic;

import com.coladungeon.Challenges;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.buffs.Barrier;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.FloatingText;
import com.coladungeon.items.potions.PotionOfHealing;
import com.coladungeon.sprites.CharSprite;
import com.coladungeon.sprites.ItemSpriteSheet;

public class PotionOfShielding extends ExoticPotion {
	
	{
		icon = ItemSpriteSheet.Icons.POTION_SHIELDING;
	}
	
	@Override
	public void apply(Hero hero) {
		identify();

		if (Dungeon.isChallenged(Challenges.NO_HEALING)){
			PotionOfHealing.pharmacophobiaProc(hero);
		} else {
			//~75% of a potion of healing
			Buff.affect(hero, Barrier.class).setShield((int) (0.6f * hero.HT + 10));
			hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString((int) (0.6f * hero.HT + 10)), FloatingText.SHIELDING );
		}
	}
}
