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
            .desc("一个专门装满了重装职业装甲的补给包，包含了各种不同风格的重装装甲。");
    }
} 