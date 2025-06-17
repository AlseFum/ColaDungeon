package com.coladungeon.levels.themes;

import com.coladungeon.levels.Level;
import com.coladungeon.levels.DeadEndLevel;
/**
 * ThemeBuilder provides a fluent interface for creating and configuring ThemePack instances.
 * It allows for easy construction of theme packs with different level configurations.
 */
public class ThemeBuilder {
    
    private ThemePack themePack;
    
    public ThemeBuilder() {
        themePack = new ThemePack();
        themePack.normalLevel = DeadEndLevel.class;
        themePack.BossLevel = DeadEndLevel.class;
    }
    
    /**
     * Sets the regular level class for this theme
     * @param levelClass The class to use for normal levels
     * @return This builder for method chaining
     */
    public ThemeBuilder setNormalLevel(Class<? extends Level> levelClass) {
        themePack.normalLevel = levelClass;
        return this;
    }
    
    /**
     * Sets the boss level class for this theme
     * @param levelClass The class to use for boss levels
     * @return This builder for method chaining
     */
    public ThemeBuilder setBossLevel(Class<? extends Level> levelClass) {
        themePack.BossLevel = levelClass;
        return this;
    }
    
    /**
     * Registers the built theme with the ThemeManager under the given name
     * @param themeName Name to register the theme under
     * @return The completed ThemePack instance (same as build())
     */
    public ThemePack register(String themeName) {
        ThemeManager.registerTheme(themeName, themePack);
        return themePack;
    }
} 