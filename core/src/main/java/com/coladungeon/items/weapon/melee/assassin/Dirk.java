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

package com.coladungeon.items.weapon.melee.assassin;

import com.coladungeon.Assets;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Speed;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.messages.Messages;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;

public class Dirk extends Assassinator {

	{
		image = ItemSpriteSheet.DIRK;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1f;

		tier = 2;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}
	@Override
	public boolean useTargeting(){
		return false;
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		Dagger.sneakAbility(hero, target, 4, 2+buffedLvl(), this);
	}

	@Override
	public String abilityInfo() {
		if (levelKnown){
			return Messages.get(this, "ability_desc", 2+buffedLvl());
		} else {
			return Messages.get(this, "typical_ability_desc", 2);
		}
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return Integer.toString(2+level);
	}

	@Override
	public String name() {
		return "Dirk";
	}

	@Override
	public String desc() {
		return "A slender, double-edged dagger that's longer than a typical dagger. Its extended reach and sharp edges make it particularly effective for surprise attacks.";
	}

	// @Override
	// public void special_effect(Char attacker, Char defender, int damage) {
	// 	super.special_effect(attacker, defender, damage);
	// 	if (attacker instanceof Hero) {
	// 		Buff.affect(attacker, Speed.class, 5f); // Apply speed boost for 5 seconds
	// 		GLog.i("The Dirk grants you a burst of speed!");
	// 	}
	// }

}
