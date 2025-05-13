package com.cola.ai;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

/**
 * 攻击状态：怪物近距离攻击玩家的行为
 */
public class AttackState extends BaseState {

    private int consecutiveAttacks = 0;
    private int maxConsecutiveAttacks;
    
    public AttackState() {
        super("Attack");
        this.maxConsecutiveAttacks = 3;
    }
    
    public AttackState(int maxConsecutiveAttacks) {
        super("Attack");
        this.maxConsecutiveAttacks = maxConsecutiveAttacks;
    }
    
    @Override
    public void enter(Mob mob) {
        consecutiveAttacks = 0;
    }
    
    @Override
    public boolean update(Mob mob) {
        Hero hero = Dungeon.hero;
        
        // 检查玩家是否在攻击范围内
        if (!Dungeon.level.adjacent(mob.pos, hero.pos)) {
            return false; // 不在攻击范围内，退出攻击状态
        }
        
        // 检查是否已经攻击太多次（某些怪物可能需要休息）
        if (consecutiveAttacks >= maxConsecutiveAttacks) {
            // 50%几率继续攻击，50%几率停止攻击
            if (Random.Int(2) == 0) {
                consecutiveAttacks = 0;
            } else {
                return false;
            }
        }
        
        // 执行攻击
        mob.attack(hero);
        consecutiveAttacks++;
        
        // 攻击后有一定几率改变位置（围绕玩家移动）
        if (Random.Int(3) == 0) {
            repositionAroundTarget(mob, hero.pos);
        }
        
        return true;
    }
    
    @Override
    public void exit(Mob mob) {
        // 退出攻击状态
        consecutiveAttacks = 0;
    }
    
    /**
     * 尝试在目标周围重新定位
     */
    private boolean repositionAroundTarget(Mob mob, int targetPos) {
        // 获取目标周围的位置
        int[] neighbors = PathFinder.NEIGHBOURS8;
        int[] validPositions = new int[8];
        int count = 0;
        
        for (int i = 0; i < neighbors.length; i++) {
            int newPos = targetPos + neighbors[i];
            
            if (newPos >= 0 && newPos < Dungeon.level.length() &&
                Dungeon.level.passable[newPos] &&
                Actor.findChar(newPos) == null) {
                
                validPositions[count++] = newPos;
            }
        }
        
        // 如果有有效位置，随机选择一个
        if (count > 0) {
            int newPos = validPositions[Random.Int(count)];
            mob.move(newPos);
            Actor.delayChar(mob, 1f / mob.speed());
            return true;
        }
        
        return false;
    }
} 