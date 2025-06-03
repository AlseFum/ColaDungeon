/*
 * Cola Dungeon
 * Copyright (C) 2022-2024 Cola Dungeon Team
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

package com.coladungeon.levels.rooms.special;

import com.coladungeon.levels.Level;
import com.coladungeon.levels.Terrain;
import com.coladungeon.levels.rooms.Room;
import com.coladungeon.levels.rooms.RoomWeaver;
import com.watabou.utils.Random;

public class RuinedAltarRoom extends Room {
    
    @Override
    public int minWidth() { return 7; }
    public int maxWidth() { return 9; }
    
    @Override
    public int minHeight() { return 7; }
    public int maxHeight() { return 9; }
    
    @Override
    public void paint(Level level) {
        RoomWeaver weaver = new RoomWeaver(this, level);
        
        // 添加自定义字符映射
        weaver.setCharMapping('A', Terrain.PEDESTAL)  // 祭坛
              .setCharMapping('r', Terrain.EMPTY_DECO)  // 碎石
              .setCharMapping('b', Terrain.BARRICADE)   // 障碍物
              .setCharMapping('e', Terrain.EMBERS);     // 灰烬
        
        // 基础布局
        String layout = """
            #######
            #.rrr.#
            #rr.rr#
            #.rAr.#
            #rr.rr#
            #.rrr.#
            #######
            """;
        
        // 应用布局
        weaver.weaveLayout(layout);
        
        // 随机添加一些装饰性地形
        for (int i = 1; i < width()-1; i++) {
            for (int j = 1; j < height()-1; j++) {
                if (weaver.get(i, j) == Terrain.EMPTY && Random.Int(10) == 0) {
                    weaver.set(i, j, Random.Int(2) == 0 ? Terrain.EMPTY_DECO : Terrain.EMBERS);
                }
            }
        }
        
        // 确保入口和出口区域是可通行的
        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
            if (door.x == left) {
                weaver.set(door.x-left+1, door.y-top, Terrain.EMPTY);
            } else if (door.x == right) {
                weaver.set(door.x-left-1, door.y-top, Terrain.EMPTY);
            } else if (door.y == top) {
                weaver.set(door.x-left, door.y-top+1, Terrain.EMPTY);
            } else if (door.y == bottom) {
                weaver.set(door.x-left, door.y-top-1, Terrain.EMPTY);
            }
        }
    }
} 