package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;

import com.coladungeon.mod.ArknightsMod.items.build.guard.ArtsFighterWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.CenturionWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.CrusherWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.DreadnoughtWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.EarthshakerWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.FighterWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.InstructorWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.LiberatorWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.LordWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.ReaperWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.SolobladeWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.SwordmasterWeapon;
import com.coladungeon.sprites.ItemSpriteManager;

public class GuardSupply extends Supply {
    {
        image = ItemSpriteManager.ByName("arksupply");
    }
    
    public GuardSupply() {
        super();
        this.put_in(CenturionWeapon.class)
            .put_in(DreadnoughtWeapon.class)
            .put_in(ArtsFighterWeapon.class)
            .put_in(CrusherWeapon.class)
            .put_in(EarthshakerWeapon.class)
            .put_in(FighterWeapon.class)
            .put_in(InstructorWeapon.class)
            .put_in(LiberatorWeapon.class)
            .put_in(LordWeapon.class)
            .put_in(ReaperWeapon.class)
            .put_in(SolobladeWeapon.class)
            .put_in(SwordmasterWeapon.class)
            .name("守卫武器补给包")
            .desc("""
                一个专门装满了守卫职业武器的补给包，包含了各种不同风格的守卫武器。
                
                包含的武器类型：
                • 罗德岛标准剑 - 基础标准武器，供一般人使用
                • 百夫长武器 - 平衡型武器
                • 无畏武器 - 抗性解除武器
                • 术战武器 - 法术伤害武器
                • 粉碎武器 - 重击武器
                • 震地武器 - 范围伤害武器
                • 战士武器 - 充能武器
                • 教官武器 - 友军增益武器
                • 解放武器 - 充能解放武器
                • 领主武器 - 远程控制武器
                • 死神武器 - 生命汲取武器
                • 独剑武器 - 生命值相关武器
                • 剑术大师武器 - 双击控制武器

                本源术士未实现
                """);
    }
} 