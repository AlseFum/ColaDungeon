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

package com.coladungeon.actors.buffs;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.hero.Talent;
import com.coladungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class EnhancedRings extends FlavourBuff {

	{
		type = Buff.buffType.POSITIVE;
	}

	@Override
	public boolean attachTo(Char target) {
		if (super.attachTo(target)){
			if (target instanceof Hero) ((Hero) target).updateHT(false);
			return true;
		}
		return false;
	}

	@Override
	public void detach() {
		super.detach();
		if (target instanceof Hero) ((Hero) target).updateHT(false);
	}

	@Override
	public int icon() {
		return BuffIndicator.UPGRADE;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0, 1, 0);
	}

	@Override
	public float iconFadePercent() {
		float max = 3*Dungeon.hero.pointsInTalent(Talent.ENHANCED_RINGS);
		return Math.max(0, (max-visualcooldown()) / max);
	}

}
