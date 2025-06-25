package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;
import com.coladungeon.mod.ArknightsMod.items.CommandTerminal;
import com.coladungeon.mod.ArknightsMod.items.RefinedCloak;
import com.coladungeon.sprites.ItemSpriteManager;

public class UtilitySupply extends Supply {
    {
        image = ItemSpriteManager.ByName("arksupply");
    }
    
    public UtilitySupply() {
        super();
        this.put_in(CommandTerminal.class)
            .put_in(RefinedCloak.class)
            .name("通用物品补给包")
            .desc("罗德岛指挥终端 - 充能获得cost点数，用于部署干员\n罗德岛精制潜行斗篷 - 充能获得隐身效果的神器");
    }
} 