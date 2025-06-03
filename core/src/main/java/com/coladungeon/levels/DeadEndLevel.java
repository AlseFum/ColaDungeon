package com.coladungeon.levels;

import com.coladungeon.Assets;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.levels.features.LevelTransition;
import com.coladungeon.levels.rooms.Room;
import com.coladungeon.levels.rooms.special.RuinedAltarRoom;
import com.watabou.utils.Point;

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
        altarRoom.connected.put(null, new Room.Door(altarRoom.left + SIZE / 2, altarRoom.top)); // 上门
        altarRoom.connected.put(null, new Room.Door(altarRoom.left + SIZE / 2, altarRoom.bottom)); // 下门
        altarRoom.paint(this);

        // 入口（向上）
        int _entrance = (SIZE + 6) * (SIZE + 3) + (SIZE + 6) / 2;

        transitions.add(
			new LevelTransition(this,
								 _entrance,
								  LevelTransition.Type.REGULAR_ENTRANCE));

        // 出口（向下）
        int _exit = (SIZE + 6) * 3 + (SIZE + 6) / 2;

        transitions.add(
			new LevelTransition(this,
								 _exit,
								  LevelTransition.Type.REGULAR_EXIT));

        // 创建通道
        Point entrancePoint = cellToPoint(_entrance);
        Point exitPoint = cellToPoint(_exit);

        // // 连接入口到祭坛房间
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
        map[_entrance] = Terrain.ENTRANCE;
        map[_exit] = Terrain.EXIT;

        return true;
    }

    @Override
    protected void createItems() {
    }

    @Override
    public int randomRespawnCell(Char ch) {
        return entrance() - width();
    }

    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
    }

    @Override
    public Actor addRespawner() {
        return null;
    }
}
