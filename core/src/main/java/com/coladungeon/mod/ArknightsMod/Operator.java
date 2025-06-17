package com.coladungeon.mod.ArknightsMod;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.actors.buffs.Invisibility;
import com.coladungeon.windows.WndOptions;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.coladungeon.mod.ArknightsMod.sprites.BlockSprite;
import com.coladungeon.mod.ArknightsMod.category.OperatorConst;

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

    public static final String ARRANGE_IDLE = "arrange_idle";
    public static final String ARRANGE_FOLLOW = "arrange_follow";
    public static final String ARRANGE_CHASE = "arrange_chase";
    public static final String ARRANGE_GUARD = "arrange_guard";
    public static final String ARRANGE_RANGE = "arrange_range";
    public static final String ARRANGE_SWEEP = "arrange_sweep";
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
    public boolean canInteract(Char c) {
        return true;
    }

    @Override
    public boolean interact(Char c) {
        if (c != Dungeon.hero) {
            return true;
        }

        Game.runOnRenderThread(() -> {
            GameScene.show(new WndOptions(sprite(),
                    "调整部署",
                    "选择部署模式：",
                    "自由游荡",
                    "跟随玩家",
                    "追击敌人",
                    "守卫区域",
                    "远程攻击",
                    "扫荡区域",
                    "撤离",
                    "取消") {
                @Override
                protected void onSelect(int index) {
                    switch (index) {
                        case 0:
                            arrange = ARRANGE_IDLE;
                            break;
                        case 1:
                            arrange = ARRANGE_FOLLOW;
                            break;
                        case 2:
                            arrange = ARRANGE_CHASE;
                            break;
                        case 3:
                            arrange = ARRANGE_GUARD;
                            break;
                        case 4:
                            arrange = ARRANGE_RANGE;
                            break;
                        case 5:
                            arrange = ARRANGE_SWEEP;
                            break;
                        case 6:
                            // 撤离
                            die(null);
                            break;
                    }
                }
            });
        });

        return true;
    }

    @Override
    public boolean act() {
        super.charAct();

        boolean justAlerted = alerted;
        alerted = false;

        if (justAlerted) {
            sprite.showAlert();
        } else {
            sprite.hideAlert();
            sprite.hideLost();
        }

        if (paralysed > 0) {
            enemySeen = false;
            spend(TICK);
            return true;
        }

        // 调用新方法处理状态选择
        updateState();

        boolean enemyInFOV = enemy != null
                && enemy.isAlive()
                && fieldOfView[enemy.pos]
                && enemy.invisible <= 0;
        boolean result = state.act(enemyInFOV, justAlerted);

        return result;
    }

    // 新增方法处理状态选择逻辑
    private void updateState() {
        enemy = chooseEnemy();
        // 根据 arrange 状态切换 AI 状态
        switch (arrange) {
            case ARRANGE_IDLE:
                // 自由游荡模式：随机移动，不主动攻击
                state = WANDERING;
                if (target == -1) {
                    target = Dungeon.level.randomDestination(this);
                }
                break;

            case ARRANGE_FOLLOW:
                // 跟随模式：始终跟随玩家，保持2格距离
                state = follow;
                break;

            case ARRANGE_CHASE:
                // 追击模式：主动寻找并追击敌人
                if (enemy != null) {
                    state = HUNTING;
                    target = enemy.pos;
                } else {
                    state = WANDERING;
                    if (target == -1) {
                        target = Dungeon.level.randomDestination(this);
                    }
                }
                break;

            case ARRANGE_GUARD:
                // 守卫模式：在指定区域巡逻，发现敌人时追击
                if (enemy != null) {
                    state = HUNTING;
                    target = enemy.pos;
                } else {
                    state = WANDERING;
                    // 在当前位置5格范围内随机移动
                    if (target == -1 || Dungeon.level.distance(pos, target) > 5) {
                        target = Dungeon.level.randomDestination(this);
                    }
                }
                break;

            case ARRANGE_RANGE:
                // 远程模式：保持距离进行远程攻击
                if (enemy != null) {
                    state = HUNTING;
                    // 保持3-5格距离
                    if (Dungeon.level.distance(pos, enemy.pos) < 3) {
                        // 太近了，后退
                        int newPos = Dungeon.level.randomDestination(this);
                        if (newPos != -1 && Dungeon.level.distance(newPos, enemy.pos) > Dungeon.level.distance(pos, enemy.pos)) {
                            target = newPos;
                        }
                    } else if (Dungeon.level.distance(pos, enemy.pos) > 5) {
                        // 太远了，接近
                        target = enemy.pos;
                    } else {
                        // 在合适距离，保持位置
                        target = -1;
                    }
                } else {
                    state = WANDERING;
                    if (target == -1) {
                        target = Dungeon.level.randomDestination(this);
                    }
                }
                break;

            case ARRANGE_SWEEP:
                // 扫荡模式：主动搜索并清除区域内的敌人
                if (enemy != null) {
                    state = HUNTING;
                    target = enemy.pos;
                } else {
                    state = WANDERING;
                    // 在未探索区域移动
                    if (target == -1 || !Dungeon.level.visited[target]) {
                        target = Dungeon.level.randomDestination(this);
                    }
                }
                break;
        }
    }

    @Override

    protected boolean doAttack(Char enemy) {
        attack(enemy);
        Invisibility.dispel(this);
        spend(attackDelay());
        return true;
    }
    public AiState follow = (boolean enemyInFOV, boolean justAlerted) -> {
        if (Dungeon.level.distance(pos, Dungeon.hero.pos) > 2) {
            target = Dungeon.hero.pos;

        } else {
            target = -1;
        }
        GLog.i("跟随中：from " + pos + " to " + Dungeon.hero.pos);
        GLog.i("target is:" + target);

        int oldPos = pos;
        if (target != -1) {
            if (getCloser(target)) {
                moveSprite(oldPos, pos);
            }
        }

        // 确保每次行动都消耗时间
        spend(1f);
        return true;

    };
}
