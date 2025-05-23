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

package com.coladungeon.levels.themes;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.items.Generator;
import com.coladungeon.items.potions.PotionOfMindVision;
import com.coladungeon.levels.Level;
import com.coladungeon.levels.RegularLevel;
import com.coladungeon.levels.Terrain;
import com.coladungeon.levels.features.LevelTransition;
import com.coladungeon.levels.painters.HallsPainter;
import com.coladungeon.levels.painters.Painter;
import com.coladungeon.levels.rooms.Room;
import com.coladungeon.levels.rooms.standard.StandardRoom;
import com.coladungeon.levels.traps.GrimTrap;
import com.coladungeon.levels.traps.ToxicTrap;
import com.coladungeon.messages.Messages;
import com.coladungeon.sprites.ItemSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

/**
 * 暗影森林主题包 - 一个黑暗神秘的森林迷宫
 */
public class ShadowForestThemePack {
    
    public static String name() {
        return "Shadow Forest";
    }
    
    public static String desc() {
        return "A dark and mysterious forest where shadows hide ancient secrets.";
    }
    
    public static ItemSprite icon() {
        return new ItemSprite(ItemSpriteSheet.EXOTIC_ISAZ, null);
    }
    
    /**
     * 暗影森林楼层
     */
    public static class ShadowForestLevel extends RegularLevel {
        
        @Override
        public String tilesTex() {
            return Assets.Environment.TILES_HALLS;
        }
        
        @Override
        public String waterTex() {
            return Assets.Environment.WATER_HALLS;
        }
        
        @Override
        protected int standardRooms(boolean forceMax) {
            return 4; // 减少房间数量，从8减少到4
        }
        
        @Override
        protected int specialRooms(boolean forceMax) {
            return 0; // 减少特殊房间，从1减少到0
        }
        
        @Override
        protected ArrayList<Room> initRooms() {
            ArrayList<Room> initRooms = new ArrayList<>();
            
            // 创建暗影核心房间，同时包含入口和出口
            ShadowHeartRoom heartRoom = new ShadowHeartRoom();
            initRooms.add(heartRoom);
            
            // 设置为入口和出口房间
            roomEntrance = heartRoom;
            roomExit = heartRoom;
            
            // 添加其他标准房间
            int standards = standardRooms(feeling == Feeling.LARGE);
            for (int i = 0; i < standards; i++) {
                StandardRoom s;
                do {
                    s = StandardRoom.createRoom();
                } while (!s.setSizeCat(standards - i));
                i += s.sizeFactor() - 1;
                initRooms.add(s);
            }
            
            return initRooms;
        }
        
        @Override
        protected Painter painter() {
            return new HallsPainter()
                    .setWater(feeling == Feeling.WATER ? 0.65f : 0.10f, 4)
                    .setGrass(feeling == Feeling.GRASS ? 0.80f : 0.45f, 3)
                    .setTraps(nTraps(), trapClasses(), trapChances());
        }
        
        @Override
        protected Class<?>[] trapClasses() {
            return new Class<?>[]{GrimTrap.class, ToxicTrap.class};
        }
        
        @Override
        protected float[] trapChances() {
            return new float[]{2, 3};
        }
        
        @Override
        public String tileName(int tile) {
            switch (tile) {
                case Terrain.WATER:
                    return "Shadow Pool";
                case Terrain.HIGH_GRASS:
                    return "Dark Vines";
                default:
                    return super.tileName(tile);
            }
        }
        
        @Override
        public String tileDesc(int tile) {
            switch (tile) {
                case Terrain.WATER:
                    return "Dark water that reflects no light, hiding unknown depths.";
                case Terrain.HIGH_GRASS:
                    return "Twisted vines that seem to writhe in the darkness.";
                default:
                    return super.tileDesc(tile);
            }
        }
    }
    
    /**
     * 暗影核心房间 - 森林的心脏，同时包含入口和出口
     */
    public static class ShadowHeartRoom extends StandardRoom {
        
        private int entrancePos = -1;
        private int exitPos = -1;
        
        // 添加序列化支持
        private static final String ENTRANCE_POS = "entrance_pos";
        private static final String EXIT_POS = "exit_pos";
        
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ENTRANCE_POS, entrancePos);
            bundle.put(EXIT_POS, exitPos);
        }
        
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            entrancePos = bundle.getInt(ENTRANCE_POS);
            exitPos = bundle.getInt(EXIT_POS);
        }
        
        @Override
        public int minWidth() {
            return Math.max(super.minWidth(), 6); // 减小尺寸
        }
        
        @Override
        public int minHeight() {
            return Math.max(super.minHeight(), 6); // 减小尺寸
        }
        
        @Override
        public void paint(Level level) {
            // 基础房间绘制
            Painter.fill(level, this, Terrain.WALL);
            Painter.fill(level, this, 1, Terrain.EMPTY);
            
            // 创建一个简化的暗影核心区域
            Point center = center();
            
            // 在中心创建暗影祭坛（简化版）
            Painter.set(level, center, Terrain.PEDESTAL);
            
            // 在两端放置入口和出口
            Point entrance = new Point(left + 1, center.y);
            Point exit = new Point(right - 1, center.y);
            
            entrancePos = level.pointToCell(entrance);
            exitPos = level.pointToCell(exit);
            
            // 设置入口楼梯（向上）- 来自主线
            Painter.set(level, entrance, Terrain.ENTRANCE);
            level.transitions.add(new LevelTransition(level, entrancePos, LevelTransition.Type.REGULAR_ENTRANCE));
            
            // 设置分支出口楼梯（向下），指向暗影森林分支（branch = 17）
            Painter.set(level, exit, Terrain.EXIT);
            level.transitions.add(new LevelTransition(level, exitPos, LevelTransition.Type.BRANCH_EXIT, 
                    2, 17, LevelTransition.Type.BRANCH_ENTRANCE));
            
            // 在房间里随机放置少量暗影之草
            for (int i = 0; i < 6; i++) {
                Point grassPos = random(1);
                if (level.map[level.pointToCell(grassPos)] == Terrain.EMPTY && 
                    Random.Int(2) == 0) {
                    level.map[level.pointToCell(grassPos)] = Terrain.HIGH_GRASS;
                }
            }
            
            // 放置一些特殊物品
            if (Random.Int(3) == 0) {
                level.drop(new PotionOfMindVision(), level.pointToCell(random(1)));
            }
        }
        
        @Override
        public boolean isEntrance() {
            return true;
        }
        
        @Override
        public boolean isExit() {
            return true;
        }
    }
    
    /**
     * 暗影森林分支入口楼层
     */
    public static class ShadowForestBranchLevel extends RegularLevel {
        
        @Override
        public String tilesTex() {
            return Assets.Environment.TILES_HALLS;
        }
        
        @Override
        public String waterTex() {
            return Assets.Environment.WATER_HALLS;
        }
        
        @Override
        protected int standardRooms(boolean forceMax) {
            return 4;
        }
        
        @Override
        protected int specialRooms(boolean forceMax) {
            return 0;
        }
        
        @Override
        protected ArrayList<Room> initRooms() {
            ArrayList<Room> initRooms = new ArrayList<>();
            
            // 创建分支入口房间
            ShadowBranchEntranceRoom entranceRoom = new ShadowBranchEntranceRoom();
            initRooms.add(entranceRoom);
            
            // 设置为入口房间
            roomEntrance = entranceRoom;
            
            // 添加其他标准房间
            int standards = standardRooms(feeling == Feeling.LARGE);
            for (int i = 0; i < standards; i++) {
                StandardRoom s;
                do {
                    s = StandardRoom.createRoom();
                } while (!s.setSizeCat(standards - i));
                i += s.sizeFactor() - 1;
                initRooms.add(s);
            }
            
            return initRooms;
        }
        
        @Override
        protected Painter painter() {
            return new HallsPainter()
                    .setWater(feeling == Feeling.WATER ? 0.65f : 0.10f, 4)
                    .setGrass(feeling == Feeling.GRASS ? 0.80f : 0.45f, 3)
                    .setTraps(nTraps(), trapClasses(), trapChances());
        }
        
        @Override
        protected Class<?>[] trapClasses() {
            return new Class<?>[]{GrimTrap.class, ToxicTrap.class};
        }
        
        @Override
        protected float[] trapChances() {
            return new float[]{2, 3};
        }
    }
    
    /**
     * 暗影森林分支入口房间
     */
    public static class ShadowBranchEntranceRoom extends StandardRoom {
        
        // 添加序列化支持
        private static final String BRANCH_ENTRANCE_MARKER = "branch_entrance_marker";
        
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(BRANCH_ENTRANCE_MARKER, true);
        }
        
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            // 分支入口房间的标记，可用于验证
        }
        
        @Override
        public boolean isEntrance() {
            return true;
        }
        
        @Override
        public void paint(Level level) {
            // 基础房间绘制
            Painter.fill(level, this, Terrain.WALL);
            Painter.fill(level, this, 1, Terrain.EMPTY);
            
            // 在房间中心创建分支入口
            Point center = center();
            
            // 创建分支入口楼梯
            Painter.set(level, center, Terrain.ENTRANCE);
            level.transitions.add(new LevelTransition(level, level.pointToCell(center), 
                    LevelTransition.Type.BRANCH_ENTRANCE, 1, 0, LevelTransition.Type.BRANCH_EXIT));
            
            // 添加暗影装饰
            for (int i = 0; i < 4; i++) {
                Point grassPos = random(1);
                if (level.map[level.pointToCell(grassPos)] == Terrain.EMPTY) {
                    level.map[level.pointToCell(grassPos)] = Terrain.HIGH_GRASS;
                }
            }
        }
    }
} 