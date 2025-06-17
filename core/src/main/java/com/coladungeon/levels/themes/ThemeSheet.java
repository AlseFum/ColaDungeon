package com.coladungeon.levels.themes;

import com.coladungeon.levels.themes.sewer.SewerLevel;
import com.coladungeon.levels.themes.sewer.SewerBossLevel;
import com.coladungeon.levels.themes.caves.CavesBossLevel;
import com.coladungeon.levels.themes.caves.CavesLevel;
import com.coladungeon.levels.themes.city.CityBossLevel;
import com.coladungeon.levels.themes.city.CityLevel;
import com.coladungeon.levels.themes.halls.HallsBossLevel;
import com.coladungeon.levels.themes.halls.HallsLevel;
import com.coladungeon.levels.themes.prison.PrisonBossLevel;
import com.coladungeon.levels.themes.prison.PrisonLevel;
import com.coladungeon.levels.DebugLevel;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ThemeSheet is a static utility class that stores all available ThemePack objects.
 * It provides centralized access to themes for the ThemeManager to use.
 */
public class ThemeSheet {
    
    // Map of theme name to ThemePack
    public static Map<String, ThemePack> themePacks = new HashMap<>();
    
    // Static theme pack instances - centralized here
    public static ThemePack SewerTheme;
    public static ThemePack CavesTheme;
    public static ThemePack CityTheme;
    public static ThemePack HallsTheme;
    public static ThemePack PrisonTheme;
    public static ThemePack DebugTheme;
    // public static ThemePack CrystalTempleTheme;
    // public static ThemePack ShadowForestTheme;
    
    static {
        // Initialize theme packs with appropriate weight functions
        SewerTheme = new ThemePack(SewerLevel.class, SewerBossLevel.class, 
            (depth, branch) -> (branch == 0 && depth >= 1 && depth <= 5) ? (short) 1 : (short) 0);
        
        PrisonTheme = new ThemePack(PrisonLevel.class, PrisonBossLevel.class,
            (depth, branch) -> (branch == 0 && depth >= 6 && depth <= 10) ? (short) 1 : (short) 0);
        
        CavesTheme = new ThemePack(CavesLevel.class, CavesBossLevel.class,
            (depth, branch) -> (branch == 0 && depth >= 11 && depth <= 15) ? (short) 1 : (short) 0);
        
        CityTheme = new ThemePack(CityLevel.class, CityBossLevel.class,
            (depth, branch) -> (branch == 0 && depth >= 16 && depth <= 20) ? (short) 1 : (short) 0);
        
        HallsTheme = new ThemePack(HallsLevel.class, HallsBossLevel.class,
            (depth, branch) -> (branch == 0 && depth >= 21 && depth <= 25) ? (short) 1 : (short) 0);
        
        DebugTheme = new ThemePack(DebugLevel.class, DebugLevel.class);
        
        // Initialize with default themes
        registerDefaultThemes();
    }
    
    // Private constructor to prevent instantiation
    private ThemeSheet() {
        throw new AssertionError("ThemeSheet is a utility class and should not be instantiated");
    }
    
    /**
     * Register built-in default themes
     */
    private static void registerDefaultThemes() {
        registerThemePack("sewer", SewerTheme);
        registerThemePack("prison", PrisonTheme);
        registerThemePack("caves", CavesTheme);
        registerThemePack("city", CityTheme);
        registerThemePack("halls", HallsTheme);
        
        // // 注册新的特殊主题包
        // registerThemePack("crystal_temple", new ThemePack(CrystalTempleLevel.class, CrystalTempleLevel.class, (depth, branch) -> (branch == 0 && depth >= 1 && depth <= 5) ? (short) 1 : (short) 0));
        // registerThemePack("shadow_forest", new ThemePack(ShadowForestLevel.class, ShadowForestLevel.class));
    }
    
    /**
     * Register a new theme pack
     * @param name The name of the theme
     * @param themePack The ThemePack object
     */
    public static void registerThemePack(String name, ThemePack themePack) {
        themePacks.put(name, themePack);
    }
    

    public static ThemePack getThemePack(String name) {
        return themePacks.get(name);
    }
    

    public static List<String> getThemeNames() {
        return new ArrayList<>(themePacks.keySet());
    }

    public static boolean hasTheme(String name) {
        return themePacks.containsKey(name);
    }
} 