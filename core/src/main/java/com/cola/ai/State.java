package com.cola.ai;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

/**
 * 怪物AI状态接口
 * 定义了状态机中的单个状态应该具有的行为
 */
public interface State {
    
    /**
     * 进入该状态时的行为
     * @param mob 当前怪物实例
     */
    void enter(Mob mob);
    
    /**
     * 在该状态下的更新逻辑
     * @param mob 当前怪物实例
     * @return 是否应该继续执行该状态的逻辑
     */
    boolean update(Mob mob);
    
    /**
     * 退出状态时的行为
     * @param mob 当前怪物实例
     */
    void exit(Mob mob);
    
    /**
     * 获取状态名称，用于调试
     * @return 状态的名称
     */
    String getName();
} 