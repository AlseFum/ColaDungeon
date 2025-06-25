package com.coladungeon.mod.ArknightsMod;

import com.coladungeon.mod.ArknightsMod.items.ArkSupply;
import com.coladungeon.mod.Index;
import com.coladungeon.sprites.ItemSpriteManager;
import com.coladungeon.utils.EventBus;

public class ArknightsMod extends Index.Mod {

    static {
        EventBus.register("ItemSpriteManager:init", (data) -> {
        ItemSpriteManager.registerTexture("cola/minor_chips.png", 32)
                .label("sniper_chips")
                .label("defender_chips")
                .label("guard_chips")
                .label("vanguard_chips")
                .label("caster_chips")
                .label("medic_chips")
                .label("support_chips")
                .label("specialist_chips");
            return null;
        });
    }

    {

        author = "alsecola";
        namespace = "arknights-parody";
    }

    @Override
    public void setup() {
        EventBus.register("Hero:created", (data) -> {
            new ArkSupply().identify().collect();
            return null;
        });
        // EventBus.register("Hero:beforeDie", (data) -> {
        //     if (data instanceof com.coladungeon.utils.EventBus.EventData edata && edata.get("hero") instanceof com.coladungeon.actors.hero.Hero h) {

        //     }
        //     return null;
        //     // return new java.util.function.Function<com.coladungeon.actors.hero.Hero, Object>() {
        //     //     @Override
        //     //     public Object apply(com.coladungeon.actors.hero.Hero hero) {
        //     //         GLog.i("caught");
        //     //         return null;
        //     //     }
        //     // };
        // });

    }
}
