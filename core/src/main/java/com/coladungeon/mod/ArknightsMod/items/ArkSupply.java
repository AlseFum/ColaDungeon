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
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.AgentWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.ChargerWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.FlagBearerWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.PioneerWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.TacticianWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.support.BardArtifact;
import com.coladungeon.mod.ArknightsMod.items.build.support.CraftsmanWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.support.StagnatorWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.support.SummonerArtifact;
import com.coladungeon.mod.ArknightsMod.items.build.support.WeakenWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.support.WitchArtifact;
import com.coladungeon.mod.ArknightsMod.items.build.support.WitchWeapon;
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
            .put_in(AgentWeapon.class)
            .put_in(ChargerWeapon.class)
            .put_in(FlagBearerWeapon.class)
            .put_in(PioneerWeapon.class)
            .put_in(TacticianWeapon.class)
            .put_in(StagnatorWeapon.class)
            .put_in(WeakenWeapon.class)
            .put_in(BardArtifact.class)
            .put_in(SummonerArtifact.class)
            .put_in(CraftsmanWeapon.class)
            .put_in(WitchWeapon.class)
            .put_in(WitchArtifact.class)
            .put_in(PhantomWeapon.class)
            .put_in(TexasWeapon.class)
            .name("罗德岛补给包")
            .desc("包含各种职业武器和神器的补给包。\n\n" +
                  "特种职业武器：\n" +
                  "• 推击手武器 - 可以将敌人推开的特种武器\n" +
                  "• 钩索师武器 - 可以拉近敌人或自身的特种武器\n" +
                  "• 陷阱师武器 - 可以制作陷阱的特种武器\n" +
                  "• 怪杰武器 - 高风险高回报的混沌武器\n" +
                  "• 行商武器 - 可以进行交易和谈判的特种武器\n" +
                  "• 人偶师武器 - 可以召唤人偶并与之联动的特种武器\n" +
                  "• 炼金师武器 - 可以炼制各种炼金物品的特种武器\n" +
                  "• 巡空者武器 - 可以召唤羽翼并在空中战斗的特种武器\n\n" +
                  "先锋职业武器：\n" +
                  "• 尖兵武器 - 充能恢复部署费用的武器\n" +
                  "• 冲锋手武器 - 杀敌回cost，充能连击的武器\n" +
                  "• 执旗手武器 - 充能cost，释放buff的武器\n" +
                  "• 情报官武器 - 长距离攻击，装载投掷武器的武器\n" +
                  "• 战术家武器 - 充能释放战术点的武器\n\n" +
                  "辅助职业物品：\n" +
                  "• 凝滞师武器 - 释放凝滞效果的武器\n" +
                  "• 削弱者武器 - 释放削弱效果的武器\n" +
                  "• 吟游者神器 - 增益层数，移动减缓的神器\n" +
                  "• 召唤师神器 - 充能产生召唤物的神器\n" +
                  "• 工匠武器 - 制作装置的武器\n" +
                  "• 巫役武器 - 元素伤害武器\n" +
                  "• 巫役神器 - 范围元素伤害神器\n\n" +
                  "干员武器：\n" +
                  "• 幻影武器 - 幻影干员的专属武器\n" +
                  "• 德克萨斯武器 - 德克萨斯干员的专属武器")
            .image(ItemSpriteManager.ByName("arksupply"));
    }
}