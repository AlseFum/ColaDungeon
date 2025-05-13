package com.cola;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsLevel;

import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;

public class ThemePack {
    // emmm, try to make things more separated
    Class<? extends Level> normalLevel;
    Class<? extends Level> BossLevel;
    static ThemePack SewerTheme = new ThemePack();
    static ThemePack CavesTheme = new ThemePack();
    static ThemePack CityTheme = new ThemePack();
    static ThemePack HallsTheme = new ThemePack();
    static ThemePack PrisonTheme = new ThemePack();
    static {
        SewerTheme.normalLevel = SewerLevel.class;
        SewerTheme.BossLevel = SewerBossLevel.class;
        CavesTheme.normalLevel = CavesLevel.class;
        CavesTheme.BossLevel = CavesBossLevel.class;
        CityTheme.normalLevel = CityLevel.class;
        CityTheme.BossLevel = CityBossLevel.class;
        HallsTheme.normalLevel = HallsLevel.class;
        HallsTheme.BossLevel = HallsBossLevel.class;
        PrisonTheme.normalLevel = PrisonLevel.class;
        PrisonTheme.BossLevel = PrisonBossLevel.class;
    }

    public static ThemePack getTheme(int depth) {
        if (depth >= 1 && depth <= 5) {
            return SewerTheme;
        } else if (depth >= 6 && depth <= 10) {
            return CavesTheme;
        } else if (depth >= 11 && depth <= 15) {
            return CityTheme;
        } else if (depth >= 16 && depth <= 20) {
            return HallsTheme;
        } else if (depth >= 21 && depth <= 25) {
            return PrisonTheme;
        } else {
            return null;
        }
    }
}
