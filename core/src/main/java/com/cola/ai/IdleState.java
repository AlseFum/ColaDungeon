package com.cola.ai;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.utils.Random;

/**
 * 闲置状态：怪物在没有目标时的行为
 */
public class IdleState extends BaseState {

    private int waitTime;
    private int maxWaitTime;
    
    public IdleState() {
        super("Idle");
        this.maxWaitTime = 10;
    }
    
    public IdleState(int maxWaitTime) {
        super("Idle");
        this.maxWaitTime = maxWaitTime;
    }
    
    @Override
    public void enter(Mob mob) {
        waitTime = Random.IntRange(5, maxWaitTime);
    }
    
    @Override
    public boolean update(Mob mob) {
        // 检查是否发现玩家
        if (Dungeon.level.heroFOV[mob.pos]) {
            return false; // 返回false以允许状态机切换到其他状态
        }
        
        // 随机移动或待机
        waitTime--;
        
        if (waitTime <= 0) {
            // 随机移动
            int[] dirX = {-1, 0, 1, 0, -1, 1, 1, -1};
            int[] dirY = {0, 1, 0, -1, 1, 1, -1, -1};
            
            int dir = Random.Int(8);
            int newX = (mob.pos % Dungeon.level.width()) + dirX[dir];
            int newY = (mob.pos / Dungeon.level.width()) + dirY[dir];
            int newPos = newX + newY * Dungeon.level.width();
            
            if (newPos >= 0 && newPos < Dungeon.level.length() && 
                Dungeon.level.passable[newPos] && 
                Actor.findChar(newPos) == null) {
                
                mob.move(newPos);
                mob.sprite.move(mob.pos, newPos);
                mob.pos = newPos;
            }
            
            // 重置等待时间
            waitTime = Random.IntRange(5, maxWaitTime);
        }
        
        return true;
    }
    
    @Override
    public void exit(Mob mob) {
        // 退出闲置状态时无需特殊处理
    }
} 