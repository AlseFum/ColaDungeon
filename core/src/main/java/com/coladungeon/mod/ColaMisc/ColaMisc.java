package com.coladungeon.mod.ColaMisc;

import com.coladungeon.mod.Index;
import com.coladungeon.utils.EventBus;
import com.coladungeon.utils.EventBus.EventData;
import com.coladungeon.Dungeon;
public class ColaMisc extends Index.Mod {

    {
        author = "alsecola";
        namespace = "cola-misc";
    }

    @Override
    public void setup() {
        // System.out.println("cola-misc: setup");
        // EventBus.register("PhysicalDamage",(e) -> {
        //     System.out.println("cola-misc: PhysicalDamage heard");
        //     if(((EventData)e).get("attacker") == Dungeon.hero){
        //         System.out.println("cola-misc: PhysicalDamage");
        //         return DamageAugment.Add(10000);
        //     }
        //     else return null;
        // });
    }
}
