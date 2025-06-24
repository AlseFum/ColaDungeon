package com.coladungeon.mod.ArknightsMod;

import com.coladungeon.actors.hero.HeroClassSheet;
import com.coladungeon.levels.themes.ThemePack;
import com.coladungeon.levels.themes.ThemeSheet;
import com.coladungeon.mod.ArknightsMod.items.ArkSupply;
import com.coladungeon.mod.Index;
import com.coladungeon.utils.GLog;
import com.coladungeon.utils.EventBus;

public class ArknightsMod extends Index.Mod {

    {

        author = "alsecola";
        namespace = "arknights-parody";
    }

    @Override
    public void setup() {

        // HeroClassSheet.registerStandardClass("Doctor")
        //         .title("博士")
        //         .desc("zoot进行的模拟")
        //         .shortDesc("来玩zoot的")
        //         .splashArt("cola/doctor_splashart.jpg")
        //         .initializer((hero) -> {
        //         })
        //         .register();
        EventBus.register("Hero:created", (data) -> {
            new ArkSupply().identify().collect();
            return null;
        });
        // EventBus.register("Hero:beforeDie", (data) -> {
        //     if (data instanceof com.coladungeon.utils.EventBus.EventData edata && edata.get("hero") instanceof com.coladungeon.actors.hero.Hero h) {
        //         h.HP += 1000;
        //         h.updateHT(true);
        //         GLog.i("Hero:beforeDie");
        //     }
        //     return null;
        //     return new java.util.function.Function<com.coladungeon.actors.hero.Hero, Object>() {
        //         @Override
        //         public Object apply(com.coladungeon.actors.hero.Hero hero) {
        //             GLog.i("caught");
        //             return null;
        //         }
        //     };
        // });

    }
}
