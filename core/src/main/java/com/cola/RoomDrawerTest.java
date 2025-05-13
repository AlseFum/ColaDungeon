package com.cola;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.EntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.ExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.ConnectionRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.TunnelRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 房间绘制器测试类
 * 展示如何使用RoomDrawer类
 */
public class RoomDrawerTest {
    
    /**
     * 使用反射从RegularLevel获取rooms字段
     * @param level 关卡实例
     * @return 房间列表，如果不可用则返回null
     */
    @SuppressWarnings("unchecked")
    private static ArrayList<Room> getRooms(Level level) {
        if (!(level instanceof RegularLevel)) {
            return null;
        }
        
        try {
            Field roomsField = RegularLevel.class.getDeclaredField("rooms");
            roomsField.setAccessible(true);
            return (ArrayList<Room>) roomsField.get(level);
        } catch (Exception e) {
            System.out.println("Error accessing rooms: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 在日志中打印当前关卡的所有房间信息
     */
    public static void debugCurrentLevelRooms() {
        if (Dungeon.level == null) {
            System.out.println("No level loaded!");
            return;
        }
        
        // 只有RegularLevel才有rooms字段
        if (!(Dungeon.level instanceof RegularLevel)) {
            System.out.println("Current level doesn't support room inspection!");
            return;
        }
        
        ArrayList<Room> rooms = getRooms(Dungeon.level);
        
        if (rooms == null || rooms.isEmpty()) {
            System.out.println("No rooms found in current level!");
            return;
        }
        
        // 打印所有房间信息
        String roomInfo = RoomDrawer.drawAllRooms(Dungeon.level, rooms);
        System.out.println(roomInfo);
        
        // 打印房间连接信息
        String connectionInfo = RoomDrawer.drawRoomConnections(rooms);
        System.out.println(connectionInfo);
        
        // 打印整个地图布局
        String mapLayout = RoomDrawer.createRoomMap(Dungeon.level, rooms);
        System.out.println(mapLayout);
        
        // 打印房间统计信息
        String stats = RoomDrawer.getRoomStats(Dungeon.level, rooms);
        System.out.println(stats);
    }
    
    /**
     * 打印特定房间的详细信息
     * @param level 当前关卡
     * @param roomIndex 要打印的房间索引
     */
    public static void debugRoom(Level level, int roomIndex) {
        ArrayList<Room> rooms = getRooms(level);
        
        if (rooms == null || roomIndex < 0 || roomIndex >= rooms.size()) {
            System.out.println("Invalid level or room index!");
            return;
        }
        
        Room room = rooms.get(roomIndex);
        String roomInfo = RoomDrawer.drawRoom(level, room);
        System.out.println(roomInfo);
    }
    
    /**
     * 打印特定类型房间的信息
     * @param level 当前关卡
     * @param roomClass 房间类型的Class对象
     */
    public static void debugRoomsByType(Level level, Class<? extends Room> roomClass) {
        ArrayList<Room> rooms = getRooms(level);
        
        if (rooms == null) {
            System.out.println("Invalid level!");
            return;
        }
        
        ArrayList<Room> matchingRooms = new ArrayList<>();
        
        for (Room r : rooms) {
            if (roomClass.isInstance(r)) {
                matchingRooms.add(r);
            }
        }
        
        if (matchingRooms.isEmpty()) {
            System.out.println("No rooms of type " + roomClass.getSimpleName() + " found!");
            return;
        }
        
        System.out.println("Found " + matchingRooms.size() + " rooms of type " + roomClass.getSimpleName() + ":");
        for (Room r : matchingRooms) {
            System.out.println(RoomDrawer.drawRoom(level, r));
        }
    }
    
    /**
     * 在控制台打印入口房间和出口房间的信息
     */
    public static void debugEntranceAndExit() {
        if (Dungeon.level == null) {
            System.out.println("Invalid level!");
            return;
        }
        
        ArrayList<Room> rooms = getRooms(Dungeon.level);
        
        if (rooms == null) {
            System.out.println("No rooms found in level!");
            return;
        }
        
        Room entrance = null;
        Room exit = null;
        
        for (Room r : rooms) {
            if (r.isEntrance()) {
                entrance = r;
            } else if (r.isExit()) {
                exit = r;
            }
            
            if (entrance != null && exit != null) {
                break;
            }
        }
        
        if (entrance != null) {
            System.out.println("Entrance Room:");
            System.out.println(RoomDrawer.drawRoom(Dungeon.level, entrance));
        } else {
            System.out.println("No entrance room found!");
        }
        
        if (exit != null) {
            System.out.println("Exit Room:");
            System.out.println(RoomDrawer.drawRoom(Dungeon.level, exit));
        } else {
            System.out.println("No exit room found!");
        }
    }
    
    /**
     * 示例：如何在游戏内部使用这个功能
     * 
     * 这个方法可以被游戏中的某个控制台命令或调试功能调用
     * @param args 命令参数
     */
    public static void executeDebugCommand(String[] args) {
        if (args.length == 0) {
            // 默认显示所有房间信息
            debugCurrentLevelRooms();
            return;
        }
        
        String command = args[0].toLowerCase();
        
        switch (command) {
            case "all":
                debugCurrentLevelRooms();
                break;
                
            case "room":
                if (args.length >= 2) {
                    try {
                        int roomIndex = Integer.parseInt(args[1]);
                        debugRoom(Dungeon.level, roomIndex);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid room index: " + args[1]);
                    }
                } else {
                    System.out.println("Usage: room <roomIndex>");
                }
                break;
                
            case "type":
                if (args.length >= 2) {
                    String type = args[1].toLowerCase();
                    
                    // 根据类型名称确定对应的Class
                    Class<? extends Room> roomClass = null;
                    
                    if (type.equals("standard")) {
                        roomClass = StandardRoom.class;
                    } else if (type.equals("entrance")) {
                        roomClass = EntranceRoom.class;
                    } else if (type.equals("exit")) {
                        roomClass = ExitRoom.class;
                    } else if (type.equals("connection") || type.equals("tunnel")) {
                        roomClass = ConnectionRoom.class;
                    } else if (type.equals("special")) {
                        roomClass = SpecialRoom.class;
                    } else {
                        System.out.println("Unknown room type: " + type);
                        break;
                    }
                    
                    if (roomClass != null) {
                        debugRoomsByType(Dungeon.level, roomClass);
                    }
                } else {
                    System.out.println("Usage: type <roomType>");
                }
                break;
                
            case "entrance-exit":
            case "gates":
                debugEntranceAndExit();
                break;
                
            case "help":
            default:
                System.out.println("Room Drawer Debug Commands:");
                System.out.println("  all - Show all rooms");
                System.out.println("  room <index> - Show details for a specific room");
                System.out.println("  type <type> - Show rooms of a specific type (standard, entrance, exit, connection, special)");
                System.out.println("  entrance-exit - Show entrance and exit rooms");
                System.out.println("  help - Show this help message");
        }
    }
} 