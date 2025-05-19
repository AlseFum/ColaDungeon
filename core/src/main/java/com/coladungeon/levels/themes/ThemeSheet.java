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

import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.HashMap;
import java.util.Map;

/**
 * ThemeSheet contains the visual and gameplay configuration data for a theme.
 * This includes information about textures, colors, sound effects, and other theme-specific elements.
 */
public class ThemeSheet implements Bundlable {
    
    // Keys for storing in bundle
    private static final String THEME_NAME = "theme_name";
    private static final String PROPERTIES_KEYS = "properties_keys";
    private static final String PROPERTIES_VALUES = "properties_values";
    
    private String themeName;
    private Map<String, String> properties = new HashMap<>();
    
    public ThemeSheet(String name) {
        this.themeName = name;
    }
    
    /**
     * Sets a visual or gameplay property for this theme
     * @param key The property name
     * @param value The property value
     * @return This ThemeSheet for method chaining
     */
    public ThemeSheet setProperty(String key, String value) {
        properties.put(key, value);
        return this;
    }
    
    /**
     * Gets a property value by its key
     * @param key The property key
     * @return The property value, or null if not found
     */
    public String getProperty(String key) {
        return properties.get(key);
    }
    
    /**
     * Gets a property value with a default fallback if not found
     * @param key The property key
     * @param defaultValue The default value to return if the property is not found
     * @return The property value, or the default value if not found
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }
    
    /**
     * Checks if a property exists
     * @param key The property key
     * @return true if the property exists, false otherwise
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }
    
    /**
     * Gets the name of this theme
     * @return The theme name
     */
    public String getThemeName() {
        return themeName;
    }
    
    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(THEME_NAME, themeName);
        
        String[] keys = properties.keySet().toArray(new String[0]);
        String[] values = new String[keys.length];
        
        for (int i = 0; i < keys.length; i++) {
            values[i] = properties.get(keys[i]);
        }
        
        bundle.put(PROPERTIES_KEYS, keys);
        bundle.put(PROPERTIES_VALUES, values);
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        themeName = bundle.getString(THEME_NAME);
        
        String[] keys = bundle.getStringArray(PROPERTIES_KEYS);
        String[] values = bundle.getStringArray(PROPERTIES_VALUES);
        
        properties.clear();
        if (keys != null && values != null && keys.length == values.length) {
            for (int i = 0; i < keys.length; i++) {
                properties.put(keys[i], values[i]);
            }
        }
    }
} 