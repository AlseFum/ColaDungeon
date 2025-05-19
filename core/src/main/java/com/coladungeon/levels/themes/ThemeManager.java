package com.coladungeon.levels.themes;

import com.coladungeon.CDSettings;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

    private static final String CURRENT_THEME = "current_theme";
    private static ThemeManager instance;
    
    private String currentTheme = "default";
    
    // Private constructor to enforce singleton pattern
    private ThemeManager() {
        // The theme registry is now managed by ThemeSheet
    }
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    public void registerTheme(String themeName, ThemePack themePack) {
        // Delegate to ThemeSheet
        ThemeSheet.getInstance().registerThemePack(themeName, themePack);
    }
    
    public ThemePack getCurrentTheme() {
        ThemePack theme = ThemeSheet.getInstance().getThemePack(currentTheme);
        return theme != null ? theme : ThemePack.getTheme(1); // Default to Sewer theme
    }
    
    public void setCurrentTheme(String themeName) {
        if (ThemeSheet.getInstance().hasTheme(themeName)) {
            currentTheme = themeName;
            CDSettings.put(CURRENT_THEME, currentTheme);
        }
    }
    
    public List<String> getAvailableThemeNames() {
        return ThemeSheet.getInstance().getThemeNames();
    }
    
    // Randomly select a theme from the available ones
    public void selectRandomTheme() {
        List<String> themes = getAvailableThemeNames();
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