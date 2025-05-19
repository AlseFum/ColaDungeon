package com.coladungeon.levels.themes;

import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ThemeSheet is a singleton that stores all available ThemePack objects.
 * It provides centralized access to themes for the ThemeManager to use.
 */
public class ThemeSheet implements Bundlable {
    
    // Singleton instance
    private static ThemeSheet instance = null;
    
    // Keys for storing in bundle
    private static final String THEME_PACKS = "theme_packs";
    private static final String THEME_NAMES = "theme_names";
    
    // Map of theme name to ThemePack
    private Map<String, ThemePack> themePacks = new HashMap<>();
    
    // Private constructor to enforce singleton pattern
    private ThemeSheet() {
        // Initialize with default themes
        registerDefaultThemes();
    }
    
    /**
     * Get the singleton instance
     * @return The ThemeSheet instance
     */
    public static ThemeSheet getInstance() {
        if (instance == null) {
            instance = new ThemeSheet();
        }
        return instance;
    }
    
    /**
     * Register built-in default themes
     */
    private void registerDefaultThemes() {
        // Default themes are registered here
        // These correspond to the static themes in ThemePack
        registerThemePack("sewer", ThemePack.getTheme(1));
        registerThemePack("prison", ThemePack.getTheme(6));
        registerThemePack("caves", ThemePack.getTheme(11));
        registerThemePack("city", ThemePack.getTheme(16));
        registerThemePack("halls", ThemePack.getTheme(21));
    }
    
    /**
     * Register a new theme pack
     * @param name The name of the theme
     * @param themePack The ThemePack object
     */
    public void registerThemePack(String name, ThemePack themePack) {
        themePacks.put(name, themePack);
    }
    
    /**
     * Get a theme pack by name
     * @param name The name of the theme
     * @return The ThemePack, or null if not found
     */
    public ThemePack getThemePack(String name) {
        return themePacks.get(name);
    }
    
    /**
     * Get all available theme names
     * @return List of theme names
     */
    public List<String> getThemeNames() {
        return new ArrayList<>(themePacks.keySet());
    }
    
    /**
     * Get all available theme packs
     * @return Map of theme names to theme packs
     */
    public Map<String, ThemePack> getAllThemePacks() {
        return new HashMap<>(themePacks);
    }
    
    /**
     * Check if a theme exists
     * @param name The name of the theme
     * @return true if the theme exists, false otherwise
     */
    public boolean hasTheme(String name) {
        return themePacks.containsKey(name);
    }
    
    @Override
    public void storeInBundle(Bundle bundle) {
        String[] names = themePacks.keySet().toArray(new String[0]);
        bundle.put(THEME_NAMES, names);
        
        // Store theme packs as a collection of Bundlable objects
        ArrayList<Bundlable> packs = new ArrayList<>();
        for (String name : names) {
            packs.add(themePacks.get(name));
        }
        bundle.put(THEME_PACKS, packs);
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        themePacks.clear();
        
        String[] names = bundle.getStringArray(THEME_NAMES);
        Collection<Bundlable> packs = bundle.getCollection(THEME_PACKS);
        
        if (names != null && packs != null && names.length == packs.size()) {
            int i = 0;
            for (Bundlable pack : packs) {
                if (pack instanceof ThemePack && i < names.length) {
                    themePacks.put(names[i], (ThemePack)pack);
                    i++;
                }
            }
        } else {
            // If loading fails, reinitialize with defaults
            registerDefaultThemes();
        }
    }
    
    /**
     * Reset the singleton instance (for testing or resets)
     */
    public static void reset() {
        instance = null;
    }
} 