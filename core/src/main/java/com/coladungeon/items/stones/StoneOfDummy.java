package com.coladungeon.items.stones;

import com.coladungeon.actors.mobs.Golem;
import com.coladungeon.actors.mobs.Rat;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
public class StoneOfDummy extends Runestone {
    {
        image = ItemSpriteSheet.STONE_HOLDER;
    }
    
    @Override
    protected void activate(int cell) {
        Rat rat = new Rat() {
            @Override
            protected boolean act() {
                spend(TICK);
                return true; // Prevent movement by only spending time
            }
            {
                defenseSkill=0;
            }
        };
        rat.pos = cell;
        rat.state = rat.PASSIVE;
        //FIXME
        // Dungeon.level.mobs.add(golem);
        // Actor.add(golem);
        GameScene.add(rat);
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