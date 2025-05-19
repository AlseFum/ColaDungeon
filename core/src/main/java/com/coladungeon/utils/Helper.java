package com.coladungeon.utils;

import com.coladungeon.levels.Level;
import com.coladungeon.Dungeon;

import com.coladungeon.levels.DeadEndLevel;
import com.coladungeon.levels.DebugLevel;
import com.coladungeon.levels.LastLevel;
import com.coladungeon.levels.themes.ThemePack;
import com.coladungeon.sprites.ItemSpriteManager;

//this file is to help make the dungeon code more soft.
public class Helper {
    public static Level newLevel(int depth) {
        Level level;
        ThemePack theme = ThemePack.getTheme(depth);
        if (theme == null) {
            level = new DeadEndLevel();
        } else {
            try {

                if (Dungeon.bossLevel()) {
                    level = theme.BossLevel.getDeclaredConstructor().newInstance();
                } else {
                    level = theme.normalLevel.getDeclaredConstructor().newInstance();
                }
            } catch (Exception e) {
                level = new DeadEndLevel();
            }
        }

        return new DebugLevel();
    }

    public static Level newLevel(int depth, int branch) {
        if (depth >= 26 && depth <= 30)
            return new LastLevel();
        else if (depth >= 0)
            return newLevel(depth);
        else
            return new DeadEndLevel();

    }

    public static ItemSpriteManager.ImageMapping map_image(int image) {
        return ItemSpriteManager.mapImage(image);
    }
}
