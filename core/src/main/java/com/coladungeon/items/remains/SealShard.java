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

package com.coladungeon.items.remains;

import com.coladungeon.Assets;
import com.coladungeon.actors.buffs.Barrier;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.FloatingText;
import com.coladungeon.sprites.CharSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class SealShard extends RemainsItem {

	{
		image = ItemSpriteSheet.SEAL_SHARD;
	}

	@Override
	protected void doEffect(Hero hero) {
		Buff.affect(hero, Barrier.class).incShield(Math.round(hero.HT/5f));
		hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(Math.round(hero.HT/5f)), FloatingText.SHIELDING );
		Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
	}

}
