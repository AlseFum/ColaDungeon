/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.coladungeon.items.scrolls.exotic;

import com.coladungeon.Dungeon;
import com.coladungeon.items.scrolls.ScrollOfTeleportation;
import com.coladungeon.levels.Level;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.InterlevelScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.Game;

public class ScrollOfPassage extends ExoticScroll {
	
	{
		icon = ItemSpriteSheet.Icons.SCROLL_PASSAGE;
	}
	
	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
		identify();
		readAnimation();
		
		if (!Dungeon.interfloorTeleportAllowed()) {
			
			GLog.w( Messages.get(ScrollOfTeleportation.class, "no_tele") );
			return;
			
		}

		Level.beforeTransition();
		InterlevelScene.mode = InterlevelScene.Mode.RETURN;
		InterlevelScene.returnDepth = Math.max(1, (Dungeon.depth - 1 - (Dungeon.depth-2)%5));
		InterlevelScene.returnBranch = 0;
		InterlevelScene.returnPos = -1;
		Game.switchScene( InterlevelScene.class );
	}
}
