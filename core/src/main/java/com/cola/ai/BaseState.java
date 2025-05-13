package com.cola.ai;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

/**
 * 状态基类，提供默认实现
 */
public abstract class BaseState implements State {
    
    protected String name;
    
    public BaseState(String name) {
        this.name = name;
    }
    
    @Override
    public void enter(Mob mob) {
        // 默认实现为空
    }
    
    @Override
    public boolean update(Mob mob) {
        // 默认继续执行
        return true;
    }
    
    @Override
    public void exit(Mob mob) {
        // 默认实现为空
    }
    
    @Override
    public String getName() {
        return name;
    }
} 