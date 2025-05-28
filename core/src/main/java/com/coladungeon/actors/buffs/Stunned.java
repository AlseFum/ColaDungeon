package com.coladungeon.actors.buffs;

import com.coladungeon.actors.Char;
import com.coladungeon.messages.Messages;
import com.coladungeon.ui.BuffIndicator;

public class Stunned extends FlavourBuff {
    
    public static final float DURATION = 2f;
    
    {
        type = buffType.NEGATIVE;
        announced = true;
    }
    
    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            target.paralysed++;
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public void detach() {
        super.detach();
        if (target != null)
            target.paralysed--;
    }
    
    @Override
    public int icon() {
        return BuffIndicator.PARALYSIS;
    }
    
    @Override
    public String toString() {
        return Messages.get(this, "name");
    }
    
    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }
} 