package com.coladungeon.mod.ColaMisc;

import com.coladungeon.sprites.MobSprite;
import com.watabou.noosa.TextureFilm;

public class DummySprite extends MobSprite {
//这个sprite是特殊的占位符
	public DummySprite() {
		super();

		texture( "cola/crab.png" );

		TextureFilm frames = new TextureFilm( texture, 16, 16 );

		idle = new Animation( 5, true );
		idle.frames( frames, 0, 1, 0, 2 );

		run = new Animation( 15, true );
		run.frames( frames, 3, 4, 5, 6 );

		attack = new Animation( 12, false );
		attack.frames( frames, 7, 8, 9 );

		die = new Animation( 12, false );
		die.frames( frames, 10, 11, 12, 13 );

		play( idle );
	}

	@Override
	public int blood() {
		return 0xFFFFEA80;
	}
}
