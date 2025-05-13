package com.cola;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.watabou.utils.Random;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class MapDrawer {
    
    private static final Map<Integer, Character> TERRAIN_CHARS = new HashMap<>();
    private static final Map<Integer, String> TERRAIN_NAMES = new HashMap<>();
    private static final Map<Character, Integer> CHAR_TO_TERRAIN = new HashMap<>();
    
    // 物品符号到物品类型的映射
    private static final Map<Character, String> ITEM_SYMBOLS = new HashMap<>();
    
    static {
        // Basic terrain symbols
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
        
        // Terrain names for the legend
        TERRAIN_NAMES.put(Terrain.EMPTY, "Empty");
        TERRAIN_NAMES.put(Terrain.WALL, "Wall");
        TERRAIN_NAMES.put(Terrain.DOOR, "Door");
        TERRAIN_NAMES.put(Terrain.OPEN_DOOR, "Open Door");
        TERRAIN_NAMES.put(Terrain.ENTRANCE, "Entrance");
        TERRAIN_NAMES.put(Terrain.EXIT, "Exit");
        TERRAIN_NAMES.put(Terrain.EMBERS, "Embers");
        TERRAIN_NAMES.put(Terrain.WATER, "Water");
        TERRAIN_NAMES.put(Terrain.GRASS, "Grass");
        TERRAIN_NAMES.put(Terrain.HIGH_GRASS, "High Grass");
        TERRAIN_NAMES.put(Terrain.CHASM, "Chasm");
        TERRAIN_NAMES.put(Terrain.TRAP, "Trap");
        TERRAIN_NAMES.put(Terrain.SECRET_TRAP, "Secret Trap");
        TERRAIN_NAMES.put(Terrain.INACTIVE_TRAP, "Inactive Trap");
        TERRAIN_NAMES.put(Terrain.STATUE, "Statue");
        TERRAIN_NAMES.put(Terrain.ALCHEMY, "Alchemy Pot");
        TERRAIN_NAMES.put(Terrain.BARRICADE, "Barricade");
        TERRAIN_NAMES.put(Terrain.EMPTY_WELL, "Empty Well");
        TERRAIN_NAMES.put(Terrain.WELL, "Well");
        TERRAIN_NAMES.put(Terrain.BOOKSHELF, "Bookshelf");
        TERRAIN_NAMES.put(Terrain.EMPTY_DECO, "Decoration");
        
        // Create reverse mapping for string-to-level conversion
        for (Map.Entry<Integer, Character> entry : TERRAIN_CHARS.entrySet()) {
            CHAR_TO_TERRAIN.put(entry.getValue(), entry.getKey());
        }
        
        // Add special mappings
        CHAR_TO_TERRAIN.put(' ', Terrain.CHASM);  // Space is chasm
        CHAR_TO_TERRAIN.put('M', Terrain.EMPTY);  // M will be empty with monster added later
        CHAR_TO_TERRAIN.put('@', Terrain.EMPTY);  // @ will be empty with hero added later
        CHAR_TO_TERRAIN.put('$', Terrain.EMPTY);  // $ will be empty with gold added later
        
        // 物品符号定义
        ITEM_SYMBOLS.put('$', "gold");         // 金币
        ITEM_SYMBOLS.put('?', "scroll");       // 卷轴
        ITEM_SYMBOLS.put('!', "potion");       // 药水
        ITEM_SYMBOLS.put(')', "weapon");       // 武器
        ITEM_SYMBOLS.put('(', "armor");        // 护甲
        ITEM_SYMBOLS.put('*', "misc");         // 杂项
        ITEM_SYMBOLS.put('%', "food");         // 食物
        ITEM_SYMBOLS.put('/', "wand");         // 法杖
        ITEM_SYMBOLS.put('{', "seed");         // 种子
        ITEM_SYMBOLS.put('[', "ring");         // 戒指
        ITEM_SYMBOLS.put('}', "stone");        // 石头
        ITEM_SYMBOLS.put('=', "artifact");     // 神器
    }
    
    /**
     * Get character representation for a terrain type
     */
    public static char getTerrain(int terrain) {
        Character c = TERRAIN_CHARS.get(terrain);
        return c != null ? c : '?';
    }
    
    /**
     * Get terrain type from character representation
     */
    public static int getTerrainFromChar(char c) {
        Integer terrain = CHAR_TO_TERRAIN.get(c);
        return terrain != null ? terrain : Terrain.EMPTY;
    }
    
    /**
     * Convert the map of a level to a string representation
     */
    public static String drawMap(Level level) {
        StringBuilder sb = new StringBuilder();
        
        int width = level.width();
        int height = level.height();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pos = x + y * width;
                int terrain = level.map[pos];
                
                // Check for actors (mobs, player) at this position
                Char ch = Actor.findChar(pos);
                
                // Check for items at this position
                Heap heap = level.heaps.get(pos);
                
                // Prioritize drawing mobs, then items, then terrain
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
                    sb.append(getTerrain(terrain));
                }
            }
            sb.append('\n');
        }
        
        return sb.toString();
    }
    
    /**
     * Draw just a small section of the map centered on a position
     */
    public static String drawMapSection(Level level, int centerPos, int radius) {
        StringBuilder sb = new StringBuilder();
        
        int width = level.width();
        int height = level.height();
        
        int centerX = centerPos % width;
        int centerY = centerPos / width;
        
        int startX = Math.max(0, centerX - radius);
        int startY = Math.max(0, centerY - radius);
        int endX = Math.min(width - 1, centerX + radius);
        int endY = Math.min(height - 1, centerY + radius);
        
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                int pos = x + y * width;
                int terrain = level.map[pos];
                
                // Check for actors at this position
                Char ch = Actor.findChar(pos);
                
                // Check for items at this position
                Heap heap = level.heaps.get(pos);
                
                // Prioritize drawing characters
                if (pos == centerPos) {
                    sb.append('X'); // Mark center position
                } else if (ch != null) {
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
                    sb.append(getTerrain(terrain));
                }
            }
            sb.append('\n');
        }
        
        return sb.toString();
    }
    
    /**
     * Generate a legend explaining the map symbols
     */
    public static String getLegend() {
        StringBuilder legend = new StringBuilder("Map Legend:\n");
        legend.append("@ - Hero\n");
        legend.append("M - Monster\n");
        legend.append("$ - Item\n");
        legend.append("X - Center point (in section view)\n");
        
        // Add terrain symbols
        for (Map.Entry<Integer, Character> entry : TERRAIN_CHARS.entrySet()) {
            int terrainType = entry.getKey();
            String terrainName = TERRAIN_NAMES.get(terrainType);
            if (terrainName != null) {
                legend.append(entry.getValue()).append(" - ").append(terrainName).append("\n");
            }
        }
        
        // 添加物品符号到图例中
        legend.append("\nItem Symbols:\n");
        for (Map.Entry<Character, String> entry : ITEM_SYMBOLS.entrySet()) {
            legend.append(entry.getKey()).append(" - ").append(entry.getValue()).append("\n");
        }
        
        return legend.toString();
    }
    
    /**
     * 从字符串创建地图
     * 
     * @param level 要修改的关卡
     * @param mapString 地图字符串，每行一行
     * @return 修改后的关卡，包含英雄起始位置
     */
    public static void createLevelFromString(Level level, String mapString) {
        String[] lines = mapString.split("\n");
        
        // 确定地图尺寸
        int height = lines.length;
        int width = 0;
        for (String line : lines) {
            width = Math.max(width, line.length());
        }
        
        // 设置关卡大小
        level.setSize(width, height);
        
        // 默认英雄起始位置（如果地图中未指定）
        int heroPos = -1;
        
        // 记录物品放置位置
        List<ItemPlacement> itemPlacements = new ArrayList<>();
        
        // 解析地图
        for (int y = 0; y < height; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                int pos = x + y * width;
                char c = line.charAt(x);
                
                // 检查是否是物品符号
                if (ITEM_SYMBOLS.containsKey(c)) {
                    // 记录物品放置位置和类型
                    itemPlacements.add(new ItemPlacement(pos, c));
                    // 物品下面是空地
                    level.map[pos] = Terrain.EMPTY;
                } else {
                    // 设置地形
                    level.map[pos] = getTerrainFromChar(c);
                }
                
                // 记录特殊位置
                if (c == '@') {
                    // 玩家位置
                    heroPos = pos;
                    // 确保这个位置是入口
                    level.map[pos] = Terrain.ENTRANCE;
                    
                    // 设置关卡入口
                    level.entrance = pos;
                } else if (c == '>') {
                    // 出口位置
                    level.map[pos] = Terrain.EXIT;
                    
                    // 设置关卡出口
                    level.exit = pos;
                }
            }
        }
        
        // 如果没找到英雄位置，使用默认位置
        if (heroPos == -1) {
            // 寻找一个空位作为入口
            for (int i = 0; i < level.length(); i++) {
                if (level.map[i] == Terrain.EMPTY) {
                    level.map[i] = Terrain.ENTRANCE;
                    level.entrance = i;
                    break;
                }
            }
        }
        
        // 放置物品
        for (ItemPlacement placement : itemPlacements) {
            placeItem(level, placement.position, placement.itemSymbol);
        }
        
        // 地图后处理，如更新视线、邻近关系等
        level.buildFlagMaps();
        level.cleanWalls();
    }
    
    /**
     * 在指定位置放置物品（私有方法，用于地图生成）
     */
    private static void placeItem(Level level, int pos, char itemSymbol) {
        Item item = createItemBySymbol(itemSymbol);
        if (item != null) {
            Heap heap = level.drop(item, pos);
            heap.type = getHeapTypeBySymbol(itemSymbol);
        }
    }
    
    /**
     * 根据物品符号创建对应的物品实例
     */
    private static Item createItemBySymbol(char symbol) {
        switch (symbol) {
            case '$': 
                return new Gold(Random.IntRange(50, 100));
            case '?': 
                return Random.Int(2) == 0 ? new ScrollOfIdentify() : new ScrollOfUpgrade();
            case '!': 
                return Random.Int(2) == 0 ? new PotionOfHealing() : new PotionOfStrength();
            case ')': 
                return Random.Int(2) == 0 ? new WornShortsword() : new Sword();
            case '(': 
                return Random.Int(2) == 0 ? new ClothArmor() : new LeatherArmor();
            case '%': 
                return new Food();
            case '*': 
                return new Gold(Random.IntRange(1, 10)); // 替代Dart
            default:
                return new Gold(25);
        }
    }
    
    /**
     * 根据物品符号确定堆类型
     */
    private static Heap.Type getHeapTypeBySymbol(char symbol) {
        switch (symbol) {
            case '$': 
                return Heap.Type.HEAP;
            case '?': 
                return Heap.Type.HEAP;
            case '!': 
                return Heap.Type.HEAP;
            case ')': 
                return Heap.Type.HEAP;
            case '(': 
                return Heap.Type.HEAP;
            case '%': 
                return Heap.Type.HEAP;
            case '*': 
                return Heap.Type.HEAP;
            default:
                return Heap.Type.HEAP;
        }
    }
    
    /**
     * 辅助类，用于记录物品放置位置
     */
    private static class ItemPlacement {
        int position;
        char itemSymbol;
        
        ItemPlacement(int position, char itemSymbol) {
            this.position = position;
            this.itemSymbol = itemSymbol;
        }
    }
    
    //--------- 公共API，用于在关卡中动态添加物品 ---------//
    
    /**
     * 在指定位置放置物品（公开API，用于动态添加物品）
     * 
     * @param level 要放置物品的关卡
     * @param pos 物品放置位置
     * @param itemType 物品类型字符（参考ITEM_SYMBOLS中的定义）
     * @return 放置的物品对象，如果放置失败则返回null
     */
    public static Item addItem(Level level, int pos, char itemType) {
        // 检查位置是否有效
        if (pos < 0 || pos >= level.length()) {
            return null;
        }
        
        // 检查是否是可通行地形
        if (!level.passable[pos]) {
            return null;
        }
        
        // 创建物品
        Item item = createItemBySymbol(itemType);
        if (item != null) {
            // 放置物品
            Heap heap = level.drop(item, pos);
            heap.type = getHeapTypeBySymbol(itemType);
            return item;
        }
        
        return null;
    }
    
    /**
     * 在指定坐标放置物品
     * 
     * @param level 要放置物品的关卡
     * @param x x坐标
     * @param y y坐标
     * @param itemType 物品类型字符
     * @return 放置的物品对象，如果放置失败则返回null
     */
    public static Item addItem(Level level, int x, int y, char itemType) {
        int pos = x + y * level.width();
        return addItem(level, pos, itemType);
    }
    
    /**
     * 在区域内随机位置放置物品
     * 
     * @param level 要放置物品的关卡
     * @param startX 区域起始X坐标
     * @param startY 区域起始Y坐标
     * @param endX 区域结束X坐标
     * @param endY 区域结束Y坐标
     * @param itemType 物品类型字符
     * @return 放置的物品对象，如果放置失败则返回null
     */
    public static Item addRandomItem(Level level, int startX, int startY, int endX, int endY, char itemType) {
        // 确保区域范围有效
        startX = Math.max(0, startX);
        startY = Math.max(0, startY);
        endX = Math.min(level.width() - 1, endX);
        endY = Math.min(level.height() - 1, endY);
        
        // 尝试在区域内随机位置放置物品
        for (int attempts = 0; attempts < 20; attempts++) {
            int x = Random.IntRange(startX, endX);
            int y = Random.IntRange(startY, endY);
            int pos = x + y * level.width();
            
            // 检查位置是否可用
            if (level.passable[pos] && Actor.findChar(pos) == null) {
                return addItem(level, pos, itemType);
            }
        }
        
        return null; // 没有找到合适的位置
    }
    
    /**
     * 在整个关卡的随机位置放置物品
     * 
     * @param level 要放置物品的关卡
     * @param itemType 物品类型字符
     * @return 放置的物品对象，如果放置失败则返回null
     */
    public static Item addRandomItem(Level level, char itemType) {
        return addRandomItem(level, 0, 0, level.width() - 1, level.height() - 1, itemType);
    }
    
    /**
     * 批量添加物品
     * 
     * @param level 要放置物品的关卡
     * @param itemPositionMap 物品位置和类型的映射，格式：Map<Integer, Character>，key是位置，value是物品类型
     * @return 成功放置的物品数量
     */
    public static int addItems(Level level, Map<Integer, Character> itemPositionMap) {
        int successCount = 0;
        
        for (Map.Entry<Integer, Character> entry : itemPositionMap.entrySet()) {
            int pos = entry.getKey();
            char itemType = entry.getValue();
            
            if (addItem(level, pos, itemType) != null) {
                successCount++;
            }
        }
        
        return successCount;
    }
    
    /**
     * 随机散布多种物品
     * 
     * @param level 要放置物品的关卡
     * @param itemCount 各类物品的数量映射，格式：Map<Character, Integer>，key是物品类型，value是数量
     * @return 成功放置的物品数量
     */
    public static int scatterItems(Level level, Map<Character, Integer> itemCount) {
        int successCount = 0;
        
        for (Map.Entry<Character, Integer> entry : itemCount.entrySet()) {
            char itemType = entry.getKey();
            int count = entry.getValue();
            
            for (int i = 0; i < count; i++) {
                if (addRandomItem(level, itemType) != null) {
                    successCount++;
                }
            }
        }
        
        return successCount;
    }
} 