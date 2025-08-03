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
import com.coladungeon.utils.EventBus;
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
				EventBus.fire("ooze","level",Dungeon.level,"pos",pos);
			}
		}
	}
}
