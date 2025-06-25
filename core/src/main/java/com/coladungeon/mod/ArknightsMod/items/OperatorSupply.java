package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;
import com.coladungeon.mod.ArknightsMod.operator.Phantom.PhantomWeapon;
import com.coladungeon.mod.ArknightsMod.operator.TexasTheOmertosa.TexasWeapon;
import com.coladungeon.sprites.ItemSpriteManager;

public class OperatorSupply extends Supply {
    {
        image = ItemSpriteManager.ByName("arksupply");
    }
    
    public OperatorSupply() {
        super();
        this.put_in(PhantomWeapon.class)
            .put_in(TexasWeapon.class)
            .name("干员武器补给包")
            .desc("一个专门装满了干员专属武器的补给包，包含了各种干员的专属武器。");
    }
} 