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

package com.coladungeon.levels;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.Bones;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.items.Heap;
import com.coladungeon.items.Item;
import com.coladungeon.levels.features.LevelTransition;
import com.coladungeon.levels.rooms.Room;
import com.coladungeon.levels.rooms.special.RuinedAltarRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class DeadEndLevel extends Level {

	private static final int SIZE = 9;
	private RuinedAltarRoom altarRoom;
	
	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}
	
	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_CAVES;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.WATER_HALLS;
	}
	
	@Override
	protected boolean build() {
		setSize(SIZE + 6, SIZE + 6);
		
		// 清空地图
		for (int i = 0; i < length(); i++) {
			map[i] = Terrain.WALL;
		}
		
		// 创建中心区域
		altarRoom = new RuinedAltarRoom();
		altarRoom.setSize();
		altarRoom.set(3, 3, 3 + SIZE - 1, 3 + SIZE - 1);
		altarRoom.connected.put(null, new Room.Door(altarRoom.left + SIZE/2, altarRoom.top)); // 上门
		altarRoom.connected.put(null, new Room.Door(altarRoom.left + SIZE/2, altarRoom.bottom)); // 下门
		altarRoom.paint(this);
		
		// 入口（向上）
		int entrance = (SIZE + 6) * (SIZE + 3) + (SIZE + 6) / 2;
		
		transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
		
		// 出口（向下）
		int exit = (SIZE + 6) * 3 + (SIZE + 6) / 2;
		
		transitions.add(new LevelTransition(this, exit, LevelTransition.Type.REGULAR_EXIT));
		
		// 创建通道
		Point entrancePoint = cellToPoint(entrance);
		Point exitPoint = cellToPoint(exit);
		
		// 连接入口到祭坛房间
		for (int y = altarRoom.top; y < entrancePoint.y; y++) {
			map[y * width() + entrancePoint.x] = Terrain.EMPTY;
		}
		
		// 连接祭坛房间到出口
		for (int y = exitPoint.y + 1; y <= altarRoom.bottom; y++) {
			map[y * width() + exitPoint.x] = Terrain.EMPTY;
		}
		
		// 在通道两侧添加水
		for (int y = altarRoom.top; y < entrancePoint.y; y++) {
			if (map[y * width() + entrancePoint.x] == Terrain.EMPTY) {
				map[y * width() + entrancePoint.x - 1] = Terrain.WATER;
				map[y * width() + entrancePoint.x + 1] = Terrain.WATER;
			}
		}
		for (int y = exitPoint.y + 1; y <= altarRoom.bottom; y++) {
			if (map[y * width() + exitPoint.x] == Terrain.EMPTY) {
				map[y * width() + exitPoint.x - 1] = Terrain.WATER;
				map[y * width() + exitPoint.x + 1] = Terrain.WATER;
			}
		}
		
		// 确保梯子位置正确
		map[entrance] = Terrain.ENTRANCE;
		map[exit] = Terrain.EXIT;
		
		return true;
	}
	
	@Override
	public Mob createMob() {
		return null;
	}
	
	@Override
	protected void createMobs() {
	}

	public Actor addRespawner() {
		return null;
	}

	@Override
	protected void createItems() {
		Random.pushGenerator(Random.Long());
		ArrayList<Item> bonesItems = Bones.get();
		if (bonesItems != null) {
			for (Item i : bonesItems) {
				drop(i, entrance()-width()).setHauntedIfCursed().type = Heap.Type.REMAINS;
			}
		}
		Random.popGenerator();
	}
	
	@Override
	public int randomRespawnCell( Char ch ) {
		return entrance()-width();
	}

}
