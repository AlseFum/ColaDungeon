package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;
import com.coladungeon.sprites.ItemSpriteManager;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.AlchemistWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.DollkeeperWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.GeekWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.HookWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.MerchantWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.PusherWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.SkyRangerWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.TrappistWeapon;
import com.coladungeon.mod.ArknightsMod.operator.Phantom.PhantomWeapon;
import com.coladungeon.mod.ArknightsMod.operator.TexasTheOmertosa.TexasWeapon;
import com.coladungeon.sprites.ItemSpriteSheet;

public class ArkSupply extends Supply {
    public ArkSupply() {
        super();
        this.put_in(PusherWeapon.class)
            .put_in(HookWeapon.class)
            .put_in(TrappistWeapon.TrappistArtifact.class)
            .put_in(GeekWeapon.class)
            .put_in(MerchantWeapon.class)
            .put_in(DollkeeperWeapon.class)
            .put_in(AlchemistWeapon.class)
            .put_in(SkyRangerWeapon.class)
            .put_in(PhantomWeapon.class)
            .put_in(TexasWeapon.class)
            .name("罗德岛补给包")
            .desc("包含各种特种职业武器和干员武器的补给包。\n\n" +
                  "包含以下武器：\n" +
                  "• 推击手武器 - 可以将敌人推开的特种武器\n" +
                  "• 钩索师武器 - 可以拉近敌人或自身的特种武器\n" +
                  "• 陷阱师武器 - 可以制作陷阱的特种武器\n" +
                  "• 怪杰武器 - 高风险高回报的混沌武器\n" +
                  "• 行商武器 - 可以进行交易和谈判的特种武器\n" +
                  "• 人偶师武器 - 可以召唤人偶并与之联动的特种武器\n" +
                  "• 炼金师武器 - 可以炼制各种炼金物品的特种武器\n" +
                  "• 巡空者武器 - 可以召唤羽翼并在空中战斗的特种武器\n" +
                  "• 幻影武器 - 幻影干员的专属武器\n" +
                  "• 德克萨斯武器 - 德克萨斯干员的专属武器")
            .image(ItemSpriteManager.ByName("arksupply"));
    }
}