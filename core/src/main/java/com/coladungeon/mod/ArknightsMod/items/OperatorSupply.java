package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;
import com.coladungeon.mod.ArknightsMod.operator.Phantom.PhantomWeapon;
import com.coladungeon.mod.ArknightsMod.operator.TexasTheOmertosa.TexasWeapon;
import com.coladungeon.sprites.ItemSpriteManager;

public class OperatorSupply extends Supply {
    {
        image = ItemSpriteManager.ByName("arksupply");
    }
    
    public OperatorSupply() {
        super();
        this.put_in(PhantomWeapon.class)
            .put_in(TexasWeapon.class)
            .name("干员武器补给包")
            .desc("""
                一个专门装满了干员专属武器的补给包，包含了各种干员的专属武器。
                
                包含的武器类型：
                • 幻影武器 - 幻影干员的专属武器
                • 德克萨斯武器 - 德克萨斯干员的专属武器

                每个武器都是特定干员的专属装备，
                具有独特的技能和效果。
                
                武器特点：
                • 专属设计：为特定干员量身定制
                • 独特技能：具有干员特色的特殊技能
                • 高契合度：与干员能力完美配合
                • 稀有珍贵：专属武器的稀有性
                """);
    }
} 