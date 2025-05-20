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

package com.coladungeon.utils;

import com.coladungeon.levels.Level;
import com.coladungeon.Dungeon;

import com.coladungeon.levels.DeadEndLevel;
import com.coladungeon.levels.DebugLevel;
import com.coladungeon.levels.LastLevel;
import com.coladungeon.levels.LastShopLevel;
import com.coladungeon.levels.MiningLevel;
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
            if (Dungeon.bossLevel()) {
                level = theme.getBossLevel();
            } else {
                level = theme.getNormalLevel();
            }
        }

        return level;
    }

    public static Level newLevel(int depth, int branch) {
        
        Level level;

        
        switch (branch) {
            case 0:
                if (depth > 25) {
                    level = new LastLevel();
                } else if (depth == 25 && !Dungeon.bossLevel(depth)) {
                    level = new LastShopLevel();
                } else if (Dungeon.bossLevel(depth)) {
                    ThemePack theme = ThemePack.getTheme(depth);
                    if (theme != null) {
                        level = theme.getBossLevel();
                    } else {
                        level = new DeadEndLevel();
                    }
                } else {
                    ThemePack theme = ThemePack.getTheme(depth);
                    if (theme != null) {
                        level = theme.getNormalLevel();
                    } else {
                        level = new DeadEndLevel();
                    }
                }
                break;
                
            case 1:
                level = new MiningLevel();
                break;
                
            default:
                level = new DeadEndLevel();
                break;
        }
        
        level.create();
        
        return level;
    }
    
    public static ItemSpriteManager.ImageMapping map_image(int image) {
        return ItemSpriteManager.mapImage(image);
    }
}
