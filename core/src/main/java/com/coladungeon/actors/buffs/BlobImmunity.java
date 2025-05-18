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

import com.coladungeon.actors.blobs.Blizzard;
import com.coladungeon.actors.blobs.ConfusionGas;
import com.coladungeon.actors.blobs.CorrosiveGas;
import com.coladungeon.actors.blobs.Electricity;
import com.coladungeon.actors.blobs.Fire;
import com.coladungeon.actors.blobs.Freezing;
import com.coladungeon.actors.blobs.Inferno;
import com.coladungeon.actors.blobs.ParalyticGas;
import com.coladungeon.actors.blobs.Regrowth;
import com.coladungeon.actors.blobs.SmokeScreen;
import com.coladungeon.actors.blobs.StenchGas;
import com.coladungeon.actors.blobs.StormCloud;
import com.coladungeon.actors.blobs.ToxicGas;
import com.coladungeon.actors.blobs.Web;
import com.coladungeon.actors.mobs.Tengu;
import com.coladungeon.levels.rooms.special.MagicalFireRoom;
import com.coladungeon.ui.BuffIndicator;

public class BlobImmunity extends FlavourBuff {
	
	{
		type = buffType.POSITIVE;
	}
	
	public static final float DURATION	= 20f;
	
	@Override
	public int icon() {
		return BuffIndicator.IMMUNITY;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	{
		//all harmful blobs
		immunities.add( Blizzard.class );
		immunities.add( ConfusionGas.class );
		immunities.add( CorrosiveGas.class );
		immunities.add( Electricity.class );
		immunities.add( Fire.class );
		immunities.add( MagicalFireRoom.EternalFire.class );
		immunities.add( Freezing.class );
		immunities.add( Inferno.class );
		immunities.add( ParalyticGas.class );
		immunities.add( Regrowth.class );
		immunities.add( SmokeScreen.class );
		immunities.add( StenchGas.class );
		immunities.add( StormCloud.class );
		immunities.add( ToxicGas.class );
		immunities.add( Web.class );

		immunities.add(Tengu.FireAbility.FireBlob.class);
	}

}
