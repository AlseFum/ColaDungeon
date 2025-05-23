package com.coladungeon.levels.themes;

import com.coladungeon.levels.SewerLevel;
import com.coladungeon.levels.SewerBossLevel;
import com.coladungeon.levels.CavesBossLevel;
import com.coladungeon.levels.CavesLevel;
import com.coladungeon.levels.CityBossLevel;
import com.coladungeon.levels.CityLevel;
import com.coladungeon.levels.HallsBossLevel;
import com.coladungeon.levels.HallsLevel;
import com.coladungeon.levels.PrisonBossLevel;
import com.coladungeon.levels.PrisonLevel;
import com.coladungeon.levels.DebugLevel;
import com.coladungeon.levels.themes.CrystalTempleThemePack.CrystalTempleLevel;
import com.coladungeon.levels.themes.ShadowForestThemePack.ShadowForestLevel;

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
    private static Map<String, ThemePack> themePacks = new HashMap<>();
    
    // Static theme pack instances - centralized here
    public static ThemePack SewerTheme;
    public static ThemePack CavesTheme;
    public static ThemePack CityTheme;
    public static ThemePack HallsTheme;
    public static ThemePack PrisonTheme;
    public static ThemePack DebugTheme;
    public static ThemePack CrystalTempleTheme;
    public static ThemePack ShadowForestTheme;
    
    static {
        // Initialize all theme packs
        SewerTheme = new ThemePack(SewerLevel.class, SewerBossLevel.class);
        CavesTheme = new ThemePack(CavesLevel.class, CavesBossLevel.class);
        CityTheme = new ThemePack(CityLevel.class, CityBossLevel.class);
        HallsTheme = new ThemePack(HallsLevel.class, HallsBossLevel.class);
        PrisonTheme = new ThemePack(PrisonLevel.class, PrisonBossLevel.class);
        DebugTheme = new ThemePack(DebugLevel.class, DebugLevel.class);
        
        // Special theme packs
        CrystalTempleTheme = new ThemePack(CrystalTempleLevel.class, CrystalTempleLevel.class);
        ShadowForestTheme = new ThemePack(ShadowForestLevel.class, ShadowForestLevel.class);
        
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
        // Default themes are registered here
        // These correspond to the static themes in ThemePack
        registerThemePack("sewer", SewerTheme);
        registerThemePack("prison", PrisonTheme);
        registerThemePack("caves", CavesTheme);
        registerThemePack("city", CityTheme);
        registerThemePack("halls", HallsTheme);
        
        // 注册新的特殊主题包
        registerThemePack("crystal_temple", CrystalTempleTheme);
        registerThemePack("shadow_forest", ShadowForestTheme);
    }
    
    /**
     * Register a new theme pack
     * @param name The name of the theme
     * @param themePack The ThemePack object
     */
    public static void registerThemePack(String name, ThemePack themePack) {
        themePacks.put(name, themePack);
    }
    
    /**
     * Get a theme pack by name
     * @param name The name of the theme
     * @return The ThemePack, or null if not found
     */
    public static ThemePack getThemePack(String name) {
        return themePacks.get(name);
    }
    
    /**
     * Get all available theme names
     * @return List of theme names
     */
    public static List<String> getThemeNames() {
        return new ArrayList<>(themePacks.keySet());
    }
    
    // /**
    //  * Get all available theme packs
    //  * @return Map of theme names to theme packs
    //  */
    // public static Map<String, ThemePack> getAllThemePacks() {
    //     return new HashMap<>(themePacks);
    // }
    
    /**
     * Check if a theme exists
     * @param name The name of the theme
     * @return true if the theme exists, false otherwise
     */
    public static boolean hasTheme(String name) {
        return themePacks.containsKey(name);
    }
} 