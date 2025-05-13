package com.cola;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;

import com.shatteredpixel.shatteredpixeldungeon.levels.DeadEndLevel;

import com.shatteredpixel.shatteredpixeldungeon.levels.LastLevel;


import com.watabou.utils.RectF;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;

import java.util.HashMap;
import java.util.Map;

//this file is to help make the dungeon code more soft.
public class Helper {
    public static Level newLevel(int depth) {
        Level level;
        ThemePack theme = ThemePack.getTheme(depth);
        if (theme == null) {
            level = new DeadEndLevel();
        } else {
            try {

                if (Dungeon.bossLevel()) {
                    level = theme.BossLevel.getDeclaredConstructor().newInstance();
                } else {
                    level = theme.normalLevel.getDeclaredConstructor().newInstance();
                }
            } catch (Exception e) {
                level = new DeadEndLevel();
            }
        }

        return level;
    }

    public static Level newLevel(int depth, int branch) {
        if (depth >= 26 && depth <= 30)
            return new LastLevel();
        else if (depth >= 0)
            return newLevel(depth);
        else
            return new DeadEndLevel();

    }

    public static class DirectImageMap {
        public String path;
        public RectF rect;
        public float height;

        public DirectImageMap(String path, RectF rect, float height) {
            this.path = path;
            this.rect = rect;
            this.height = height;
        }
    }

    public static DirectImageMap map_image(int image) {
        if (image < 114514) {
            return new DirectImageMap(
                    Assets.Sprites.ITEMS,
                    ItemSpriteSheet.film.get(image),
                    ItemSpriteSheet.film.height(image));
        } else if (image == 114515) {
            return new DirectImageMap("minecraft/bread.png", new RectF(0.25f, 0, 0.75f, 1), 16);
        } else {
            // rectF是按百分比代表texture的位置
            return new DirectImageMap("minecraft/golden_apple.png", new RectF(0.25f, 0, 0.75f, 1), 16);
        }
    };

    /**
     * 从字符串创建自定义地图关卡
     * 
     * @param mapString 表示地图的字符串，每行一行
     * @return 创建好的关卡对象
     */
    public static Level createCustomLevel(String mapString) {
        // 创建一个DebugLevel作为自定义关卡的容器
        DebugLevel level = new DebugLevel();

        // 使用字符串构建地图
        level.buildMapFromString(mapString);

        return level;
    }

    /**
     * 使用预设模板创建关卡
     * 
     * @param template 地图模板名称
     * @return 创建好的关卡对象
     */
    public static Level createLevelFromTemplate(String template) {
        String mapString;

        // 根据模板名称选择相应的地图字符串
        switch (template.toLowerCase()) {
            case "maze":
                mapString = "###############\n" +
                        "#@............#\n" +
                        "#.#######.###.#\n" +
                        "#.#.....#.#...#\n" +
                        "#.#.###.#.#.#.#\n" +
                        "#...#...#.#.#.#\n" +
                        "#####.###.#.#.#\n" +
                        "#.....#...#.#.#\n" +
                        "#.#####.###.#.#\n" +
                        "#.#.......#.#.#\n" +
                        "#.#.#######.#.#\n" +
                        "#...#.......#.#\n" +
                        "###.#.#######.#\n" +
                        "#>..#.........#\n" +
                        "###############";
                break;

            case "arena":
                mapString = "###############\n" +
                        "###.........###\n" +
                        "##...........##\n" +
                        "#.............#\n" +
                        "#......@......#\n" +
                        "#.............#\n" +
                        "#.............#\n" +
                        "#.............#\n" +
                        "#.............#\n" +
                        "#.............#\n" +
                        "#.............#\n" +
                        "#......>......#\n" +
                        "#.............#\n" +
                        "###.........###\n" +
                        "###############";
                break;

            case "cave":
                mapString = "###############\n" +
                        "##....#....####\n" +
                        "#..#........###\n" +
                        "#.....##.....##\n" +
                        "##..####.....##\n" +
                        "###.####......#\n" +
                        "###..###..#...#\n" +
                        "##...##...##..#\n" +
                        "#....#@...###.#\n" +
                        "#............##\n" +
                        "#.............#\n" +
                        "##......#.....#\n" +
                        "###....###...>#\n" +
                        "####...#######\n" +
                        "###############";
                break;

            case "dungeon":
                mapString = "#################\n" +
                        "#...#...#...#...#\n" +
                        "#.#.#.#.#.#.#.#.#\n" +
                        "#.#...#...#...#.#\n" +
                        "#.#####.#####.#.#\n" +
                        "#.#...#.#...#.#.#\n" +
                        "#.#.#.#.#.#.#.#.#\n" +
                        "#...#...#.#...#.#\n" +
                        "##########.#####\n" +
                        "#@..............#\n" +
                        "###.#.#.#.#.#.#.#\n" +
                        "#...#...#...#...#\n" +
                        "#.###########.###\n" +
                        "#.......#.......#\n" +
                        "#.#####.#.#####.#\n" +
                        "#.#.....#.....#.#\n" +
                        "#.#.###.#.###.#.#\n" +
                        "#...#...#...#...#\n" +
                        "#####.#####.#####\n" +
                        "#.....#...#.....#\n" +
                        "#.#####.#.#####.#\n" +
                        "#.#.....#.....#.#\n" +
                        "#.#.###.#.###.#.#\n" +
                        "#...#.....#...#.#\n" +
                        "#####.#####.#####\n" +
                        "#.....#...#.....#\n" +
                        "#.#####.#.#####.#\n" +
                        "#.#.....#.....#.#\n" +
                        "#.#.###.#.###.#.#\n" +
                        "#...#.....#>..#.#\n" +
                        "#################";
                break;

            case "itemtest":
                // 创建一个包含各种物品的测试地图
                DebugLevel itemLevel = new DebugLevel();
                itemLevel.buildItemTestMap();
                return itemLevel;

            case "itemdemo":
                // 创建一个演示动态添加物品的地图
                DebugLevel demoLevel = new DebugLevel();
                demoLevel.buildItemDemoMap();
                return demoLevel;

            default:
                // 简单的默认地图
                mapString = "#########\n" +
                        "#@......#\n" +
                        "#.......#\n" +
                        "#...#...#\n" +
                        "#...#...#\n" +
                        "#.......#\n" +
                        "#......>#\n" +
                        "#########";
        }

        return createCustomLevel(mapString);
    }

    /**
     * 创建一个包含各种物品的测试关卡
     * 
     * @return 创建好的关卡对象，其中包含了各种物品
     */
    public static Level createItemTestLevel() {
        // 使用预设的物品测试地图
        DebugLevel level = new DebugLevel();
        level.buildItemTestMap();
        return level;
    }

    /**
     * 创建自定义的物品测试关卡
     * 
     * @param mapWithItems 带有物品符号的地图字符串
     * @return 创建好的关卡对象，其中包含了自定义摆放的物品
     */
    public static Level createCustomItemLevel(String mapWithItems) {
        DebugLevel level = new DebugLevel();
        level.buildMapFromString(mapWithItems);
        return level;
    }

    /**
     * 创建一个动态物品演示关卡
     * 这个关卡开始时只有基本地形，然后通过代码动态添加各种物品
     * 
     * @return 创建好的关卡对象
     */
    public static Level createItemDemoLevel() {
        DebugLevel level = new DebugLevel();
        level.buildItemDemoMap();
        return level;
    }

    /**
     * 在现有关卡的特定位置添加物品
     * 
     * @param level    要添加物品的关卡
     * @param x        x坐标
     * @param y        y坐标
     * @param itemType 物品类型字符
     * @return 添加的物品，如果添加失败则为null
     */
    public static Item addItemAt(Level level, int x, int y, char itemType) {
        return MapDrawer.addItem(level, x, y, itemType);
    }

    /**
     * 在现有关卡中随机散布物品
     * 
     * @param level    要添加物品的关卡
     * @param itemType 物品类型字符
     * @param count    要添加的数量
     * @return 成功添加的物品数量
     */
    public static int scatterItems(Level level, char itemType, int count) {
        int successCount = 0;

        for (int i = 0; i < count; i++) {
            if (MapDrawer.addRandomItem(level, itemType) != null) {
                successCount++;
            }
        }

        return successCount;
    }

    /**
     * 在现有关卡中散布多种类型的物品
     * 
     * @param level      要添加物品的关卡
     * @param itemCounts 物品类型和数量的映射表
     * @return 成功添加的物品数量
     */
    public static int scatterMultipleItems(Level level, Map<Character, Integer> itemCounts) {
        return MapDrawer.scatterItems(level, itemCounts);
    }

    /**
     * 创建一个包含所有物品类型的示例地图，并动态添加额外物品
     * 这个方法是各种物品添加功能的综合演示
     * 
     * @return 包含完整物品示例的关卡
     */
    public static Level createCompleteItemShowcase() {
        // 1. 创建基础地图
        DebugLevel level = new DebugLevel();
        level.buildItemDemoMap();

        // 2. 在固定位置添加特定物品
        for (int i = 0; i < 5; i++) {
            addItemAt(level, 10, 3 + i, (char) ('!' + i)); // 使用不同的物品类型
        }

        // 3. 添加随机物品
        Map<Character, Integer> extraItems = new HashMap<>();
        extraItems.put('$', 20); // 20个金币
        extraItems.put('?', 10); // 10个卷轴
        extraItems.put('!', 8); // 8个药水
        extraItems.put(')', 5); // 5个武器
        extraItems.put('(', 5); // 5个护甲
        scatterMultipleItems(level, extraItems);

        return level;
    }
}
