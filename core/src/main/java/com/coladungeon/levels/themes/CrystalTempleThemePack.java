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
import com.coladungeon.actors.mobs.CrystalGuardian;
import com.coladungeon.actors.mobs.CrystalWisp;
import com.coladungeon.items.Generator;
import com.coladungeon.items.artifacts.TimekeepersHourglass;
import com.coladungeon.levels.Level;
import com.coladungeon.levels.RegularLevel;
import com.coladungeon.levels.Terrain;
import com.coladungeon.levels.features.LevelTransition;
import com.coladungeon.levels.painters.CavesPainter;
import com.coladungeon.levels.painters.Painter;
import com.coladungeon.levels.rooms.Room;
import com.coladungeon.levels.rooms.standard.StandardRoom;
import com.coladungeon.levels.traps.SummoningTrap;
import com.coladungeon.levels.traps.WornDartTrap;
import com.coladungeon.messages.Messages;
import com.coladungeon.sprites.ItemSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.tiles.DungeonTileSheet;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

/**
 * 水晶神殿主题包 - 一个充满水晶的神秘神殿
 */
public class CrystalTempleThemePack {
    
    public static String name() {
        return "Crystal Temple";
    }
    
    public static String desc() {
        return "A mystical temple filled with glowing crystals and ancient magic.";
    }
    
    public static ItemSprite icon() {
        return new ItemSprite(ItemSpriteSheet.CRYSTAL_CHEST, null);
    }
    
    /**
     * 水晶神殿楼层
     */
    public static class CrystalTempleLevel extends RegularLevel {
        
        @Override
        public String tilesTex() {
            return Assets.Environment.TILES_CAVES;
        }
        
        @Override
        public String waterTex() {
            return Assets.Environment.WATER_CAVES;
        }
        
        @Override
        protected int standardRooms(boolean forceMax) {
            return 3; // 大幅减少房间数量，从6减少到3
        }
        
        @Override
        protected int specialRooms(boolean forceMax) {
            return 1; // 减少特殊房间，从2减少到1
        }
        
        @Override
        protected ArrayList<Room> initRooms() {
            ArrayList<Room> initRooms = new ArrayList<>();
            
            // 创建特殊的圣殿中心房间，同时包含入口和出口
            CrystalTempleRoom templeRoom = new CrystalTempleRoom();
            initRooms.add(templeRoom);
            
            // 设置为入口和出口房间
            roomEntrance = templeRoom;
            roomExit = templeRoom;
            
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
            return new CavesPainter()
                    .setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 6)
                    .setGrass(feeling == Feeling.GRASS ? 0.65f : 0.15f, 3)
                    .setTraps(nTraps(), trapClasses(), trapChances());
        }
        
        @Override
        protected Class<?>[] trapClasses() {
            return new Class<?>[]{SummoningTrap.class, WornDartTrap.class};
        }
        
        @Override
        protected float[] trapChances() {
            return new float[]{3, 1};
        }
        
        @Override
        public String tileName(int tile) {
            switch (tile) {
                case Terrain.WATER:
                    return Messages.get(CrystalTempleLevel.class, "water_name");
                case Terrain.HIGH_GRASS:
                    return Messages.get(CrystalTempleLevel.class, "high_grass_name");
                default:
                    return super.tileName(tile);
            }
        }
        
        @Override
        public String tileDesc(int tile) {
            switch (tile) {
                case Terrain.WATER:
                    return Messages.get(CrystalTempleLevel.class, "water_desc");
                case Terrain.HIGH_GRASS:
                    return Messages.get(CrystalTempleLevel.class, "high_grass_desc");
                default:
                    return super.tileDesc(tile);
            }
        }
    }
    
    /**
     * 水晶神殿房间 - 同时包含入口和出口的特殊房间
     */
    public static class CrystalTempleRoom extends StandardRoom {
        
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
            return Math.max(super.minWidth(), 6); // 进一步减小尺寸
        }
        
        @Override
        public int minHeight() {
            return Math.max(super.minHeight(), 6); // 进一步减小尺寸
        }
        
        @Override
        public void paint(Level level) {
            // 基础房间绘制
            Painter.fill(level, this, Terrain.WALL);
            Painter.fill(level, this, 1, Terrain.EMPTY);
            
            // 在房间中心创建水晶祭坛
            Point center = center();
            
            // 创建中央水晶柱（简化版）
            Painter.set(level, center, Terrain.PEDESTAL);
            
            // 在房间的两个对角放置入口和出口（更靠近边缘）
            Point entrance = new Point(left + 1, top + 1);
            Point exit = new Point(right - 1, bottom - 1);
            
            entrancePos = level.pointToCell(entrance);
            exitPos = level.pointToCell(exit);
            
            // 设置入口楼梯（向上）- 来自主线
            Painter.set(level, entrance, Terrain.ENTRANCE);
            level.transitions.add(new LevelTransition(level, entrancePos, LevelTransition.Type.REGULAR_ENTRANCE));
            
            // 设置分支出口楼梯（向下），指向水晶神殿分支（branch = 16）
            Painter.set(level, exit, Terrain.EXIT);
            level.transitions.add(new LevelTransition(level, exitPos, LevelTransition.Type.BRANCH_EXIT, 
                    2, 16, LevelTransition.Type.BRANCH_ENTRANCE));
            
            // 在房间周围放置少量水晶装饰
            for (int i = 0; i < 4; i++) {
                Point crystalPos = random(1);
                if (level.map[level.pointToCell(crystalPos)] == Terrain.EMPTY) {
                    level.map[level.pointToCell(crystalPos)] = Terrain.HIGH_GRASS;
                }
            }
            
            // 放置特殊物品
            if (Random.Int(4) == 0) {
                level.drop(Generator.random(Generator.Category.POTION), level.pointToCell(center().offset(Random.Int(-1, 2), Random.Int(-1, 2))));
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
     * 水晶神殿分支入口楼层
     */
    public static class CrystalTempleBranchLevel extends RegularLevel {
        
        @Override
        public String tilesTex() {
            return Assets.Environment.TILES_CAVES;
        }
        
        @Override
        public String waterTex() {
            return Assets.Environment.WATER_CAVES;
        }
        
        @Override
        protected int standardRooms(boolean forceMax) {
            return 3;
        }
        
        @Override
        protected int specialRooms(boolean forceMax) {
            return 1;
        }
        
        @Override
        protected ArrayList<Room> initRooms() {
            ArrayList<Room> initRooms = new ArrayList<>();
            
            // 创建分支入口房间
            CrystalBranchEntranceRoom entranceRoom = new CrystalBranchEntranceRoom();
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
            return new CavesPainter()
                    .setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 6)
                    .setGrass(feeling == Feeling.GRASS ? 0.65f : 0.15f, 3)
                    .setTraps(nTraps(), trapClasses(), trapChances());
        }
        
        @Override
        protected Class<?>[] trapClasses() {
            return new Class<?>[]{SummoningTrap.class, WornDartTrap.class};
        }
        
        @Override
        protected float[] trapChances() {
            return new float[]{3, 1};
        }
    }
    
    /**
     * 水晶神殿分支入口房间
     */
    public static class CrystalBranchEntranceRoom extends StandardRoom {
        
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
            
            // 添加水晶装饰
            for (int i = 0; i < 3; i++) {
                Point crystalPos = random(1);
                if (level.map[level.pointToCell(crystalPos)] == Terrain.EMPTY) {
                    level.map[level.pointToCell(crystalPos)] = Terrain.HIGH_GRASS;
                }
            }
        }
    }
} 