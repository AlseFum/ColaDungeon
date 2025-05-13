package com.cola;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DebugLevel extends Level {

    private static final int SIZE = 10;
    private boolean isCustomMapLoaded = false;

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
        // 优先使用自定义地图（如果有）
        if (buildCustomMap()) {
            return true;
        }

        // 否则使用默认地图
        setSize(17, 17);

        for (int i = 2; i < SIZE; i++) {
            for (int j = 2; j < SIZE; j++) {
                map[i * width() + j] = Terrain.EMPTY;
            }
        }

        for (int i = 1; i <= SIZE; i++) {
            map[width() + i] = map[width() * SIZE + i] = map[width() * i + 1] = map[width() * i + SIZE] = Terrain.WATER;
        }

        int entrance = SIZE * width() + SIZE / 2 + 1;

        // different exit behaviour depending on main branch or side one
        if (Dungeon.branch == 0) {
            transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
        } else {
            transitions.add(new LevelTransition(this,
                    entrance,
                    LevelTransition.Type.BRANCH_ENTRANCE,
                    Dungeon.depth,
                    0,
                    LevelTransition.Type.BRANCH_EXIT));
        }
        map[entrance] = Terrain.ENTRANCE;

        return true;
    }

    /**
     * 从字符串构建自定义地图
     * 
     * @return 是否成功构建地图
     */
    private boolean buildCustomMap() {
        // 这里可以放置你想要的地图字符串
        String customMap = "###############\n" +
                "#.............#\n" +
                "#.###.#####.#.#\n" +
                "#.#.....#...#.#\n" +
                "#.#.###.#.###.#\n" +
                "#.#...#.#.....#\n" +
                "#.###.#.#####.#\n" +
                "#.....#.......#\n" +
                "#.#########.#.#\n" +
                "#.#.........#.#\n" +
                "#.#.#######.#.#\n" +
                "#.#.#.@.?#.#.#\n" +
                "#.#.#.###.#.#.#\n" +
                "#...#...#...#>#\n" +
                "###############";

        // 使用字符串创建地图
        try {
            MapDrawer.createLevelFromString(this, customMap);
            isCustomMapLoaded = true;

            // 设置过渡点
            transitions.clear();
            if (Dungeon.branch == 0) {
                transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
            } else {
                transitions.add(new LevelTransition(this,
                        entrance,
                        LevelTransition.Type.BRANCH_ENTRANCE,
                        Dungeon.depth,
                        0,
                        LevelTransition.Type.BRANCH_EXIT));
            }

            transitions.add(new LevelTransition(this, exit, LevelTransition.Type.REGULAR_EXIT));

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从给定的字符串创建地图
     * 
     * @param mapString 地图字符串
     * @return 是否成功构建地图
     */
    public boolean buildMapFromString(String mapString) {
        try {
            // 重设关卡
            if (transitions != null) {
                transitions.clear();
            }

            // 使用字符串创建地图
            MapDrawer.createLevelFromString(this, mapString);
            isCustomMapLoaded = true;

            // 设置过渡点
            if (transitions != null) {
                if (Dungeon.branch == 0) {
                    transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
                } else {
                    transitions.add(new LevelTransition(this,
                            entrance,
                            LevelTransition.Type.BRANCH_ENTRANCE,
                            Dungeon.depth,
                            0,
                            LevelTransition.Type.BRANCH_EXIT));
                }

                // 如果有出口，添加出口过渡点
                if (exit > 0) {
                    transitions.add(new LevelTransition(this, exit, LevelTransition.Type.REGULAR_EXIT));
                } else {
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建一个包含物品的示例地图
     */
    public boolean buildItemTestMap() {
        String itemMapString = "#######################\n" +
                "#@....................#\n" +
                "#...................>.#\n" +
                "#.....###############.#\n" +
                "#.....#$$$$$$$$$$$$$#.#\n" +
                "#.....#$...........%#.#\n" +
                "#.....#$..????????..#.#\n" +
                "#.....#$...........*#.#\n" +
                "#.....#$..!!!!!!!...#.#\n" +
                "#.....#$...........&#.#\n" +
                "#.....#$..)()()()...#.#\n" +
                "#.....#$...........+#.#\n" +
                "#.....#$$$$$$$$$$$$$#.#\n" +
                "#.....###############.#\n" +
                "#.......................#\n" +
                "#######################";

        return buildMapFromString(itemMapString);
    }

    /**
     * 在关卡创建完成后添加物品
     * 可以在地图生成后的任何时候调用来添加额外的物品
     * 
     * @param itemType 物品类型字符，如'$'表示金币，'?'表示卷轴等
     * @param count    要添加的物品数量
     * @return 成功放置的物品数量
     */
    public int addItemsAfterCreation(char itemType, int count) {
        int successCount = 0;

        for (int i = 0; i < count; i++) {
            if (MapDrawer.addRandomItem(this, itemType) != null) {
                successCount++;
            }
        }

        return successCount;
    }

    /**
     * 在指定坐标添加物品
     * 
     * @param x        X坐标
     * @param y        Y坐标
     * @param itemType 物品类型字符
     * @return 添加的物品，如果添加失败则为null
     */
    public Item addItemAt(int x, int y, char itemType) {
        return MapDrawer.addItem(this, x, y, itemType);
    }

    /**
     * 在整个关卡散布多种物品
     * 
     * @param itemCounts 物品类型和数量的映射
     * @return 成功放置的物品数量
     */
    public int scatterMultipleItems(Map<Character, Integer> itemCounts) {
        return MapDrawer.scatterItems(this, itemCounts);
    }

    /**
     * 在指定形状区域内添加物品
     * 例如，可以在一个矩形房间或圆形区域内添加特定类型的物品
     * 
     * @param centerX  区域中心X坐标
     * @param centerY  区域中心Y坐标
     * @param radius   区域半径
     * @param itemType 物品类型
     * @param count    物品数量
     * @return 成功放置的物品数量
     */
    public int addItemsInArea(int centerX, int centerY, int radius, char itemType, int count) {
        int successCount = 0;

        // 计算区域范围
        int startX = centerX - radius;
        int startY = centerY - radius;
        int endX = centerX + radius;
        int endY = centerY + radius;

        for (int i = 0; i < count; i++) {
            if (MapDrawer.addRandomItem(this, startX, startY, endX, endY, itemType) != null) {
                successCount++;
            }
        }

        return successCount;
    }

    /**
     * 创建物品演示地图，同时通过代码动态添加各种物品
     */
    public boolean buildItemDemoMap() {
        // 先创建基本地图
        String baseMapString = "###############\n" +
                "#@............#\n" +
                "#.............#\n" +
                "#.............#\n" +
                "#.............#\n" +
                "#.............#\n" +
                "#.............#\n" +
                "#.............#\n" +
                "#.............#\n" +
                "#............>#\n" +
                "###############";

        // 构建基础地图
        boolean success = buildMapFromString(baseMapString);
        if (!success)
            return false;

        // 动态添加物品
        // 1. 在固定位置添加物品
        addItemAt(3, 3, '$'); // 在(3,3)位置放置金币
        addItemAt(4, 3, '?'); // 在(4,3)位置放置卷轴
        addItemAt(5, 3, '!'); // 在(5,3)位置放置药水

        // 2. 随机放置多个同类物品
        addItemsAfterCreation(')', 3); // 随机放置3件武器

        // 3. 在特定区域添加物品
        addItemsInArea(7, 7, 2, '%', 5); // 在(7,7)周围半径2的区域内放置5个食物

        // 4. 使用Map批量散布不同类型的物品
        Map<Character, Integer> itemCounts = new HashMap<>();
        itemCounts.put('$', 10); // 10个金币
        itemCounts.put('?', 5); // 5个卷轴
        itemCounts.put('!', 3); // 3个药水
        scatterMultipleItems(itemCounts);

        return true;
    }

    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
    }

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

    /**
     * Prints the map of this level to the console for debugging
     */
    public String debugPrintMap() {
        return MapDrawer.drawMap(this);
    }

    /**
     * Prints a section of the map centered around the entrance
     */
    public String debugPrintMapAroundEntrance(int radius) {
        return MapDrawer.drawMapSection(this, entrance(), radius);
    }

    /**
     * Prints the map legend
     */
    public String getMapLegend() {
        return MapDrawer.getLegend();
    }
}
