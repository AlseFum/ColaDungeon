package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;
import com.coladungeon.mod.ArknightsMod.items.build.defender.GuardianArmor;
import com.coladungeon.mod.ArknightsMod.items.build.defender.IronGuardArmor;
import com.coladungeon.mod.ArknightsMod.items.build.defender.MagicGuardArmor;
import com.coladungeon.mod.ArknightsMod.items.build.defender.DecisiveArmor;
import com.coladungeon.mod.ArknightsMod.items.build.defender.FortressArmor;
import com.coladungeon.mod.ArknightsMod.items.build.defender.SentinelArmor;
import com.coladungeon.mod.ArknightsMod.items.build.defender.UnbreakableArmor;
import com.coladungeon.mod.ArknightsMod.items.build.defender.OriginGuardArmor;
import com.coladungeon.sprites.ItemSpriteManager;

public class DefenderSupply extends Supply {
    {
        image = ItemSpriteManager.ByName("arksupply");
    }
    
    public DefenderSupply() {
        super();
        this.put_in(IronGuardArmor.class)
            .put_in(GuardianArmor.class)
            .put_in(UnbreakableArmor.class)
            .put_in(MagicGuardArmor.class)
            .put_in(DecisiveArmor.class)
            .put_in(FortressArmor.class)
            .put_in(SentinelArmor.class)
            .put_in(OriginGuardArmor.class)
            .name("重装装甲补给包")
            .desc("""
                一个专门装满了重装职业装甲的补给包，包含了各种不同风格的重装装甲。
                
                包含的装甲类型：
                • 铁卫装甲 - 基础防护装甲，适合新手重装干员
                • 守护者装甲 - 受击时自动恢复生命值
                • 不屈者装甲 - 禁疗但通过战斗恢复生命值
                • 驭法铁卫装甲 - 反弹魔法伤害
                • 决战者装甲 - 周围有敌人时快速充能
                • 要塞装甲 - 需要配套武器，否则有后坐力
                • 哨戒铁卫装甲 - 攻击距离远，视野增强
                • 本源铁卫装甲 - 装甲本身造成伤害

                每个装甲都有其独特的防御机制和特殊效果，
                为重装干员提供多样化的防护选择。
                
                装甲特点：
                • 防御力范围：4-14
                • 力量需求：16-22
                • 特殊效果触发概率：20%-50%
                • 支持升级强化
                """);
    }
} 