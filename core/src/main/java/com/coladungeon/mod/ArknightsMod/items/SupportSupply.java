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
                """);
    }
} 