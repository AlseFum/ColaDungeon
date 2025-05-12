package com.cola;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.DeadEndLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.LastLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;

import com.shatteredpixel.shatteredpixeldungeon.levels.SewerBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import java.lang.reflect.InvocationTargetException;
import com.watabou.utils.RectF;
import com.watabou.noosa.TextureFilm;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

//this file is to help make the dungeon code more soft.
public class Helper {
    public static Level newLevel(int depth) {
        Class<? extends Level> level;
        switch (depth) {
            case 1:
                level = DebugLevel.class;
                break;
            case 2:
            case 3:
            case 4:
                level = SewerLevel.class;
                break;
            case 5:
                level = SewerBossLevel.class;
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                level = PrisonLevel.class;
                break;
            case 10:
                level = PrisonBossLevel.class;
                break;
            case 11:
            case 12:
            case 13:
            case 14:
                level = CavesLevel.class;
                break;
            case 15:
                level = CavesBossLevel.class;
                break;
            case 16:
            case 17:
            case 18:
            case 19:
                level = CityLevel.class;
                break;
            case 20:
                level = CityBossLevel.class;
                break;
            case 21:
            case 22:
            case 23:
            case 24:
                level = HallsLevel.class;
                break;
            case 25:
                level = HallsBossLevel.class;
                break;
            case 26:
                level = LastLevel.class;
                break;
            default:
                level = DeadEndLevel.class;
        }
        try {
            return level.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Level newLevel(int depth, int branch) {
        Level level;
        if (branch == 0) {
            level = newLevel(depth);
        } else if (branch == 1) {
            switch (depth) {
                case 11:
                case 12:
                case 13:
                case 14:
                    level = new MiningLevel();
                    break;
                default:
                    level = new DeadEndLevel();
            }
        } else {
            level = new DeadEndLevel();
        }
        return level;
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
        } else {
            return new DirectImageMap("minecraft/bread.png", new RectF(0, 0, 1, 1), 16);
        }
    };

}
