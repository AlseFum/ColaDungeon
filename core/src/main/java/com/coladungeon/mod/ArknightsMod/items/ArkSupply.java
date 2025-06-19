package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;
import com.coladungeon.mod.ArknightsMod.items.build.RhodesStandardSword;
import com.coladungeon.mod.ArknightsMod.items.build.caster.MechAccordWeapon;
import com.coladungeon.mod.ArknightsMod.operator.Phantom.PhantomWeapon;
import com.coladungeon.mod.ArknightsMod.operator.ProjektRed.RedKnife;
import com.coladungeon.mod.ArknightsMod.operator.TexasTheOmertosa.TexasWeapon;
import com.coladungeon.sprites.ItemSpriteManager;

public class ArkSupply extends Supply {
    {
        image=ItemSpriteManager.ByName("arksupply");
    }
    public ArkSupply() {
        super();
        this.put_in(CommandTerminal.class)
            .put_in(RefinedCloak.class)
            .put_in(RhodesStandardSword.class)
            .put_in(PhantomWeapon.class)
            .put_in(RedKnife.class)
            .put_in(TexasWeapon.class)
            .put_in(MechAccordWeapon.class)
            .put_in(GuardSupply.class)
            .put_in(DefenderSupply.class)
            .name("罗德岛补给包")
            .desc("""
                一个装满了罗德岛特制装备的补给包，可以从中获取到各种罗德岛特制装备。
                
                包含的装备类型：
                • 命令终端 - 干员部署和指挥系统
                • 精制斗篷 - 特殊防护装备
                • 罗德岛标准剑 - 基础近战武器
                • 幻影武器 - 幻影干员专用武器
                • 红刀 - 红干员专用武器
                • 德克萨斯武器 - 德克萨斯干员专用武器
                • 机械协奏武器 - 术师干员专用武器
                • 守卫武器补给包 - 近卫干员武器集合
                • 重装装甲补给包 - 重装干员装甲集合
                """);
    }
}