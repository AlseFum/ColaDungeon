package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;
import com.coladungeon.mod.ArknightsMod.items.weapon.CommandTerminal;
import com.coladungeon.mod.ArknightsMod.items.weapon.FlagBearerWeapon;
import com.coladungeon.mod.ArknightsMod.items.weapon.RefinedCloak;
import com.coladungeon.mod.ArknightsMod.items.weapon.RhodesStandardSword;
import com.coladungeon.mod.ArknightsMod.operator.Phantom.PhantomWeapon;
import com.coladungeon.mod.ArknightsMod.operator.ProjektRed.RedKnife;
import com.coladungeon.mod.ArknightsMod.operator.TexasTheOmertosa.TexasWeapon;
import com.coladungeon.mod.ArknightsMod.operator.Fang.Spear;
import com.coladungeon.sprites.ItemSpriteManager;

public class ArkSupply extends Supply {
    {
        image=ItemSpriteManager.registerTexture("cola/arksupply.png",64).label("arksupply").getByName("arksupply");
    }
    public ArkSupply() {
        super();
        this.put_in(CommandTerminal.class)
            .put_in(FlagBearerWeapon.class)
            .put_in(RefinedCloak.class)
            .put_in(RhodesStandardSword.class)
            .put_in(PhantomWeapon.class)
            .put_in(RedKnife.class)
            .put_in(TexasWeapon.class)
            .put_in(Spear.class)
            .name("罗德岛补给包")
            .desc("一个装满了罗德岛特制装备的补给包，可以从中获取到各种罗德岛特制装备。\n\n" +
                  "包含以下装备：\n" +
                  "- 罗德岛指挥终端：用于部署干员\n" +
                  "- 旗手武器：可充能的特殊武器\n" +
                  "- 罗德岛精制潜行斗篷：提供隐身效果\n" +
                  "- 罗德岛标准剑：基础近战武器\n" +
                  "- 演出双匕：傀影的专属武器\n" +
                  "- 红的小刀：红的专属武器\n" +
                  "- 缄默德克萨斯的武器：双持武器\n" +
                  "- 芬的长矛：先锋干员的武器");
    }
}