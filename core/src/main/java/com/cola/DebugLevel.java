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
}
