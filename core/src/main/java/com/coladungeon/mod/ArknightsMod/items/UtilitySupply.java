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
            .desc("""
                一个专门装满了通用物品的补给包，包含了各种功能性物品和神器。
                
                包含的物品类型：
                • 罗德岛指挥终端 - 充能获得cost点数，用于部署干员
                • 罗德岛精制潜行斗篷 - 充能获得隐身效果的神器

                每个物品都有其独特的功能和特殊效果，
                为玩家提供多样化的游戏体验。
                
                物品特点：
                • 指挥系统：部署和管理干员
                • 隐身系统：充能获得不同等级的隐身效果
                • 充能机制：通过充能获得更强的效果
                • 多功能性：提供多种游戏玩法选择
                """);
    }
} 