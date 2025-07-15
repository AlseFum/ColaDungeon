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
        EventBus.on("Hero:created", (data) -> {
            new Cola().identify().collect();
            return null;
        });
    }
}
