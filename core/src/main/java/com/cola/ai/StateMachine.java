package com.cola.ai;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

import java.util.HashMap;
import java.util.Map;

/**
 * 怪物AI状态机
 * 管理不同状态之间的转换和当前状态的更新
 */
public class StateMachine {
    
    // 当前状态
    private State currentState;
    
    // 全局状态，每次更新都会检查
    private State globalState;
    
    // 前一个状态，用于实现返回之前状态的功能
    private State previousState;
    
    // 所有可用状态的映射表
    private Map<String, State> states = new HashMap<>();
    
    // 状态机所属的怪物
    private Mob owner;
    
    /**
     * 构造函数
     * @param owner 状态机所属的怪物
     */
    public StateMachine(Mob owner) {
        this.owner = owner;
    }
    
    /**
     * 添加状态到状态机
     * @param name 状态名称
     * @param state 状态实例
     */
    public void addState(String name, State state) {
        states.put(name, state);
    }
    
    /**
     * 设置当前状态
     * @param stateName 状态名称
     */
    public void setCurrentState(String stateName) {
        State state = states.get(stateName);
        if (state != null) {
            if (currentState != null) {
                currentState.exit(owner);
            }
            previousState = currentState;
            currentState = state;
            currentState.enter(owner);
        }
    }
    
    /**
     * 设置全局状态
     * @param stateName 状态名称
     */
    public void setGlobalState(String stateName) {
        State state = states.get(stateName);
        if (state != null) {
            if (globalState != null) {
                globalState.exit(owner);
            }
            globalState = state;
            globalState.enter(owner);
        }
    }
    
    /**
     * 切换到前一个状态
     */
    public void revertToPreviousState() {
        if (previousState != null) {
            if (currentState != null) {
                currentState.exit(owner);
            }
            currentState = previousState;
            previousState = null;
            currentState.enter(owner);
        }
    }
    
    /**
     * 更新状态机，执行当前状态和全局状态的逻辑
     */
    public void update() {
        // 首先检查全局状态
        if (globalState != null) {
            globalState.update(owner);
        }
        
        // 然后更新当前状态
        if (currentState != null) {
            currentState.update(owner);
        }
    }
    
    /**
     * 检查当前状态
     * @param stateName 状态名称
     * @return 是否是当前状态
     */
    public boolean isInState(String stateName) {
        if (currentState == null) return false;
        return currentState.getName().equals(stateName);
    }
    
    /**
     * 获取当前状态名称
     * @return 当前状态的名称
     */
    public String getCurrentStateName() {
        return currentState != null ? currentState.getName() : "None";
    }
    
    /**
     * 获取全局状态名称
     * @return 全局状态的名称
     */
    public String getGlobalStateName() {
        return globalState != null ? globalState.getName() : "None";
    }
    
    /**
     * 获取前一个状态名称
     * @return 前一个状态的名称
     */
    public String getPreviousStateName() {
        return previousState != null ? previousState.getName() : "None";
    }
} 