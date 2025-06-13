package com.coladungeon.mod.ColaMisc;

import com.coladungeon.mod.ColaMisc.items.food.Cola;
import com.coladungeon.mod.Index;
import com.coladungeon.utils.EventBus;
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
        // 在游戏开始时给玩家一个可乐
        EventBus.register("Hero:created", (data) -> {
            new Cola().identify().collect();
            return null;
        });
    }
}
