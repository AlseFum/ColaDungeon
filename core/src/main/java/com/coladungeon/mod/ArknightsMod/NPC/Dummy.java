package com.coladungeon.mod.ArknightsMod.NPC;

import com.coladungeon.actors.Char;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.sprites.MobSprite;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Random;

public class Dummy extends Mob {

    {
        spriteClass = DummySprite.class;
        
        HP = HT = 114514;
        defenseSkill = 5;
        
        EXP = 5;
        maxLvl = 10;
        
        loot = null;
        lootChance = 0f;
    }
    @Override
    public String name(){
        return "训练假人";
    }

    @Override
    public boolean act() {
        // 训练假人只站在原地，不进行任何行动
        spend(1f);
        return true;
    }
    
    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 1, 5 );
    }
    
    @Override
    public int attackSkill( Char target ) {
        return 10;
    }
    
    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 2);
    }
    
    @Override
    public void die( Object cause ) {
        super.die( cause );
    }

    public static class DummySprite extends MobSprite {
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
}
