package com.coladungeon.levels.themes;

import java.util.ArrayList;
import java.util.List;

import com.coladungeon.Dungeon;
import com.coladungeon.levels.DeadEndLevel;
import com.coladungeon.levels.DebugLevel;
import com.coladungeon.levels.LastLevel;
import com.coladungeon.levels.Level;
import com.watabou.utils.Random;

public class ThemeManager {

    private static String currentTheme = "default";

    // Private constructor to prevent instantiation
    private ThemeManager() {
        throw new AssertionError("ThemeManager is a utility class and should not be instantiated");
    }

    /**
     * 为指定深度创建楼层，默认使用主线分支（branch = 0）
     */
    public static Level createLevel(int depth) {
        return createLevel(depth, 0);
    }

    /**
     * 为指定深度和分支创建楼层 - 核心公共接口
     */
    public static Level createLevel(int depth, int branch) {
        if (depth < -5) {
            return new DeadEndLevel();
        }
        if (depth > -5 && depth < 0) {
            return new DebugLevel();
        }
        if (depth >= 26) {
            return new LastLevel();
        }
        // 一次遍历找到最高权重并收集对应主题
        short maxWeight = 0;
        List<String> topThemes = new ArrayList<>();

        for (var entry : ThemeSheet.themePacks.entrySet()) {
            String themeName = entry.getKey();
            ThemePack themePack = entry.getValue();
            short weight = themePack.getWeight(depth, branch);
            if (weight > 0) {
                if (weight > maxWeight) {
                    // 发现更高权重，清空列表并添加新主题
                    maxWeight = weight;
                    topThemes.clear();
                    topThemes.add(themeName);
                } else if (weight == maxWeight) {
                    // 相同权重，添加到列表
                    topThemes.add(themeName);
                }
            }
        }

        if (topThemes.isEmpty()) {
            // 没有可用主题，返回死胡同
            return new DeadEndLevel();
        }

        // 从最高权重主题中随机选择
        String selectedTheme = Random.element(topThemes);
        // System.out.println("[ThemeManager] select Theme: " + selectedTheme + " (by weight: " + maxWeight + ")");

        setCurrentTheme(selectedTheme);
        ThemePack themePack = getCurrentTheme();
        if (themePack == null) {
            return new DeadEndLevel();
        }
        boolean isBoss = ThemeManager.bossLevel(depth);
        Level level = isBoss
                ? themePack.getBossLevel()
                : themePack.getNormalLevel();
        // System.out.println("[ThemeManager] success create level: " + level.getClass().getSimpleName());
        return level;
    }
    public static boolean bossLevel(int depth){
        return depth == 5 || depth == 10 || depth == 15 || depth == 20 || depth == 25;
    }
    public static boolean shopOnLevel(){
        return shopOnLevel(Dungeon.depth);
    }
    public static boolean shopOnLevel(int depth){
         return depth == 6 || depth == 11 || depth == 16;
    }
    public static void registerTheme(String themeName, ThemePack themePack) {
        // Delegate to ThemeSheet
        ThemeSheet.registerThemePack(themeName, themePack);
    }

    public static void setCurrentTheme(String themeName) {
        if (ThemeSheet.hasTheme(themeName)) {
            currentTheme = themeName;
        }
    }

    public static ThemePack getCurrentTheme() {
        // Default to Sewer theme
        ThemePack theme = ThemeSheet.getThemePack(currentTheme);
        return theme != null ? theme : ThemeSheet.SewerTheme; 
    }

    public static List<String> getAvailableThemeNames() {
        return ThemeSheet.getThemeNames();
    }

    // Randomly select a theme from the available ones
    public static void selectRandomTheme() {
        List<String> themes = getAvailableThemeNames();
        if (!themes.isEmpty()) {
            String randomTheme = Random.element(themes);
            setCurrentTheme(randomTheme);
        }
    }
}
