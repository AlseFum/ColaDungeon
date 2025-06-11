package com.coladungeon.mod.ArknightsMod;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.mobs.npcs.NPC;
import com.coladungeon.mod.ArknightsMod.NPC.Dummy;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
public class Operator extends NPC {

    {
        spriteClass = Dummy.DummySprite.class;
        
        HP = HT = 100;
        defenseSkill = 20;
        
        // 干员默认是盟友
        alignment = Alignment.ALLY;
    }
    public Branch branch=Branch.None;
    public Rarity rarity=Rarity.SExtra;
    public int cost=99;
    public static enum Rarity{
        //SX means X-stars. S1 means 1-star.
        //SExtra means Extra rarity. don't get it.
        S1(0),S2(0),S3(0),S4(2),S5(3),S6(6),SExtra(0);
        public int hope_cost;
        Rarity(int hopcost){
            this.hope_cost = hopcost;
        }
    }
    public String arrange = "Idle";
    @Override
    public String name() {
        return "干员";
    }
    
    @Override
    public String description() {
        return "罗德岛的精英干员";
    }
    
    @Override
    public String info() {
        String desc = super.info();
        
        // 添加arrange的描述
        desc += "\n\n当前任务：";
        switch (arrange) {
            case "Follow":
                desc += "跟随玩家";
                break;
            default:
                desc += "自由游荡";
                break;
        }
        
        return desc;
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
 
    
    // 保存和加载状态
    private static final String STATE = "state";
    private static final String TARGET = "target";
    
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("arrange", arrange);
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
        arrange = bundle.getString("arrange");
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
    public static enum Role{
        Vanguard,
        Supporter,
        Caster,
        Defender,
        Guard,
        Medic,
        Sniper,
        Specialist,
        None;
    }
    public static enum Branch{
        Core(Role.Caster),
        Splash(Role.Caster),
        Blast(Role.Caster),
        Chain(Role.Caster),
        MechAccord(Role.Caster),
        Phalanx(Role.Caster),
        Mystic(Role.Caster),
        None(Role.None),
        Pioneer(Role.Vanguard);

        public final Role r;
        Branch(){
            this.r = Role.None;
        }
        Branch(Role r){
            this.r = r;
        }
    }
    
    
}