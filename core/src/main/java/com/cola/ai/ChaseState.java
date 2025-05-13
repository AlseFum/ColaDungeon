package com.cola.ai;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

/**
 * 追踪状态：怪物看到玩家后追踪的行为
 */
public class ChaseState extends BaseState {

    private int targetPos;
    private int lostSight;
    private int maxLostSight;
    
    public ChaseState() {
        super("Chase");
        this.maxLostSight = 5;
    }
    
    public ChaseState(int maxLostSight) {
        super("Chase");
        this.maxLostSight = maxLostSight;
    }
    
    @Override
    public void enter(Mob mob) {
        targetPos = Dungeon.hero.pos;
        lostSight = 0;
    }
    
    @Override
    public boolean update(Mob mob) {
        // 检查是否能看到玩家
        Hero hero = Dungeon.hero;
        
        // 如果玩家可见，更新目标位置
        if (Dungeon.level.heroFOV[mob.pos]) {
            targetPos = hero.pos;
            lostSight = 0;
        } else {
            lostSight++;
            
            // 如果太久没看到玩家，停止追踪
            if (lostSight >= maxLostSight) {
                return false;
            }
        }
        
        // 尝试移动到玩家位置
        if (mob.pos == targetPos) {
            // 已经到达玩家位置，但没看到玩家
            lostSight = maxLostSight;
            return false;
        }
        
        // 计算到玩家的下一步
        int step = getNextStepToTarget(mob, targetPos);
        
        if (step != -1) {
            Char ch = Actor.findChar(step);
            
            if (ch instanceof Hero) {
                // 如果下一步是玩家，攻击
                mob.attack(hero);
            } else if (ch == null) {
                // 如果路径可行，移动
                mob.move(step);
                // 由于无法直接访问spend方法，我们使用delay代替
                // delay相当于消耗一定时间
                Actor.delayChar(mob, 1f / mob.speed());
            }
            
            return true;
        } else {
            // 无法找到路径
            lostSight++;
            return true;
        }
    }
    
    @Override
    public void exit(Mob mob) {
        // 清理追踪状态
        lostSight = 0;
    }
    
    /**
     * 计算到目标的下一步
     */
    private int getNextStepToTarget(Mob mob, int target) {
        if (mob.pos == target) {
            return -1;
        }
        
        // 简化的寻路算法
        int[] neighbors = PathFinder.NEIGHBOURS8;
        int bestDist = Integer.MAX_VALUE;
        int bestStep = -1;
        
        for (int i = 0; i < neighbors.length; i++) {
            int step = mob.pos + neighbors[i];
            
            if (step >= 0 && step < Dungeon.level.length() &&
                Dungeon.level.passable[step] &&
                Actor.findChar(step) == null) {
                
                int dist = Dungeon.level.distance(step, target);
                if (dist < bestDist) {
                    bestDist = dist;
                    bestStep = step;
                }
            }
        }
        
        return bestStep;
    }
} 