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

import com.coladungeon.CDSettings;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ThemeManager {

    private static final String CURRENT_THEME = "current_theme";
    private static ThemeManager instance;
    
    private String currentTheme = "default";
    private Map<String, ThemePack> availableThemes = new HashMap<>();
    
    // Private constructor to enforce singleton pattern
    private ThemeManager() {
        // Initialize available themes
        // The built-in themes are added by default
    }
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    public void registerTheme(String themeName, ThemePack themePack) {
        availableThemes.put(themeName, themePack);
    }
    
    public ThemePack getCurrentTheme() {
        return availableThemes.getOrDefault(currentTheme, ThemePack.getTheme(1)); // Default to Sewer theme
    }
    
    public void setCurrentTheme(String themeName) {
        if (availableThemes.containsKey(themeName)) {
            currentTheme = themeName;
            CDSettings.put(CURRENT_THEME, currentTheme);
        }
    }
    
    public ArrayList<String> getAvailableThemeNames() {
        return new ArrayList<>(availableThemes.keySet());
    }
    
    // Randomly select a theme from the available ones
    public void selectRandomTheme() {
        ArrayList<String> themes = getAvailableThemeNames();
        if (!themes.isEmpty()) {
            String randomTheme = Random.element(themes);
            setCurrentTheme(randomTheme);
        }
    }
    
    public void loadSettings() {
        currentTheme = CDSettings.getString(CURRENT_THEME, "default");
    }
    
    public void storeInBundle(Bundle bundle) {
        bundle.put(CURRENT_THEME, currentTheme);
    }
    
    public void restoreFromBundle(Bundle bundle) {
        currentTheme = bundle.getString(CURRENT_THEME);
    }
} 