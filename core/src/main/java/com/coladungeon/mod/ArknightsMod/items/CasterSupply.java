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
                一个专门装满了术师职业武器的补给包，包含了各种不同分支的术师武器。
                
                包含的武器类型：
                • 便携制式施术单元 - 模块化术师武器系统
                  - 支持安装不同术师分支模块
                  - 可定制专属法杖
                  - 包含中坚、扩散、链、驭械、轰击、秘术等分支
                • 机械协奏武器 - 驭械术师专用武器
                  - 特殊机械效果
                  - 充能释放机制
                
                便携制式施术单元特点：
                • 基础伤害：2-6
                • 充能机制：3/3
                • 模块系统：6种不同分支模块
                • 定制法杖：消耗升级卷轴制作
                • 特殊效果：每个模块有独特的法术效果
                
                术师分支效果：
                • 中坚术师：标准魔法导弹
                • 扩散术师：范围伤害效果
                • 链术师：法术在敌人间跳跃
                • 驭械术师：机械增益效果
                • 轰击术师：超远距离直线攻击
                • 秘术师：能量球增益效果
                
                适合所有需要法术攻击的术师干员使用。
                """);
    }
} 