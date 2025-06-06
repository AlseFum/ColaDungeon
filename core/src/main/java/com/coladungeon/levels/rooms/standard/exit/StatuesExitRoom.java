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

package com.coladungeon.levels.rooms.standard.exit;

import com.coladungeon.levels.Level;
import com.coladungeon.levels.Terrain;
import com.coladungeon.levels.features.LevelTransition;
import com.coladungeon.levels.painters.Painter;
import com.coladungeon.levels.rooms.standard.StatuesRoom;
import com.watabou.utils.PathFinder;

public class StatuesExitRoom extends StatuesRoom {

	@Override
	public float[] sizeCatProbs() {
		return new float[]{3, 1, 0};
	}

	@Override
	public boolean isExit() {
		return true;
	}

	@Override
	public void paint(Level level) {
		super.paint(level);

		int exit = level.pointToCell(center());

		if (width() <= 10 && height()<= 10){
			Painter.fill(level, this, 3, Terrain.EMPTY_SP);
		}

		for (int i : PathFinder.NEIGHBOURS8){
			if (level.map[exit + i] != Terrain.STATUE_SP) {
				Painter.set(level, exit + i, Terrain.EMPTY_SP);
			}
		}

		Painter.set( level, exit, Terrain.EXIT );
		level.transitions.add(new LevelTransition(level, exit, LevelTransition.Type.REGULAR_EXIT));

	}

}
