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

package com.coladungeon.levels.rooms;

import java.util.HashMap;
import java.util.Map;

import com.coladungeon.levels.Level;
import com.coladungeon.levels.Terrain;
import com.coladungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Rect;

/**
 * A utility class for weaving terrain in rooms using a more intuitive API.
 * Provides character-to-terrain mapping for creating room layouts from strings.
 */
public class RoomWeaver {
    
    // Default character-to-terrain mapping
    private static final Map<Character, Integer> DEFAULT_CHAR_MAP = new HashMap<>();
    
    static {
        // Basic terrain types
        DEFAULT_CHAR_MAP.put('#', Terrain.WALL);           // Wall
        DEFAULT_CHAR_MAP.put('.', Terrain.EMPTY);          // Floor
        DEFAULT_CHAR_MAP.put(' ', Terrain.EMPTY);          // Alternative floor
        DEFAULT_CHAR_MAP.put('0', Terrain.EMPTY_SP);       // Empty space
        
        // Door types
        DEFAULT_CHAR_MAP.put('+', Terrain.DOOR);           // Door
        DEFAULT_CHAR_MAP.put('L', Terrain.LOCKED_DOOR);    // Locked door
        DEFAULT_CHAR_MAP.put('S', Terrain.SECRET_DOOR);    // Secret door
        DEFAULT_CHAR_MAP.put('C', Terrain.CRYSTAL_DOOR);   // Crystal door
        
        // Special terrain
        DEFAULT_CHAR_MAP.put('~', Terrain.WATER);          // Water
        DEFAULT_CHAR_MAP.put('"', Terrain.HIGH_GRASS);     // High grass
        DEFAULT_CHAR_MAP.put('\'', Terrain.GRASS);         // Normal grass
        DEFAULT_CHAR_MAP.put('P', Terrain.PEDESTAL);       // Pedestal
        DEFAULT_CHAR_MAP.put('s', Terrain.STATUE);         // Statue
        DEFAULT_CHAR_MAP.put('T', Terrain.TRAP);           // Trap
        DEFAULT_CHAR_MAP.put('_', Terrain.EMPTY_WELL);     // Empty well
        DEFAULT_CHAR_MAP.put('W', Terrain.WELL);           // Well
        DEFAULT_CHAR_MAP.put('I', Terrain.BOOKSHELF);      // Bookshelf
        DEFAULT_CHAR_MAP.put('e', Terrain.EMBERS);         // Embers
        DEFAULT_CHAR_MAP.put('*', Terrain.BARRICADE);      // Barricade
    }
    
    // The character-to-terrain mapping being used for this weaver
    private Map<Character, Integer> charMap;
    
    // The room and level being woven
    private Room room;
    private Level level;
    
    /**
     * Creates a new RoomWeaver for the given room and level.
     * 
     * @param room the room to weave in
     * @param level the level containing the room
     */
    public RoomWeaver(Room room, Level level) {
        this.room = room;
        this.level = level;
        this.charMap = new HashMap<>(DEFAULT_CHAR_MAP);
    }
    
    /**
     * Maps a character to a specific terrain type.
     * 
     * @param c the character to map
     * @param terrain the terrain type to associate with the character
     * @return this RoomWeaver instance for method chaining
     */
    public RoomWeaver setCharMapping(char c, int terrain) {
        charMap.put(c, terrain);
        return this;
    }
    
    /**
     * Retrieves the terrain type associated with a character.
     * 
     * @param c the character to look up
     * @return the associated terrain type, or Terrain.EMPTY if not found
     */
    public int getTerrainForChar(char c) {
        return charMap.getOrDefault(c, Terrain.EMPTY);
    }
    
    /**
     * Sets the terrain at the specified coordinates within the room.
     * Coordinates are relative to the room's top-left corner.
     * 
     * @param x the x-coordinate relative to the room's left edge
     * @param y the y-coordinate relative to the room's top edge
     * @param terrain the terrain type to place
     * @return this RoomWeaver instance for method chaining
     */
    public RoomWeaver set(int x, int y, int terrain) {
        if (x >= 0 && x < room.width() && y >= 0 && y < room.height()) {
            Painter.set(level, room.left + x, room.top + y, terrain);
        }
        return this;
    }
    
    /**
     * Sets the terrain at the specified point within the room.
     * Coordinates are relative to the room's top-left corner.
     * 
     * @param p the point relative to the room's top-left corner
     * @param terrain the terrain type to place
     * @return this RoomWeaver instance for method chaining
     */
    public RoomWeaver set(Point p, int terrain) {
        return set(p.x, p.y, terrain);
    }
    
    /**
     * Sets the terrain at the specified coordinates using a character mapping.
     * 
     * @param x the x-coordinate relative to the room's left edge
     * @param y the y-coordinate relative to the room's top edge
     * @param c the character representing the terrain type
     * @return this RoomWeaver instance for method chaining
     */
    public RoomWeaver setChar(int x, int y, char c) {
        return set(x, y, getTerrainForChar(c));
    }
    
    /**
     * Fills a rectangular area within the room with the specified terrain.
     * Coordinates are relative to the room's top-left corner.
     * 
     * @param left the left edge of the rectangle
     * @param top the top edge of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param terrain the terrain type to fill with
     * @return this RoomWeaver instance for method chaining
     */
    public RoomWeaver fill(int left, int top, int width, int height, int terrain) {
        Painter.fill(level, 
                room.left + left, 
                room.top + top, 
                width, 
                height, 
                terrain);
        return this;
    }
    
    /**
     * Fills a rectangular area within the room with the specified terrain.
     * 
     * @param rect the rectangle to fill, relative to the room's top-left corner
     * @param terrain the terrain type to fill with
     * @return this RoomWeaver instance for method chaining
     */
    public RoomWeaver fill(Rect rect, int terrain) {
        return fill(rect.left, rect.top, rect.width(), rect.height(), terrain);
    }
    
    /**
     * Fills a rectangular area within the room using a character mapping.
     * 
     * @param left the left edge of the rectangle
     * @param top the top edge of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param c the character representing the terrain type
     * @return this RoomWeaver instance for method chaining
     */
    public RoomWeaver fillChar(int left, int top, int width, int height, char c) {
        return fill(left, top, width, height, getTerrainForChar(c));
    }
    
    /**
     * Reads a room layout from a string representation.
     * Each character in the string corresponds to a terrain type according to the character mapping.
     * Lines in the string should be separated by newline characters.
     * 
     * @param layout the string representation of the room layout
     * @return this RoomWeaver instance for method chaining
     */
    public RoomWeaver weaveLayout(String layout) {
        String[] lines = layout.split("\n");
        int height = Math.min(lines.length, room.height());
        
        for (int y = 0; y < height; y++) {
            String line = lines[y];
            int width = Math.min(line.length(), room.width());
            
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                setChar(x, y, c);
            }
        }
        
        return this;
    }
    
    /**
     * Draws a border around the room with the specified terrain.
     * 
     * @param terrain the terrain type for the border
     * @return this RoomWeaver instance for method chaining
     */
    public RoomWeaver border(int terrain) {
        Painter.fill(level, room.left, room.top, room.width(), 1, terrain);
        Painter.fill(level, room.left, room.bottom, room.width(), 1, terrain);
        Painter.fill(level, room.left, room.top + 1, 1, room.height() - 2, terrain);
        Painter.fill(level, room.right, room.top + 1, 1, room.height() - 2, terrain);
        return this;
    }
    
    /**
     * Draws a border around the room using a character mapping.
     * 
     * @param c the character representing the terrain type for the border
     * @return this RoomWeaver instance for method chaining
     */
    public RoomWeaver borderChar(char c) {
        return border(getTerrainForChar(c));
    }
    
    /**
     * Gets the terrain at the specified coordinates within the room.
     * 
     * @param x the x-coordinate relative to the room's left edge
     * @param y the y-coordinate relative to the room's top edge
     * @return the terrain type at the specified location
     */
    public int get(int x, int y) {
        if (x >= 0 && x < room.width() && y >= 0 && y < room.height()) {
            return level.map[(room.top + y) * level.width() + (room.left + x)];
        }
        return Terrain.WALL;
    }
    
    /**
     * Gets the character representation of the terrain at the specified coordinates.
     * 
     * @param x the x-coordinate relative to the room's left edge
     * @param y the y-coordinate relative to the room's top edge
     * @return the character representing the terrain, or '#' if not found
     */
    public char getChar(int x, int y) {
        int terrain = get(x, y);
        for (Map.Entry<Character, Integer> entry : charMap.entrySet()) {
            if (entry.getValue() == terrain) {
                return entry.getKey();
            }
        }
        return '#';
    }
} 