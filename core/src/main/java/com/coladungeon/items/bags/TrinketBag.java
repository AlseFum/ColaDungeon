package com.coladungeon.items.bags;

import com.coladungeon.items.Item;
import com.coladungeon.items.trinkets.Trinket;
import com.coladungeon.sprites.ItemSpriteSheet;
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
