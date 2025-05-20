package com.coladungeon.levels.themes;

import com.coladungeon.levels.Level;
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
import com.coladungeon.levels.DeadEndLevel;
import com.coladungeon.levels.DebugLevel;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class ThemePack implements Bundlable {
    // Theme pack data
    public Class<? extends Level> normalLevel;
    public Class<? extends Level> BossLevel;
    
    // Optional theme sheet for additional customization
    private ThemeSheet themeSheet;
    
    // Predefined theme packs
    private static ThemePack SewerTheme = new ThemePack();
    private static ThemePack CavesTheme = new ThemePack();
    private static ThemePack CityTheme = new ThemePack();
    private static ThemePack HallsTheme = new ThemePack();
    private static ThemePack PrisonTheme = new ThemePack();
    private static ThemePack DebugTheme = new ThemePack();
    
    static {
        SewerTheme.normalLevel = SewerLevel.class;
        SewerTheme.BossLevel = SewerBossLevel.class;
        CavesTheme.normalLevel = CavesLevel.class;
        CavesTheme.BossLevel = CavesBossLevel.class;
        CityTheme.normalLevel = CityLevel.class;
        CityTheme.BossLevel = CityBossLevel.class;
        HallsTheme.normalLevel = HallsLevel.class;
        HallsTheme.BossLevel = HallsBossLevel.class;
        PrisonTheme.normalLevel = PrisonLevel.class;
        PrisonTheme.BossLevel = PrisonBossLevel.class;
        DebugTheme.normalLevel= DebugLevel.class;
        DebugTheme.BossLevel= DebugLevel.class;
    }

    // Default constructor
    public ThemePack() {
    }
    
    // Constructor with level classes
    public ThemePack(Class<? extends Level> normalLevel, Class<? extends Level> bossLevel) {
        this.normalLevel = normalLevel;
        this.BossLevel = bossLevel;
    }
    
    /**
     * Get the appropriate theme based on dungeon depth
     */
    public static ThemePack getTheme(int depth) {
        if (depth >= 1 && depth <= 5) {
            return DebugTheme;//||SewerTheme;
        } else if (depth >= 6 && depth <= 10) {
            return PrisonTheme;
            
        } else if (depth >= 11 && depth <= 15) {
            return CavesTheme;
            
        } else if (depth >= 16 && depth <= 20) {
            return CityTheme;
            
        } else if (depth >= 21 && depth <= 25) {
            return HallsTheme;
        } else {
            return null;
        }
    }
    
    /**
     * Creates a new instance of the normal level
     * @return A new instance of the normal level, or DeadEndLevel if instantiation fails
     */
    public Level getNormalLevel() {
        try {
            return normalLevel.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return new DeadEndLevel();
        }
    }
    
    /**
     * Creates a new instance of the boss level
     * @return A new instance of the boss level, or DeadEndLevel if instantiation fails
     */
    public Level getBossLevel() {
        try {
            return BossLevel.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return new DeadEndLevel();
        }
    }
    
    /**
     * Set a theme sheet for additional customization
     */
    public void setThemeSheet(ThemeSheet sheet) {
        this.themeSheet = sheet;
    }
    
    /**
     * Get the theme sheet
     */
    public ThemeSheet getThemeSheet() {
        return themeSheet;
    }
    
    /**
     * Check if this theme pack has a theme sheet
     */
    public boolean hasThemeSheet() {
        return themeSheet != null;
    }
    
    // Bundle keys for serialization
    private static final String NORMAL_LEVEL = "normal_level";
    private static final String BOSS_LEVEL = "boss_level";
    private static final String THEME_SHEET = "theme_sheet";
    
    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(NORMAL_LEVEL, normalLevel.getName());
        bundle.put(BOSS_LEVEL, BossLevel.getName());
        if (themeSheet != null) {
            bundle.put(THEME_SHEET, themeSheet);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void restoreFromBundle(Bundle bundle) {
        try {
            String normalLevelName = bundle.getString(NORMAL_LEVEL);
            String bossLevelName = bundle.getString(BOSS_LEVEL);
            
            normalLevel = (Class<? extends Level>) Class.forName(normalLevelName);
            
            BossLevel = (Class<? extends Level>) Class.forName(bossLevelName);
            
            if (bundle.contains(THEME_SHEET)) {
                themeSheet = (ThemeSheet) bundle.get(THEME_SHEET);
            }
        } catch (ClassNotFoundException e) {
            // Fallback to sewer theme if classes can't be found
            normalLevel = SewerLevel.class;
            BossLevel = SewerBossLevel.class;
        }
    }
}
