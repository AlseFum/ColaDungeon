package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;
import com.coladungeon.mod.ArknightsMod.items.build.support.BardArtifact;
import com.coladungeon.mod.ArknightsMod.items.build.support.CraftsmanWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.support.StagnatorWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.support.SummonerArtifact;
import com.coladungeon.mod.ArknightsMod.items.build.support.WeakenWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.support.WitchArtifact;
import com.coladungeon.mod.ArknightsMod.items.build.support.WitchWeapon;
import com.coladungeon.sprites.ItemSpriteManager;

public class SupportSupply extends Supply {
    {
        image = ItemSpriteManager.ByName("arksupply");
    }
    
    public SupportSupply() {
        super();
        this.put_in(StagnatorWeapon.class)
            .put_in(WeakenWeapon.class)
            .put_in(BardArtifact.class)
            .put_in(SummonerArtifact.class)
            .put_in(CraftsmanWeapon.class)
            .put_in(WitchWeapon.class)
            .put_in(WitchArtifact.class)
            .name("辅助物品补给包")
            .desc("""
                一个专门装满了辅助职业武器和神器的补给包，包含了各种不同风格的辅助物品。
                
                包含的物品类型：
                • 凝滞师武器 - 释放凝滞效果的武器
                • 削弱者武器 - 释放削弱效果的武器
                • 吟游者神器 - 增益层数，移动减缓的神器
                • 召唤师神器 - 充能产生召唤物的神器
                • 工匠武器 - 制作装置的武器
                • 巫役武器 - 元素伤害武器
                • 巫役神器 - 范围元素伤害神器

                每个物品都有其独特的辅助机制和特殊效果，
                为辅助干员提供多样化的支援选择。
                
                物品特点：
                • 控制效果：凝滞、削弱等状态控制
                • 增益支援：为友军提供各种增益效果
                • 召唤系统：召唤各种辅助单位
                • 元素伤害：火、冰、雷等元素攻击
                • 装置制作：制作各种功能性装置
                """);
    }
} 