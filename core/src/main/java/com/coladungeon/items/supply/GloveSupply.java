package com.coladungeon.items.supply;

import com.coladungeon.items.weapon.melee.knuckles.Knuckles;
import com.coladungeon.items.weapon.melee.Gauntlet;
import com.coladungeon.items.weapon.melee.Sai;
import com.coladungeon.items.weapon.melee.Gloves;
import com.coladungeon.items.weapon.melee.Scimitar;
public class GloveSupply extends Supply {
    public GloveSupply() {
        super();
        this.put_in(Knuckles.class)
            .put_in(Gauntlet.class)
            .put_in(Sai.class)
            .put_in(Gloves.class)
            .put_in(Scimitar.class)
            .name("GloveSupply")
            .desc("一个装满了拳套的包，可以从中获取到各种拳套。");
    }
}
