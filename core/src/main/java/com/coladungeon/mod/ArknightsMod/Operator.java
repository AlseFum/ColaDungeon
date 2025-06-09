package com.coladungeon.mod.ArknightsMod;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Haste;
import com.coladungeon.actors.buffs.Invisibility;
import com.coladungeon.actors.mobs.npcs.NPC;
import com.coladungeon.items.weapon.melee.MeleeWeapon;
import com.coladungeon.messages.Messages;
import com.coladungeon.sprites.CharSprite;
import com.coladungeon.sprites.OperatorSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Operator extends NPC {

    {
        spriteClass = OperatorSprite.class;
        
        HP = HT = 100;
        defenseSkill = 20;
        
        // 干员默认是盟友
        alignment = Alignment.ALLY;
    }
    
    @Override
    public String name() {
        return "干员";
    }
    
    @Override
    public String description() {
        return "罗德岛的精英干员";
    }
    
    @Override
    protected boolean act() {
        // 干员会跟随玩家，但保持一定距离
        if (Dungeon.hero != null) {
            int target = Dungeon.hero.pos;
            if (distance(Dungeon.hero) > 2) {
                getCloser(target);
            }
        }
        return super.act();
    }
    
    @Override
    public int attackSkill(Char target) {
        return 20;
    }
    
    @Override
    public int damageRoll() {
        return Random.NormalIntRange(5, 15);
    }
    
    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 5);
    }
    
    // 干员特殊技能
    public void useSkill() {
        // 使用技能，例如加速
        Buff.affect(this, Haste.class, 5f);
        Buff.affect(this, Invisibility.class, 3f);
    }
    
    // 保存和加载状态
    private static final String STATE = "state";
    private static final String TARGET = "target";
    
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (state == SLEEPING) {
            bundle.put(STATE, "SLEEPING");
        } else if (state == WANDERING) {
            bundle.put(STATE, "WANDERING");
        } else if (state == HUNTING) {
            bundle.put(STATE, "HUNTING");
        } else if (state == FLEEING) {
            bundle.put(STATE, "FLEEING");
        } else if (state == PASSIVE) {
            bundle.put(STATE, "PASSIVE");
        }
        bundle.put(TARGET, target);
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        String stateStr = bundle.getString(STATE);
        if (stateStr.equals("SLEEPING")) {
            state = SLEEPING;
        } else if (stateStr.equals("WANDERING")) {
            state = WANDERING;
        } else if (stateStr.equals("HUNTING")) {
            state = HUNTING;
        } else if (stateStr.equals("FLEEING")) {
            state = FLEEING;
        } else if (stateStr.equals("PASSIVE")) {
            state = PASSIVE;
        }
        target = bundle.getInt(TARGET);
    }
    
    @Override
    public boolean interact(Char c) {
        // 与玩家交互
        if (c == Dungeon.hero) {
            // 这里可以添加对话或其他交互逻辑
            return true;
        }
        return super.interact(c);
    }
} 