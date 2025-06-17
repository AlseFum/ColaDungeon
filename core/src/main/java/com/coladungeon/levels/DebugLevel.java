package com.coladungeon.levels;

import com.coladungeon.Assets;
import com.coladungeon.Bones;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.items.Heap;
import com.coladungeon.items.Item;
import com.coladungeon.levels.features.LevelTransition;
import com.coladungeon.levels.rooms.standard.ExamplePainterRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DebugLevel extends Level {

    private static final int SIZE = 20; // Increased size to better fit our room

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
        setSize(SIZE + 2, SIZE + 2);

        // Fill with walls first
        for (int i = 0; i < length(); i++) {
            map[i] = Terrain.WALL;
        }

        // Clear the main area
        for (int i = 2; i < SIZE; i++) {
            for (int j = 2; j < SIZE; j++) {
                map[i * width() + j] = Terrain.EMPTY;
            }
        }

        // Border with water
        for (int i = 1; i <= SIZE; i++) {
            map[width() + i] = map[width() * SIZE + i] = map[width() * i + 1] = map[width() * i + SIZE] = Terrain.WATER;
        }

        // Create an ExamplePainterRoom
        ExamplePainterRoom room = new ExamplePainterRoom();
        
        // Set the room's position (in the center)
        int roomLeft = 5;
        int roomTop = 5;
        room.set(roomLeft, roomTop, roomLeft + room.maxWidth(), roomTop + room.maxHeight());
        
        // Paint the room
        room.paint(this);

        // Position the entrance
        int entrance = SIZE * width() + SIZE / 2 + 1;
        transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
        map[entrance] = Terrain.ENTRANCE;

        // Add an exit inside the room
        Point roomCenter = new Point(roomLeft + room.width() / 2, roomTop + room.height() / 2);
        int exit = roomCenter.x + roomCenter.y * width();
        transitions.add(new LevelTransition(this, exit, LevelTransition.Type.REGULAR_EXIT));
        map[exit] = Terrain.EXIT;

        return true;
    }


    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
        // No mobs in debug level
    }

    @Override
    public Actor addRespawner() {
        return null;
    }

    @Override
    protected void createItems() {
        Random.pushGenerator(Random.Long());
        ArrayList<Item> bonesItems = Bones.get();
        if (bonesItems != null) {
            for (Item i : bonesItems) {
                drop(i, entrance() - width()).setHauntedIfCursed().type = Heap.Type.REMAINS;
            }
        }
        Random.popGenerator();
    }

    @Override
    public int randomRespawnCell(Char ch) {
        return entrance() - width();
    }
}
