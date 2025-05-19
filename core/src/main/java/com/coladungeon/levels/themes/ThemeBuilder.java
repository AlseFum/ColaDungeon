/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

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
        ThemeManager.getInstance().registerTheme(themeName, built);
        return built;
    }
} 