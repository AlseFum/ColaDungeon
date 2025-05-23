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

import com.coladungeon.actors.Actor;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.hero.HeroClass;
import com.coladungeon.items.Item;
import com.coladungeon.journal.Catalog;

import java.util.ArrayList;

public abstract class RemainsItem extends Item {

	{
		bones = false;

		defaultAction = AC_USE;
	}

	public static final String AC_USE =  "USE";

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (action.equals(AC_USE)){
			hero.sprite.operate(hero.pos);

			Catalog.countUse(getClass());
			doEffect(hero);

			hero.spendAndNext(Actor.TICK);
			detach(hero.belongings.backpack);
		}
	}

	protected abstract void doEffect(Hero hero);

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public int value() {
		return 50;
	}

	public static RemainsItem get(HeroClass cls){
		if (cls.equals(HeroClass.WARRIOR)) {
			return new SealShard();
		} else if (cls.equals(HeroClass.MAGE)) {
			return new BrokenStaff();
		} else if (cls.equals(HeroClass.ROGUE)) {
			return new CloakScrap();
		} else if (cls.equals(HeroClass.HUNTRESS)) {
			return new BowFragment();
		} else if (cls.equals(HeroClass.DUELIST)) {
			return new BrokenHilt();
		} else if (cls.equals(HeroClass.CLERIC)) {
			return new TornPage();
		} else {
			return new SealShard();
		}
	}

}
