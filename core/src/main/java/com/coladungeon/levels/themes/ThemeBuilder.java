package com.coladungeon.levels.themes;

import com.coladungeon.levels.Level;

/**
 * ThemeBuilder provides a fluent interface for creating and configuring ThemePack instances.
 * It allows for easy construction of theme packs with different level configurations.
 */
public class ThemeBuilder {
    
    private ThemePack themePack;
    
    public ThemeBuilder() {
        themePack = new ThemePack();
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
     * Builds and returns the configured ThemePack
     * @return The completed ThemePack instance
     */
    public ThemePack build() {
        // Validate the theme pack before returning
        if (themePack.normalLevel == null || themePack.BossLevel == null) {
            throw new IllegalStateException("Theme pack must have both normal and boss level classes defined");
        }
        return themePack;
    }
    
    /**
     * Registers the built theme with the ThemeManager under the given name
     * @param themeName Name to register the theme under
     * @return The completed ThemePack instance (same as build())
     */
    public ThemePack register(String themeName) {
        ThemePack built = build();
        ThemeManager.registerTheme(themeName, built);
        return built;
    }
} 