package com.cola;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.watabou.utils.Point;
import com.watabou.utils.Rect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 房间可视化工具
 * 用于在调试过程中显示房间布局和内容
 */
public class RoomDrawer {
    
    // 地形类型到字符的映射
    private static final Map<Integer, Character> TERRAIN_CHARS = new HashMap<>();
    
    static {
        // 基本地形符号
        TERRAIN_CHARS.put(Terrain.EMPTY, '.');
        TERRAIN_CHARS.put(Terrain.WALL, '#');
        TERRAIN_CHARS.put(Terrain.DOOR, '+');
        TERRAIN_CHARS.put(Terrain.OPEN_DOOR, '\'');
        TERRAIN_CHARS.put(Terrain.ENTRANCE, '<');
        TERRAIN_CHARS.put(Terrain.EXIT, '>');
        TERRAIN_CHARS.put(Terrain.EMBERS, ',');
        TERRAIN_CHARS.put(Terrain.WATER, '~');
        TERRAIN_CHARS.put(Terrain.GRASS, '"');
        TERRAIN_CHARS.put(Terrain.HIGH_GRASS, ':');
        TERRAIN_CHARS.put(Terrain.CHASM, ' ');
        TERRAIN_CHARS.put(Terrain.TRAP, '^');
        TERRAIN_CHARS.put(Terrain.SECRET_TRAP, '^');
        TERRAIN_CHARS.put(Terrain.INACTIVE_TRAP, '_');
        TERRAIN_CHARS.put(Terrain.STATUE, '|');
        TERRAIN_CHARS.put(Terrain.ALCHEMY, '=');
        TERRAIN_CHARS.put(Terrain.BARRICADE, 'B');
        TERRAIN_CHARS.put(Terrain.EMPTY_WELL, 'W');
        TERRAIN_CHARS.put(Terrain.WELL, 'W');
        TERRAIN_CHARS.put(Terrain.BOOKSHELF, '&');
        TERRAIN_CHARS.put(Terrain.EMPTY_DECO, '%');
    }
    
    /**
     * 获取地形类型的字符表示
     */
    public static char getTerrain(int terrain) {
        Character c = TERRAIN_CHARS.get(terrain);
        return c != null ? c : '?';
    }
    
    /**
     * 将单个房间绘制为字符串
     * @param level 当前关卡
     * @param room 要绘制的房间
     * @return 房间的字符串表示
     */
    public static String drawRoom(Level level, Room room) {
        StringBuilder sb = new StringBuilder();
        
        Rect r = room;
        
        // 房间标题信息
        sb.append("Room: ").append(room.getClass().getSimpleName())
          .append(" (").append(r.left).append(",").append(r.top)
          .append(" to ").append(r.right).append(",").append(r.bottom)
          .append(") Size: ").append(room.width()).append("x").append(room.height());
        sb.append("\n");
        
        // 绘制房间内容
        for (int y = r.top - 1; y <= r.bottom + 1; y++) {
            for (int x = r.left - 1; x <= r.right + 1; x++) {
                int pos = x + y * level.width();
                
                // 检查坐标是否在地图范围内
                if (x < 0 || y < 0 || x >= level.width() || y >= level.height()) {
                    sb.append(' ');
                    continue;
                }
                
                // 检查该位置的角色
                Char ch = Actor.findChar(pos);
                
                // 检查该位置的物品
                Heap heap = level.heaps.get(pos);
                
                // 按优先级绘制: 角色 > 物品 > 地形
                if (ch != null) {
                    if (ch == Dungeon.hero) {
                        sb.append('@');
                    } else if (ch instanceof Mob) {
                        sb.append('M');
                    } else {
                        sb.append('C');
                    }
                } else if (heap != null) {
                    sb.append('$');
                } else {
                    int terrain = level.map[pos];
                    sb.append(getTerrain(terrain));
                }
            }
            sb.append('\n');
        }
        
        // 添加连接信息
        if (!room.connected.isEmpty()) {
            sb.append("Connected to: ");
            for (Room connected : room.connected.keySet()) {
                sb.append(connected.getClass().getSimpleName()).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 绘制房间的连接结构
     * @param rooms 要绘制的房间列表
     * @return 连接结构的字符串表示
     */
    public static String drawRoomConnections(ArrayList<Room> rooms) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Room Connections:\n");
        
        for (int i = 0; i < rooms.size(); i++) {
            Room r = rooms.get(i);
            sb.append(i).append(": ").append(r.getClass().getSimpleName());
            
            if (!r.connected.isEmpty()) {
                sb.append(" connected to ");
                for (Room connected : r.connected.keySet()) {
                    int connectedIndex = rooms.indexOf(connected);
                    if (connectedIndex >= 0) {
                        sb.append(connectedIndex).append(", ");
                    }
                }
                sb.delete(sb.length() - 2, sb.length());
            }
            
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 绘制所有房间的布局
     * @param level 当前关卡
     * @param rooms 房间列表
     * @return 所有房间布局的字符串表示
     */
    public static String drawAllRooms(Level level, ArrayList<Room> rooms) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Level contains ").append(rooms.size()).append(" rooms:\n");
        
        for (int i = 0; i < rooms.size(); i++) {
            sb.append("Room ").append(i).append(":\n");
            sb.append(drawRoom(level, rooms.get(i)));
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 创建房间的ASCII地图，包含所有房间
     * @param level 当前关卡
     * @param rooms 房间列表
     * @return 整个关卡的ASCII地图
     */
    public static String createRoomMap(Level level, ArrayList<Room> rooms) {
        // 创建地图数组
        char[][] map = new char[level.width()][level.height()];
        
        // 初始化地图为空白
        for (int y = 0; y < level.height(); y++) {
            for (int x = 0; x < level.width(); x++) {
                map[x][y] = ' ';
            }
        }
        
        // 为每个房间绘制边界
        for (int i = 0; i < rooms.size(); i++) {
            Room r = rooms.get(i);
            
            // 绘制房间边框
            for (int x = r.left; x <= r.right; x++) {
                for (int y = r.top; y <= r.bottom; y++) {
                    // 边框
                    if (x == r.left || x == r.right || y == r.top || y == r.bottom) {
                        map[x][y] = '#';
                    } 
                    // 房间内部
                    else {
                        // 只有当位置为空白时才填充，避免覆盖其他信息
                        if (map[x][y] == ' ') {
                            int pos = x + y * level.width();
                            int terrain = level.map[pos];
                            map[x][y] = getTerrain(terrain);
                        }
                    }
                }
            }
            
            // 标记房间编号
            Point center = r.center();
            map[center.x][center.y] = (char)('0' + (i % 10));
        }
        
        // 标记门
        for (Room r : rooms) {
            for (Map.Entry<Room, Room.Door> entry : r.connected.entrySet()) {
                Room.Door door = entry.getValue();
                if (door != null) {
                    map[door.x][door.y] = '+';
                }
            }
        }
        
        // 转换为字符串
        StringBuilder sb = new StringBuilder();
        
        for (int y = 0; y < level.height(); y++) {
            for (int x = 0; x < level.width(); x++) {
                sb.append(map[x][y]);
            }
            sb.append('\n');
        }
        
        return sb.toString();
    }
    
    /**
     * 获取房间统计信息
     * @param level 当前关卡
     * @param rooms 房间列表
     * @return 房间统计的字符串表示
     */
    public static String getRoomStats(Level level, ArrayList<Room> rooms) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Room Statistics:\n");
        
        // 按类型统计房间
        Map<String, Integer> roomTypes = new HashMap<>();
        
        for (Room r : rooms) {
            String type = r.getClass().getSimpleName();
            roomTypes.put(type, roomTypes.getOrDefault(type, 0) + 1);
        }
        
        sb.append("Room Types:\n");
        for (Map.Entry<String, Integer> entry : roomTypes.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        // 计算平均房间大小
        float avgWidth = 0;
        float avgHeight = 0;
        
        for (Room r : rooms) {
            avgWidth += r.width();
            avgHeight += r.height();
        }
        
        avgWidth /= rooms.size();
        avgHeight /= rooms.size();
        
        sb.append("Average Room Size: ").append(String.format("%.1f", avgWidth))
          .append(" x ").append(String.format("%.1f", avgHeight)).append("\n");
        
        return sb.toString();
    }
} 