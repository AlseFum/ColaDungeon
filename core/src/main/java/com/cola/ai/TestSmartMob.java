package com.cola.ai;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.watabou.utils.Random;

/**
 * 测试智能怪物，使用状态机控制行为
 * 这个怪物是一个简单的示例，展示如何使用状态机系统
 */
public class TestSmartMob extends StateMachineMob {

    // 自定义状态
    private static final String STATE_SEARCH = "Search";
    private static final String STATE_REST = "Rest";
    
    public TestSmartMob() {
        super();
        
        // 设置基本属性
        spriteClass = RatSprite.class;
        HT = HP = 10;
        defenseSkill = 3;
        
        EXP = 3;
        maxLvl = 5;
        
        // 添加额外状态
        stateMachine.addState(STATE_SEARCH, new SearchState());
        stateMachine.addState(STATE_REST, new RestState());
    }
    
    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 4);
    }
    
    @Override
    public int attackSkill(com.shatteredpixel.shatteredpixeldungeon.actors.Char target) {
        return 8;
    }
    
    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 1);
    }
    
    @Override
    protected void updateStateMachine() {
        // 调用父类的基本状态转换逻辑
        super.updateStateMachine();
        
        // 添加我们自己的特殊状态转换逻辑
        String currentState = stateMachine.getCurrentStateName();
        
        // 示例：随机进入休息状态
        if (HP < HT / 2 && !currentState.equals(STATE_REST) && Random.Int(10) == 0) {
            stateMachine.setCurrentState(STATE_REST);
        }
        
        // 示例：从休息状态恢复后，进入搜索状态
        if (currentState.equals(STATE_REST) && HP == HT) {
            stateMachine.setCurrentState(STATE_SEARCH);
        }
    }
    
    /**
     * 搜索状态 - 怪物在区域内积极搜索玩家
     */
    private class SearchState extends BaseState {
        
        private int searchDuration;
        
        public SearchState() {
            super(STATE_SEARCH);
        }
        
        @Override
        public void enter(com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob mob) {
            searchDuration = Random.IntRange(5, 10);
        }
        
        @Override
        public boolean update(com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob mob) {
            searchDuration--;
            
            // 随机移动，搜索玩家
            int direction = Random.Int(8);
            int[] dx = {-1, 0, 1, 0, -1, 1, 1, -1};
            int[] dy = {0, 1, 0, -1, 1, 1, -1, -1};
            
            int newX = (mob.pos % Dungeon.level.width()) + dx[direction];
            int newY = (mob.pos / Dungeon.level.width()) + dy[direction];
            int newPos = newX + newY * Dungeon.level.width();
            
            if (newPos >= 0 && newPos < Dungeon.level.length() && 
                Dungeon.level.passable[newPos] && 
                com.shatteredpixel.shatteredpixeldungeon.actors.Actor.findChar(newPos) == null) {
                
                mob.move(newPos);
                mob.sprite.move(mob.pos, newPos);
                mob.pos = newPos;
            }
            
            // 搜索时间结束后回到闲置状态
            return searchDuration > 0;
        }
    }
    
    /**
     * 休息状态 - 怪物在休息并恢复生命值
     */
    private class RestState extends BaseState {
        
        private int restDuration;
        
        public RestState() {
            super(STATE_REST);
        }
        
        @Override
        public void enter(com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob mob) {
            restDuration = Random.IntRange(5, 10);
            
            // 添加恢复效果
            Buff.affect(mob, com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing.class).setHeal(5, 1f, 0);
        }
        
        @Override
        public boolean update(com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob mob) {
            restDuration--;
            
            // 如果看到玩家，立即停止休息
            if (Dungeon.level.heroFOV[mob.pos]) {
                return false;
            }
            
            // 休息时间结束后回到闲置状态
            return restDuration > 0;
        }
    }
} 