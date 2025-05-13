package com.cola.ai;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;

/**
 * 使用状态机的怪物基类
 * 这个类扩展了标准的Mob类，使用状态机来控制行为
 */
public class StateMachineMob extends Mob {

    // 状态机
    protected StateMachine stateMachine;
    
    // 默认状态名称
    protected static final String STATE_IDLE = "Idle";
    protected static final String STATE_CHASE = "Chase";
    protected static final String STATE_ATTACK = "Attack";
    protected static final String STATE_FLEE = "Flee";
    
    public StateMachineMob() {
        super();
        // 创建状态机
        stateMachine = new StateMachine(this);
        
        // 添加默认状态
        stateMachine.addState(STATE_IDLE, new IdleState());
        stateMachine.addState(STATE_CHASE, new ChaseState());
        stateMachine.addState(STATE_ATTACK, new AttackState());
        
        // 默认的初始状态是闲置
        stateMachine.setCurrentState(STATE_IDLE);
    }
    
    @Override
    protected boolean act() {
        // 更新状态机
        updateStateMachine();
        
        // 如果状态机处理了行为，则返回true
        stateMachine.update();
        
        // 由于状态机已经处理了行为，我们只需要安排下一次行动
        spend(TICK);
        return true;
    }
    
    /**
     * 更新状态机状态
     * 这个方法根据当前情况决定是否切换状态
     */
    protected void updateStateMachine() {
        Hero hero = Dungeon.hero;
        String currentState = stateMachine.getCurrentStateName();
        
        // 根据当前状态和环境决定是否切换状态
        if (currentState.equals(STATE_IDLE)) {
            // 如果看到玩家，切换到追踪状态
            if (Dungeon.level.heroFOV[pos]) {
                stateMachine.setCurrentState(STATE_CHASE);
            }
        } else if (currentState.equals(STATE_CHASE)) {
            // 如果玩家在攻击范围内，切换到攻击状态
            if (Dungeon.level.adjacent(pos, hero.pos)) {
                stateMachine.setCurrentState(STATE_ATTACK);
            }
            // 如果看不到玩家，回到闲置状态
            else if (!Dungeon.level.heroFOV[pos]) {
                stateMachine.setCurrentState(STATE_IDLE);
            }
        } else if (currentState.equals(STATE_ATTACK)) {
            // 如果玩家不在攻击范围内，切换到追踪状态
            if (!Dungeon.level.adjacent(pos, hero.pos)) {
                stateMachine.setCurrentState(STATE_CHASE);
            }
        }
    }
    
    /**
     * 添加自定义状态
     * @param name 状态名称
     * @param state 状态实例
     */
    public void addState(String name, State state) {
        stateMachine.addState(name, state);
    }
    
    /**
     * 手动切换状态
     * @param stateName 状态名称
     */
    public void setState(String stateName) {
        stateMachine.setCurrentState(stateName);
    }
    
    /**
     * 获取当前状态名称
     * @return 当前状态名称
     */
    public String getCurrentStateName() {
        return stateMachine.getCurrentStateName();
    }
} 