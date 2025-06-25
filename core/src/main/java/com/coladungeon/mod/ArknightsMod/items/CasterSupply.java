package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;
import com.coladungeon.mod.ArknightsMod.items.build.caster.CasterWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.caster.MechAccordWeapon;
import com.coladungeon.sprites.ItemSpriteManager;

public class CasterSupply extends Supply {
    {
        image = ItemSpriteManager.ByName("arksupply");
    }
    
    public CasterSupply() {
        super();
        this.put_in(CasterWeapon.class)
            .put_in(MechAccordWeapon.class)
            .name("术师武器补给包")
            .desc("""
                一个专门装满了术师职业武器的补给包，包含了各种不同风格的术师武器。
                
                包含的武器类型：
                • 术师武器 - 基础术师武器
                • 机械协奏武器 - 机械术师武器

                每个武器都有其独特的法术机制和特殊效果，
                为术师干员提供多样化的法术攻击选择。
                
                武器特点：
                • 法术伤害：强大的法术攻击力
                • 元素效果：火、冰、雷等元素攻击
                • 范围攻击：大范围的法术攻击
                • 充能系统：法术充能和释放机制
                • 机械联动：与机械单位的协同作战
                """);
    }
} 