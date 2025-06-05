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
import com.coladungeon.actors.buffs.FlavourBuff;
import com.coladungeon.actors.buffs.Invisibility;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.messages.Messages;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.ui.BuffIndicator;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class AssassinsBlade extends Assassinator {

	{
		image = ItemSpriteSheet.ASSASSINS_BLADE;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 0.9f;
		tier = 4;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	public boolean useTargeting(){
		return false;
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		Dagger.sneakAbility(hero, target, 3, 2+buffedLvl(), this);
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
		return "Assassin's Blade";
	}

	@Override
	public String desc() {
		return "A masterfully crafted blade designed specifically for assassins. Its deadly precision and balanced weight make it perfect for swift, lethal strikes.";
	}

	@Override
	public void special_effect(Char attacker, Char defender, int damage) {
		super.special_effect(attacker, defender, damage);
		if (attacker instanceof Hero) {
			Buff.affect(attacker, AssassinCloak.class, 0f);
			GLog.i("The Assassin's Blade shrouds you in shadows!");
		}
	}

	public static class AssassinCloak extends FlavourBuff {
		{
			type = Buff.buffType.POSITIVE;
		}
			public float last=10f;	
		@Override
		public boolean act() {
			if (target.isAlive() && last>0f) {
				Buff.prolong(target, Invisibility.class, 2f);
				last-=TICK;
				spend(TICK);
			} else {
				detach();
			}
			return true;
		}

		@Override
		public int icon() {
			return BuffIndicator.INVISIBLE;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.5f, 0.5f, 0.5f);
		}

		@Override
		public String toString() {
			return "Assassin's Cloak";
		}
		@Override
		public String name(){
			return "Assassin's Cloak";
		}
		@Override
		public String desc() {
			return "The shadows of the assassin's blade continuously shroud you in invisibility.Last for "+last+" rounds.";
		}
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put("last", last);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			last = bundle.getFloat("last");
		}
		
	}
}