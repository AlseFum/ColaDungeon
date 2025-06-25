package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.AlchemistWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.DollkeeperWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.GeekWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.HookWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.MerchantWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.PusherWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.SkyRangerWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.TrappistWeapon;
import com.coladungeon.sprites.ItemSpriteManager;

public class SpecialistSupply extends Supply {
    {
        image = ItemSpriteManager.ByName("arksupply");
    }
    
    public SpecialistSupply() {
        super();
        this.put_in(PusherWeapon.class)
            .put_in(HookWeapon.class)
            .put_in(TrappistWeapon.TrappistArtifact.class)
            .put_in(GeekWeapon.class)
            .put_in(MerchantWeapon.class)
            .put_in(DollkeeperWeapon.class)
            .put_in(AlchemistWeapon.class)
            .put_in(SkyRangerWeapon.class)
            .name("特种武器补给包")
            .desc("""
                一个专门装满了特种职业武器的补给包，包含了各种不同风格的特种武器。
                
                包含的武器类型：
                • 推击手武器 - 可以将敌人推开的特种武器
                • 钩索师武器 - 可以拉近敌人或自身的特种武器
                • 陷阱师武器 - 可以制作陷阱的特种武器
                • 怪杰武器 - 高风险高回报的混沌武器
                • 行商武器 - 可以进行交易和谈判的特种武器
                • 人偶师武器 - 可以召唤人偶并与之联动的特种武器
                • 炼金师武器 - 可以炼制各种炼金物品的特种武器
                • 巡空者武器 - 可以召唤羽翼并在空中战斗的特种武器

                每个武器都有其独特的特种机制和特殊效果，
                为特种干员提供多样化的战术选择。
                
                武器特点：
                • 位移控制：推拉、钩索等位置控制
                • 陷阱系统：制作各种功能性陷阱
                • 召唤联动：人偶、羽翼等召唤物
                • 交易系统：与敌人进行交易和谈判
                • 炼金制作：制作各种炼金物品
                • 空中战斗：特殊的空中战斗机制
                """);
    }
} 