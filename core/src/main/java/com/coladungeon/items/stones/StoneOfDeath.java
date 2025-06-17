package com.coladungeon.items.stones;

import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.messages.Messages;

public class StoneOfDeath extends Runestone {
    {
        image = ItemSpriteSheet.STONE_HOLDER;
    }
    
    @Override
    protected void activate(int cell) {
        Char ch = Actor.findChar(cell);
        if (ch != null && ch.isAlive()) {
            ch.die(this);
        }
    }
    
    @Override
    public String name() {
        return Messages.get(this, "name");
    }
    
    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }
} 