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

package com.coladungeon.items.remains;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.particles.LeafParticle;
import com.coladungeon.levels.Level;
import com.coladungeon.levels.Terrain;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class BowFragment extends RemainsItem {

	{
		image = ItemSpriteSheet.BOW_FRAGMENT;
	}

	@Override
	protected void doEffect(Hero hero) {
		ArrayList<Integer> grassCells = new ArrayList<>();
		for (int i : PathFinder.NEIGHBOURS9){
			grassCells.add(hero.pos+i);
		}
		Random.shuffle(grassCells);
		for (int grassCell : grassCells){
			if (Dungeon.level.map[grassCell] == Terrain.EMPTY ||
					Dungeon.level.map[grassCell] == Terrain.EMBERS ||
					Dungeon.level.map[grassCell] == Terrain.EMPTY_DECO){
				Level.set(grassCell, Terrain.GRASS);
				GameScene.updateMap(grassCell);
			}
			CellEmitter.get(grassCell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
		}
		// 5 cells total
		int totalGrassCells = 5;
		while (grassCells.size() > totalGrassCells){
			grassCells.remove(0);
		}
		for (int grassCell : grassCells){
			int t = Dungeon.level.map[grassCell];
			if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
					|| t == Terrain.GRASS || t == Terrain.FURROWED_GRASS)
					&& Dungeon.level.plants.get(grassCell) == null){
				Level.set(grassCell, Terrain.HIGH_GRASS);
				GameScene.updateMap(grassCell);
			}
		}
		Dungeon.observe();
		Sample.INSTANCE.play(Assets.Sounds.PLANT);
	}
}
