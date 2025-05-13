package com.cola;

import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class TrinketBag extends Bag {
    {
        image = ItemSpriteSheet.HOLSTER;
    }
    @Override
	public boolean canHold( Item item ) {
		if (item instanceof Trinket ){
			return super.canHold(item);
		} else {
			return false;
		}
	}

	public int capacity(){
		return 22;
	}
}
