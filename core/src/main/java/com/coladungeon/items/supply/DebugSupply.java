package com.coladungeon.items.supply;

import com.coladungeon.items.DivineAnkh;
import com.coladungeon.items.Torch;
import com.coladungeon.items.EnergyCrystal;
import com.coladungeon.items.potions.PotionOfHealing;
import com.coladungeon.items.potions.PotionOfInvisibility;
import com.coladungeon.items.potions.PotionOfStrength;
import com.coladungeon.items.potions.exotic.PotionOfShroudingFog;
import com.coladungeon.items.scrolls.ScrollOfIdentify;
import com.coladungeon.items.scrolls.ScrollOfUpgrade;
import com.coladungeon.items.stones.StoneOfDeath;
import com.coladungeon.items.stones.StoneOfDummy;
import com.coladungeon.items.stones.StoneOfDungeonTravel;
import com.coladungeon.items.stones.StoneOfGeneration;
import com.coladungeon.items.weapon.SummonerStaff;
public class DebugSupply extends Supply {
    public DebugSupply() {
        super();
        name = "Debug Supply";
        desc = "A supply for debugging purposes.";
        put_in(PotionOfHealing.class, 100);
        put_in(ScrollOfIdentify.class, 100);
        put_in(SummonerStaff.class, 100);
        put_in(PotionOfStrength.class, 100);
        put_in(StoneOfGeneration.class, 300);
        put_in(StoneOfDungeonTravel.class, 300);
        put_in(ScrollOfUpgrade.class, 100);
        put_in(StoneOfDummy.class, 300);
        put_in(StoneOfDeath.class, 300);
        put_in(PotionOfInvisibility.class, 500);
        put_in(PotionOfShroudingFog.class, 200);
        put_in(DivineAnkh.class, 1);
        put_in(Torch.class, 100);
        put_in(EnergyCrystal.class, 100);
    }
}
