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
        rat.HT=114514;
        rat.HP=114514;
        GameScene.add(rat);
    }
    
    @Override
    public String name() {
        return "Dummy魔石";
    }
    
    @Override
    public String desc() {
        return "这块石头会生成一个血量为114514的老鼠，它什么都不会做";
    }
} 