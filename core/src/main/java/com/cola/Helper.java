package com.cola;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;

import com.shatteredpixel.shatteredpixeldungeon.levels.DeadEndLevel;

import com.shatteredpixel.shatteredpixeldungeon.levels.LastLevel;


import com.watabou.utils.RectF;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;


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

        return level;
    }

    public static Level newLevel(int depth, int branch) {
        if (depth >= 26 && depth <= 30)
            return new LastLevel();
        else if (depth >= 0)
            return newLevel(depth);
        else
            return new DeadEndLevel();

    }

    public static class DirectImageMap {
        public String path;
        public RectF rect;
        public float height;

        public DirectImageMap(String path, RectF rect, float height) {
            this.path = path;
            this.rect = rect;
            this.height = height;
        }
    }

    public static DirectImageMap map_image(int image) {
        if (image < 114514) {
            return new DirectImageMap(
                    Assets.Sprites.ITEMS,
                    ItemSpriteSheet.film.get(image),
                    ItemSpriteSheet.film.height(image));
        } else if (image == 114515) {
            return new DirectImageMap("minecraft/bread.png", new RectF(0.25f, 0, 0.75f, 1), 16);
        } else {
            // rectF是按百分比代表texture的位置
            return new DirectImageMap("minecraft/golden_apple.png", new RectF(0.25f, 0, 0.75f, 1), 16);
        }
    };
}
