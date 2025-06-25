package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;
import com.coladungeon.mod.ArknightsMod.items.build.sniper.Bow;
import com.coladungeon.mod.ArknightsMod.items.build.sniper.Crossbow;
import com.coladungeon.mod.ArknightsMod.items.build.sniper.PumpActionRifle;
import com.coladungeon.mod.ArknightsMod.items.build.sniper.SniperRifle;
import com.coladungeon.mod.ArknightsMod.items.build.sniper.SniperWeapon;
import com.coladungeon.sprites.ItemSpriteManager;

public class SniperSupply extends Supply {
    {
        image = ItemSpriteManager.ByName("arksupply");
    }
    
    public SniperSupply() {
        super();
        this.put_in(SniperWeapon.class)
            .put_in(Bow.class)
            .put_in(Crossbow.class)
            .put_in(SniperRifle.class)
            .put_in(PumpActionRifle.class)
            .name("狙击武器补给包")
            .desc("""
                一个专门装满了狙击职业武器的补给包，包含了各种不同风格的狙击武器。
                
                包含的武器类型：
                • 狙击武器 - 基础狙击武器
                • 弓箭 - 传统弓箭武器
                • 弩箭 - 高精度弩箭武器
                • 狙击步枪 - 现代狙击步枪
                • 泵动步枪 - 连发狙击步枪

                每个武器都有其独特的狙击机制和特殊效果，
                为狙击干员提供多样化的远程攻击选择。
                
                武器特点：
                • 远程攻击：超长攻击距离
                • 高精度：精准的瞄准系统
                • 穿透伤害：可以穿透多个敌人
                • 暴击机制：高暴击率和暴击伤害
                • 连发系统：快速连续射击
                """);
    }
} 