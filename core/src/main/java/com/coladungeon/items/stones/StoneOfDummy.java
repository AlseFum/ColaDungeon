package com.coladungeon.items.stones;

import com.coladungeon.actors.mobs.Golem;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.messages.Messages;
import com.coladungeon.sprites.GolemSprite;
import com.coladungeon.scenes.GameScene;
public class StoneOfDummy extends Runestone {
    {
        image = ItemSpriteSheet.STONE_HOLDER;
    }
    
    @Override
    protected void activate(int cell) {
        Golem golem = new Golem() {
            @Override
            protected boolean act() {
                spend(TICK);
                return true; // Prevent movement by only spending time
            }
        };
        golem.pos = cell;
        golem.state = golem.PASSIVE;
        //FIXME
        // Dungeon.level.mobs.add(golem);
        // Actor.add(golem);
        GameScene.add(golem);
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