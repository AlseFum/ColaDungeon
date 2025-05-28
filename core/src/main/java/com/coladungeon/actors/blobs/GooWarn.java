package com.coladungeon.actors.blobs;

import com.coladungeon.Dungeon;
import com.coladungeon.effects.BlobEmitter;
import com.coladungeon.levels.Level;
import com.coladungeon.messages.Messages;
import com.coladungeon.sprites.GooSprite;

public class GooWarn extends Blob {

	//cosmetic blob, previously used for Goo's pump up attack (that's now handled by Goo's sprite)
	// but is still used as a visual indicator for Arcane bombs

	{
		//this one needs to act just before the Goo
		actPriority = MOB_PRIO + 1;
	}

	protected int pos;

	@Override
	protected void evolve() {

		int cell;

		for (int i = area.left; i < area.right; i++){
			for (int j = area.top; j < area.bottom; j++){
				cell = i + j*Dungeon.level.width();
				off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;

				if (off[cell] > 0) {
					volume += off[cell];
				}
			}
		}

	}

	//to prevent multiple arcane bombs from visually stacking their effects
	public void seed(Level level, int cell, int amount ) {
		if (cur == null) cur = new int[level.length()];
		if (off == null) off = new int[cur.length];

		int toAdd = amount - cur[cell];
		if (toAdd > 0){
			cur[cell] += toAdd;
			volume += toAdd;
		}

		area.union(cell%level.width(), cell/level.width());
	}

	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.pour(GooSprite.GooParticle.FACTORY, 0.03f );
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}

