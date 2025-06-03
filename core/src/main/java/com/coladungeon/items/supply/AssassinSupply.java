package com.coladungeon.items.supply;

import com.coladungeon.items.weapon.melee.assassin.Dagger;
import com.coladungeon.items.weapon.melee.assassin.Dirk;
import com.coladungeon.items.weapon.melee.assassin.MirrorEdge;
import com.coladungeon.items.weapon.melee.assassin.PhantomClaw;
import com.coladungeon.items.weapon.melee.assassin.AssassinsBlade;

public class AssassinSupply extends Supply {
    public AssassinSupply() {
        super();
        name = "Assassin Supply";
        desc = "A supply for the assassin class.";
    }
    {
        put_in(Dagger.class);
        put_in(Dirk.class);
        put_in(MirrorEdge.class);
        put_in(PhantomClaw.class);
        put_in(AssassinsBlade.class);
    }
}
