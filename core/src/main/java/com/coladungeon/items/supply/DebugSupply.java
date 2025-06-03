package com.coladungeon.items.supply;

import com.coladungeon.items.potions.PotionOfHealing;
import com.coladungeon.items.potions.PotionOfInvisibility;
import com.coladungeon.items.potions.exotic.PotionOfShroudingFog;
import com.coladungeon.items.scrolls.ScrollOfIdentify;
import com.coladungeon.items.scrolls.ScrollOfMagicMapping;
import com.coladungeon.items.scrolls.ScrollOfUpgrade;
import com.coladungeon.items.stones.StoneOfBlast;
import com.coladungeon.items.stones.StoneOfDeath;
import com.coladungeon.items.stones.StoneOfDummy;
import com.coladungeon.items.stones.StoneOfDungeonTravel;
import com.coladungeon.items.stones.StoneOfGeneration;
import com.coladungeon.items.weapon.SummonerStaff;
import com.coladungeon.items.weapon.ammo.BreakingDawn;

public class DebugSupply extends Supply {
    public DebugSupply() {
        super();
        name = "Debug Supply";
        desc = "A supply for debugging purposes.";
        put_in(PotionOfHealing.class, 10);
        put_in(ScrollOfIdentify.class, 10);
        put_in(SummonerStaff.class, 1);
        put_in(BreakingDawn.class, 5);
        put_in(StoneOfGeneration.class, 3);
        put_in(StoneOfDungeonTravel.class, 3);
        put_in(ScrollOfUpgrade.class, 10);
        put_in(StoneOfDummy.class, 3);
        put_in(StoneOfDeath.class, 3);
        put_in(StoneOfBlast.class, 3);
        put_in(ScrollOfMagicMapping.class, 10);
        put_in(PotionOfInvisibility.class, 5);
        put_in(PotionOfShroudingFog.class, 2);
    }
}
