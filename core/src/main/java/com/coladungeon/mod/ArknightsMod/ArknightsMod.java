package com.coladungeon.mod.ArknightsMod;

import com.coladungeon.actors.hero.HeroClassSheet;
import com.coladungeon.levels.themes.ThemePack;
import com.coladungeon.levels.themes.ThemeSheet;
import com.coladungeon.mod.ArknightsMod.Headhunt.OperatorSummon;
import com.coladungeon.mod.ArknightsMod.items.weapon.CommandTerminal;
import com.coladungeon.mod.ArknightsMod.items.weapon.RhodesStandardSword;
import com.coladungeon.mod.ArknightsMod.operator.ProjektRed.RedKnife;
import com.coladungeon.mod.ArknightsMod.themes.LungmenLevel;
import com.coladungeon.mod.Index;
import com.coladungeon.utils.EventBus;

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
                .initializer((hero)->{})
                .register();

        ThemeSheet.registerThemePack("lungmen",
                new ThemePack(
                        LungmenLevel.class,
                        LungmenLevel.class,
                        (depth, branch) -> (branch == 0 && depth >= 1 && depth <= 5) ? (short) 0 : (short) 0));
        EventBus.register("Hero:created",(data)->{
            new OperatorSummon().identify().collect();
            new RhodesStandardSword().identify().collect();
            RedKnife redknife=new RedKnife();
            redknife.identify().collect();
            new CommandTerminal().identify().collect();
            return null;
        });
    }
}
