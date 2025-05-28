package com.coladungeon.levels.traps;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Ooze;
import com.coladungeon.effects.Splash;
import com.coladungeon.items.food.Food;
import com.coladungeon.items.food.OozedFood;
import com.coladungeon.items.Item;
import com.watabou.utils.PathFinder;

public class OozeTrap extends Trap {

	{
		color = GREEN;
		shape = DOTS;
	}

	@Override
	public void activate() {
		for( int i : PathFinder.NEIGHBOURS9) {
			if (!Dungeon.level.solid[pos + i]) {
				Splash.at( pos + i, 0x000000, 5);
				
				// Handle characters
				Char ch = Actor.findChar( pos + i );
				if (ch != null && !ch.flying){
					Buff.affect(ch, Ooze.class).set( Ooze.DURATION );
				}

				// Handle items on the ground
				if (Dungeon.level.heaps.get(pos + i) != null) {
					for (Item item : Dungeon.level.heaps.get(pos + i).items.toArray(new Item[0])) {
						// Check if the item is a food item
						if (item instanceof Food && !(item instanceof OozedFood)) {
							// Remove the original food
							Dungeon.level.heaps.get(pos + i).items.remove(item);
							
							// Create an OozedFood version
							OozedFood oozedFood = new OozedFood(item);
							
							// Add the OozedFood back to the heap
							Dungeon.level.heaps.get(pos + i).items.add(oozedFood);
						}
					}
				}
			}
		}
	}
}
