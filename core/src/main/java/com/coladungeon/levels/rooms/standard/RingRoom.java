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

package com.coladungeon.levels.rooms.standard;

import com.coladungeon.levels.Level;
import com.coladungeon.levels.Terrain;
import com.coladungeon.levels.painters.Painter;

public class RingRoom extends StandardRoom {
	
	@Override
	public int minWidth() {
		return Math.max(super.minWidth(), 7);
	}
	
	@Override
	public int minHeight() {
		return Math.max(super.minHeight(), 7);
	}
	
	@Override
	public float[] sizeCatProbs() {
		return new float[]{9, 3, 1};
	}
	
	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1 , Terrain.EMPTY );
		
		int minDim = Math.min(width(), height());
		int passageWidth = (int)Math.floor(0.25f*(minDim+1));
		Painter.fill(level, this, passageWidth+1, Terrain.WALL);
		
		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}
	}
}
