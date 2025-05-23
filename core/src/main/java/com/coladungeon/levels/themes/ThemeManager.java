package com.coladungeon.levels.themes;

import com.coladungeon.Dungeon;
import com.coladungeon.levels.Level;
import com.coladungeon.levels.DeadEndLevel;
import com.coladungeon.levels.LastLevel;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.List;

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
        System.out.println("[ThemeManager] 创建楼层: depth=" + depth + ", branch=" + branch);
        
        // 第一层：随机选择主题分支，创建特殊房间
        if (depth == 1 && branch == 0) {
            String[] themes = {"crystal_temple", "shadow_forest"};
            String selectedTheme = Random.element(themes);
            System.out.println("[ThemeManager] 第一层随机选择主题: " + selectedTheme);
            
            setCurrentTheme(selectedTheme);
            ThemePack themePack = getCurrentTheme();
            if (themePack != null) {
                Level level = themePack.getNormalLevel();
                System.out.println("[ThemeManager] 成功创建第一层: " + level.getClass().getSimpleName());
                return level;
            }
            return createStandardLevel(1);
        }
        
        // 分支楼层：直接使用对应的主题楼层
        if (branch == 16) {
            // 水晶神殿分支
            if (depth == 2) {
                return new CrystalTempleThemePack.CrystalTempleBranchLevel();
            } else {
                setCurrentTheme("crystal_temple");
                ThemePack themePack = getCurrentTheme();
                if (themePack != null) {
                    boolean isBoss = Dungeon.bossLevel(depth);
                    return isBoss ? themePack.getBossLevel() : themePack.getNormalLevel();
                }
            }
        }
        
        if (branch == 17) {
            // 暗影森林分支
            if (depth == 2) {
                return new ShadowForestThemePack.ShadowForestBranchLevel();
            } else {
                setCurrentTheme("shadow_forest");
                ThemePack themePack = getCurrentTheme();
                if (themePack != null) {
                    boolean isBoss = Dungeon.bossLevel(depth);
                    return isBoss ? themePack.getBossLevel() : themePack.getNormalLevel();
                }
            }
        }
        
        // 主线分支：使用标准楼层
        return createStandardLevel(depth);
    }
    
    /**
     * 创建标准楼层（优化后的逻辑）
     */
    private static Level createStandardLevel(int depth) {
        // 特殊情况
        if (depth == 0) return new DeadEndLevel();
        if (depth == 26) return new LastLevel();
        if (depth > 26) return new DeadEndLevel();
        
        // 判断是否为boss层
        boolean isBoss = (depth % 5 == 0) && (depth <= 25);
        
        // 根据深度选择对应的主题包
        ThemePack themePack;
        if (depth <= 5) {
            themePack = ThemeSheet.SewerTheme;
        } else if (depth <= 10) {
            themePack = ThemeSheet.PrisonTheme;
        } else if (depth <= 15) {
            themePack = ThemeSheet.CavesTheme;
        } else if (depth <= 20) {
            themePack = ThemeSheet.CityTheme;
        } else if (depth <= 25) {
            themePack = ThemeSheet.HallsTheme;
        } else {
            return new DeadEndLevel();
        }
        
        // 使用主题包创建楼层
        return isBoss ? themePack.getBossLevel() : themePack.getNormalLevel();
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
        ThemePack theme = ThemeSheet.getThemePack(currentTheme);
        return theme != null ? theme : ThemeSheet.SewerTheme; // Default to Sewer theme
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