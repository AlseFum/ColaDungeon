package com.coladungeon.mod.ArknightsMod.sprites;

import com.coladungeon.sprites.MobSprite;
import com.watabou.noosa.TextureFilm;

public class BlockSprite extends MobSprite {
//这个sprite是特殊的占位符
	public BlockSprite() {
		super();

		texture( "cola/blocksprite.png" );

		TextureFilm frames = new TextureFilm( texture, 16, 16 );

		idle = new Animation( 5, true );
		idle.frames( frames, 0, 1, 0  );

		run = new Animation( 15, true );
		run.frames( frames, 0,1,2 );

		attack = new Animation( 12, false );
		attack.frames( frames,3,4,5);

		die = new Animation( 12, false );
		die.frames( frames, 6,7 );

		play( idle );
	}

	@Override
	public int blood() {
		return 0xFFFFEA80;
	}
}
