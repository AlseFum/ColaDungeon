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

package com.coladungeon.levels.rooms.standard.entrance;

import com.coladungeon.levels.Level;
import com.coladungeon.levels.Terrain;
import com.coladungeon.levels.features.LevelTransition;
import com.coladungeon.levels.painters.Painter;
import com.coladungeon.levels.rooms.standard.RitualRoom;
import com.watabou.utils.Point;

public class RitualEntranceRoom extends RitualRoom {

	@Override
	public boolean isEntrance() {
		return true;
	}

	@Override
	protected void placeloot(Level level, Point p) {
		Painter.set(level, p, Terrain.ENTRANCE);
		level.transitions.add(new LevelTransition(level, level.pointToCell(p), LevelTransition.Type.REGULAR_ENTRANCE));
	}
}
