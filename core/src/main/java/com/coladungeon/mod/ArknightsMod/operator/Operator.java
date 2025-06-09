package com.coladungeon.mod.ArknightsMod.operator;

import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.mod.ArknightsMod.DummySprite;

public class Operator extends Mob {

    {
        HP = HT = 1;
        EXP = 0;

        alignment = Alignment.NEUTRAL;
        state = PASSIVE;
        sprite=new DummySprite();
    }

    @Override
    protected boolean act() {

        return super.act();
    }

    @Override
    public void beckon(int cell) {
    }
}
