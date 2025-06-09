package com.coladungeon.mod.ArknightsMod;

import com.coladungeon.actors.hero.HeroClassSheet;
import com.coladungeon.levels.themes.ThemePack;
import com.coladungeon.levels.themes.ThemeSheet;
import com.coladungeon.mod.Index;
import com.coladungeon.utils.EventBus;
import com.coladungeon.mod.ArknightsMod.items.OperatorSummon;
public class ArknightsMod extends Index.Mod {

    {
        author = "alsecola";
        namespace = "arknights-parody";
    }

    @Override
    public void setup() {

        HeroClassSheet.registerStandardClass("Doctor")
                .title("博士")
                .desc("zoot进行的模拟")
                .shortDesc("来玩zoot的")
                .splashArt("cola/doctor_splashart.jpg")
                .register();

        ThemeSheet.registerThemePack("lungmen",
                new ThemePack(
                        LungmenLevel.class,
                        LungmenLevel.class,
                        (depth, branch) -> (branch == 0 && depth >= 1 && depth <= 5) ? (short) 2 : (short) 0));
        EventBus.register("Hero:created",(data)->{
            new OperatorSummon().identify().collect();
            return null;
        });
        throw new RuntimeException("mymy");
    }
}
