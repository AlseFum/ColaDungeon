package com.coladungeon.mod.ColaMisc;

import com.coladungeon.actors.Char;
import com.coladungeon.actors.mobs.Mob;
import com.watabou.utils.Random;

public class dummy extends Mob {

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
}
