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

package com.coladungeon.items.spells;


import com.coladungeon.actors.buffs.MagicImmune;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Item;
import com.coladungeon.messages.Messages;
import com.coladungeon.utils.GLog;

import java.util.ArrayList;

public abstract class Spell extends Item {
	
	public static final String AC_CAST = "CAST";

	//affects how strongly on-scroll talents trigger from this scroll
	protected float talentFactor = 1;
	//the chance (0-1) of whether on-scroll talents trigger from this potion
	protected float talentChance = 1;
	
	{
		stackable = true;
		defaultAction = AC_CAST;
	}
	
	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_CAST );
		return actions;
	}
	
	@Override
	public void execute( final Hero hero, String action ) {
		
		super.execute( hero, action );
		
		if (action.equals( AC_CAST )) {
			
			if (curUser.buff(MagicImmune.class) != null){
				GLog.w( Messages.get(this, "no_magic") );
				return;
			}
			
			onCast( hero );
			
		}
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	protected abstract void onCast(Hero hero );
	
}
