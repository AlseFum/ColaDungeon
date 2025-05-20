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

package com.coladungeon.levels.rooms.standard;

import com.coladungeon.levels.Level;
import com.coladungeon.levels.Terrain;
import com.coladungeon.levels.painters.Painter;
import com.coladungeon.levels.rooms.Room;
import com.coladungeon.levels.rooms.RoomPainter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class ExamplePainterRoom extends StandardRoom {

    // 确保房间足够大以展示所有功能
    @Override
    public int minWidth() { return 9; }
    public int maxWidth() { return 12; }
    
    @Override
    public int minHeight() { return 9; }
    public int maxHeight() { return 12; }
    
    @Override
    public void paint(Level level) {
        // 首先用空地填充房间
        Painter.fill(level, this, Terrain.EMPTY);
        
        // 创建一个RoomPainter实例
        RoomPainter painter = new RoomPainter(this, level);
        
        // 创建一个展示区域来显示RoomPainter的功能
        createShowcase(painter);
        
        // 放置一个标记点，表明这是一个展示房间
        int centerX = width()/2;
        int centerY = height()/2;
        painter.set(centerX, centerY, Terrain.PEDESTAL);
        
        // 添加说明性的标记点，使用不同的地形类型来标识不同的区域
        addSignposts(painter);
        
        // 确保连接点处没有墙壁阻挡
        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
            if (door.x == left) {
                Painter.set(level, door.x, door.y, Terrain.DOOR);
                Painter.set(level, door.x+1, door.y, Terrain.EMPTY);
            } else if (door.x == right) {
                Painter.set(level, door.x, door.y, Terrain.DOOR);
                Painter.set(level, door.x-1, door.y, Terrain.EMPTY);
            } else if (door.y == top) {
                Painter.set(level, door.x, door.y, Terrain.DOOR);
                Painter.set(level, door.x, door.y+1, Terrain.EMPTY);
            } else if (door.y == bottom) {
                Painter.set(level, door.x, door.y, Terrain.DOOR);
                Painter.set(level, door.x, door.y-1, Terrain.EMPTY);
            }
        }
    }
    
    private void createShowcase(RoomPainter painter) {
        int choice = Random.Int(3);
        
        // 根据随机数选择展示不同的功能
        switch (choice) {
            case 0:
                // 展示基本绘制功能
                showcaseBasicPainting(painter);
                break;
            case 1:
                // 展示字符串布局功能
                showcaseStringLayout(painter);
                break;
            case 2:
                // 展示自定义图案功能
                showcaseCustomPattern(painter);
                break;
        }
    }
    
    private void addSignposts(RoomPainter painter) {
        // 在房间顶部添加书架标记，表示这是一个展示区
        painter.set(width()/2-1, 1, Terrain.BOOKSHELF);
        painter.set(width()/2, 1, Terrain.BOOKSHELF);
        painter.set(width()/2+1, 1, Terrain.BOOKSHELF);
        
        // 在房间底部添加井，表示这是一个实验区域
        painter.set(width()/2, height()-2, Terrain.WELL);
    }
    
    private void showcaseBasicPainting(RoomPainter painter) {
        // 创建墙壁边框（但不包括边缘，以便连接点可以正常工作）
        for (int i = 2; i < width()-2; i++) {
            painter.set(i, 1, Terrain.WALL);
            painter.set(i, height()-2, Terrain.WALL);
        }
        for (int i = 2; i < height()-2; i++) {
            painter.set(1, i, Terrain.WALL);
            painter.set(width()-2, i, Terrain.WALL);
        }
        
        // 用可视的区域分隔房间为四个象限
        painter.fill(0, height()/2-1, width(), 1, Terrain.WALL_DECO);
        painter.fill(width()/2-1, 0, 1, height(), Terrain.WALL_DECO);
        
        // 左上象限：展示地形填充
        painter.fill(2, 2, width()/2-3, height()/2-3, Terrain.GRASS);
        painter.set(width()/4, height()/4, Terrain.HIGH_GRASS);
        
        // 右上象限：展示水和深渊
        painter.fill(width()/2, 2, width()/2-3, height()/2-3, Terrain.WATER);
        painter.set(width()*3/4, height()/4, Terrain.CHASM);
        
        // 左下象限：展示陷阱和门
        painter.fill(2, height()/2, width()/2-3, height()/2-3, Terrain.EMPTY);
        painter.set(width()/4, height()*3/4, Terrain.TRAP);
        painter.set(width()/4+2, height()*3/4, Terrain.INACTIVE_TRAP);
        painter.set(width()/4-1, height()*3/4-1, Terrain.DOOR);
        
        // 右下象限：展示特殊物体
        painter.set(width()*3/4, height()*3/4-1, Terrain.PEDESTAL);
        painter.set(width()*3/4, height()*3/4+1, Terrain.STATUE);
        painter.set(width()*3/4-1, height()*3/4, Terrain.ALCHEMY);
        painter.set(width()*3/4+1, height()*3/4, Terrain.BARRICADE);
    }
    
    private void showcaseStringLayout(RoomPainter painter) {
        // 使用字符串布局来创建一个有趣的房间设计
        // 注意边缘留空，以确保连接点可以工作
        String layout;
        
        if (width() <= 9 || height() <= 9) {
            layout = 
                "#######\n" +
                "#..~..#\n" +
                "#.###.#\n" +
                "#.#,#.#\n" +
                "~.#P#.~\n" +
                "#.#,#.#\n" +
                "#.###.#\n" +
                "#..~..#\n" +
                "#######";
        } else {
            layout = 
                "#########\n" +
                "#..~.~..#\n" +
                "#.#####.#\n" +
                "#.#,,,#.#\n" +
                "~.#,,,#.~\n" +
                "~.#,P,#.~\n" +
                "~.#,,,#.~\n" +
                "#.#,,,#.#\n" +
                "#.#####.#\n" +
                "#..~.~..#\n" +
                "#########";
        }
        
        // 计算布局在房间中的偏移位置（居中，并确保墙不会覆盖边缘）
        int layoutWidth = layout.split("\n")[0].length();
        int layoutHeight = layout.split("\n").length;
        int offsetX = (width() - layoutWidth) / 2 + 1;
        int offsetY = (height() - layoutHeight) / 2 + 1;
        
        // 分行处理字符串布局
        String[] lines = layout.split("\n");
        for (int y = 0; y < lines.length; y++) {
            for (int x = 0; x < lines[y].length(); x++) {
                int roomX = x + offsetX;
                int roomY = y + offsetY;
                
                // 确保我们不覆盖房间边缘（为连接预留空间）
                if (roomX > 0 && roomX < width()-1 && roomY > 0 && roomY < height()-1) {
                    painter.setChar(roomX, roomY, lines[y].charAt(x));
                }
            }
        }
        
        // 添加一些额外的地形点以展示功能（避开边缘区域）
        int centerX = width()/2;
        int centerY = height()/2;
        
        // 放置各种门类型以展示它们（确保不在边缘）
        painter.set(centerX, centerY-2, Terrain.DOOR);
        painter.set(centerX-2, centerY, Terrain.SECRET_DOOR);
        painter.set(centerX+2, centerY, Terrain.CRYSTAL_DOOR);
    }
    
    private void showcaseCustomPattern(RoomPainter painter) {
        // 添加自定义字符映射
        painter.setCharMapping('F', Terrain.FURROWED_GRASS)
               .setCharMapping('b', Terrain.BARRICADE)
               .setCharMapping('e', Terrain.EMBERS)
               .setCharMapping('W', Terrain.WELL)
               .setCharMapping('A', Terrain.ALCHEMY);
        
        // 创建一个复杂的迷宫样式图案（确保边缘留空，为连接点预留空间）
        String layout;
        
        if (width() <= 9 || height() <= 9) {
            layout = 
                "#######\n" +
                "#F.b.F#\n" +
                "#.###.#\n" +
                "#b...b#\n" +
                "#F.P.F#\n" +
                "#b...b#\n" +
                "#.###.#\n" +
                "#F.e.F#\n" +
                "#######";
        } else {
            layout = 
                "#########\n" +
                "#F.b.b.F#\n" +
                "#F#####F#\n" +
                "#b..A..b#\n" +
                "#.#####.#\n" +
                "#W..P..W#\n" +
                "#.#####.#\n" +
                "#b..e..b#\n" +
                "#F#####F#\n" +
                "#F.b.b.F#\n" +
                "#########";
        }
        
        // 计算布局在房间中的偏移位置（居中，并确保墙不会覆盖边缘）
        int layoutWidth = layout.split("\n")[0].length();
        int layoutHeight = layout.split("\n").length;
        int offsetX = (width() - layoutWidth) / 2 + 1;
        int offsetY = (height() - layoutHeight) / 2 + 1;
        
        // 分行处理字符串布局
        String[] lines = layout.split("\n");
        for (int y = 0; y < lines.length; y++) {
            for (int x = 0; x < lines[y].length(); x++) {
                int roomX = x + offsetX;
                int roomY = y + offsetY;
                
                // 确保我们不覆盖房间边缘（为连接预留空间）
                if (roomX > 0 && roomX < width()-1 && roomY > 0 && roomY < height()-1) {
                    painter.setChar(roomX, roomY, lines[y].charAt(x));
                }
            }
        }
        
        // 添加一些额外的水域和高草来丰富房间（避开边缘区域）
        for (int x = 2; x < width()-2; x++) {
            for (int y = 2; y < height()-2; y++) {
                if (painter.get(x, y) == Terrain.EMPTY && Random.Int(12) == 0) {
                    painter.set(x, y, Random.Int(2) == 0 ? Terrain.WATER : Terrain.HIGH_GRASS);
                }
            }
        }
    }
} 