package com.coladungeon.mod.ArknightsMod;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.mod.ArknightsMod.category.OperatorConst;
import com.coladungeon.mod.ArknightsMod.sprites.BlockSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Operator extends Mob {

    {
        EXP = 0;

        alignment = Alignment.NEUTRAL;
        state = PASSIVE;
        spriteClass = BlockSprite.class;

        HP = HT = 100;
        defenseSkill = 20;

        // 干员默认是盟友
        alignment = Alignment.ALLY;
    }
    public OperatorConst.Branch branch = OperatorConst.Branch.None;
    public OperatorConst.Rarity rarity = OperatorConst.Rarity.SExtra;
    public int cost = 99;

    public static String ARRANGE_IDLE = "arrange_idle";
    public static String ARRANGE_FOLLOW = "arrange_follow";
    public static String ARRANGE_CHASE = "arrange_chase";
    public static String ARRANGE_GUARD = "arrange_guard";
    public static String ARRANGE_RANGE = "arrange_range";
    public static String ARRANGE_SWEEP = "arrange_sweep";
    public String arrange = ARRANGE_IDLE;

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

    @Override
    public boolean act() {
        // 初始化视野数组
        if (fieldOfView == null) {
            fieldOfView = new boolean[Dungeon.level.length()];
        }
        
        // 更新视野
        Dungeon.level.updateFieldOfView(this, fieldOfView);
        
        // 根据arrange状态切换AI状态
        switch (arrange) {
            case "arrange_idle":
                state = PASSIVE;
                break;
            case "arrange_follow":
                state = HUNTING;
                target = Dungeon.hero.pos;
                break;
            case "arrange_chase":
                state = HUNTING;
                // 寻找最近的敌人作为目标
                if (enemy == null || !enemy.isAlive()) {
                    enemy = chooseEnemy();
                }
                if (enemy != null) {
                    target = enemy.pos;
                }
                break;
            case "arrange_guard":
                state = WANDERING;
                // 在当前位置附近巡逻
                if (target == -1) {
                    target = Dungeon.level.randomDestination(this);
                }
                break;
            case "arrange_range":
                state = HUNTING;
                // 保持距离进行远程攻击
                if (enemy == null || !enemy.isAlive()) {
                    enemy = chooseEnemy();
                }
                if (enemy != null) {
                    target = Dungeon.level.randomDestination(this);
                }
                break;
            case "arrange_sweep":
                state = HUNTING;
                // 主动搜索并攻击敌人
                if (enemy == null || !enemy.isAlive()) {
                    enemy = chooseEnemy();
                }
                if (enemy != null) {
                    target = enemy.pos;
                }
                break;
        }
        
        return super.act();
    }

}
