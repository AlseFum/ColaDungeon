package com.coladungeon.levels.themes;

import com.coladungeon.levels.Level;
import com.coladungeon.levels.SewerLevel;
import com.coladungeon.levels.SewerBossLevel;
import com.coladungeon.levels.CavesBossLevel;
import com.coladungeon.levels.CavesLevel;
import com.coladungeon.levels.CityBossLevel;
import com.coladungeon.levels.CityLevel;
import com.coladungeon.levels.HallsBossLevel;
import com.coladungeon.levels.HallsLevel;

import com.coladungeon.levels.PrisonBossLevel;
import com.coladungeon.levels.PrisonLevel;

public class ThemePack {
    // emmm, try to make things more separated
    public Class<? extends Level> normalLevel;
    public Class<? extends Level> BossLevel;
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
