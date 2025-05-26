/*
 * Cola Dungeon
 * Copyright (C) 2022-2024 Cola Dungeon Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.coladungeon.utils;

import java.util.function.Function;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.mobs.Mob;

/**
 * 示例类，演示如何使用EventBus监听英雄事件
 * 此类可以实例化后注册英雄相关事件的监听器
 */
public class HeroEventListener {
    
    private boolean enabled = true;
    private boolean debugLogging = false;
    
    /**
     * 创建并注册一个监听器实例
     */
    public static HeroEventListener register() {
        return new HeroEventListener();
    }
    
    /**
     * 构造函数 - 自动注册所有事件处理器
     */
    public HeroEventListener() {
        registerListeners();
    }
    
    /**
     * 启用或禁用事件处理
     */
    public HeroEventListener setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    /**
     * 启用或禁用调试日志
     */
    public HeroEventListener setDebugLogging(boolean debugLogging) {
        this.debugLogging = debugLogging;
        return this;
    }
    
    /**
     * 注册所有事件监听器到EventBus
     */
    private void registerListeners() {
        // 注册伤害事件监听器
        EventBus.register(Hero.EVENT_HERO_DAMAGE, (Function<Object, Object>) obj -> {
            if (!enabled) return null;
            
            // 使用EventData处理事件
            EventBus.EventData event = (EventBus.EventData) obj;
            
            Hero hero = event.get("hero");
            if (hero != null) {
                onHeroDamage(
                    hero,
                    event.getOr("damage", 0),
                    event.getOr("source", null),
                    event.getOr("previousHealth", 0),
                    event.getOr("currentHealth", 0)
                );
            }
            
            return null;
        });
        
        // 注册移动事件监听器
        EventBus.register(Hero.EVENT_HERO_MOVE, (Function<Object, Object>) obj -> {
            if (!enabled) return null;
            
            // 使用EventData处理事件
            EventBus.EventData event = (EventBus.EventData) obj;
            
            Hero hero = event.get("hero");
            if (hero != null) {
                onHeroMove(
                    hero,
                    event.getOr("fromPosition", -1),
                    event.getOr("toPosition", -1),
                    event.getOr("travelling", false)
                );
            }
            
            return null;
        });
        
        // 注册攻击事件监听器
        EventBus.register(Hero.EVENT_HERO_ATTACKED, (Function<Object, Object>) obj -> {
            if (!enabled) return null;
            
            // 使用EventData处理事件
            EventBus.EventData event = (EventBus.EventData) obj;
            
            Hero hero = event.get("hero");
            if (hero != null) {
                onHeroAttack(
                    hero,
                    event.getOr("target", null),
                    event.getOr("hit", false),
                    event.getOr("wasEnemy", false)
                );
            }
            
            return null;
        });
        
        // 注册升级事件监听器
        EventBus.register(Hero.EVENT_HERO_LEVEL_UP, (Function<Object, Object>) obj -> {
            if (!enabled) return null;
            
            // 使用EventData处理事件
            EventBus.EventData event = (EventBus.EventData) obj;
            
            Hero hero = event.get("hero");
            if (hero != null) {
                onHeroLevelUp(
                    hero,
                    event.getOr("level", 1)
                );
            }
            
            return null;
        });
    }
    
    /**
     * 处理英雄伤害事件
     */
    private void onHeroDamage(Hero hero, int damage, Object source, int previousHealth, int currentHealth) {
        // 示例：记录重大伤害
        if (damage > hero.HT / 4) {
            // 承受高伤害 - 记录严重伤害
            if (debugLogging) {
                GLog.n("严重伤害: %d! 剩余生命值: %d", damage, currentHealth);
            }
            
            // 对于重大伤害（总生命值50%或更多），始终记录，无论是否开启调试
            if (damage >= hero.HT / 2) {
                GLog.n("受到致命伤害: %d!", damage);
            }
        } else if (debugLogging) {
            // 常规伤害记录
            GLog.i("受到伤害: %d, 剩余生命值: %d", damage, currentHealth);
        }
    }
    
    /**
     * 处理英雄移动事件
     */
    private void onHeroMove(Hero hero, int fromPosition, int toPosition, boolean travelling) {
        // 示例：追踪英雄进入某种地形类型
        int terrain = Dungeon.level.map[toPosition];
        
        if (debugLogging) {
            GLog.i("英雄移动: (%d → %d)", fromPosition, toPosition);
        }
        
        // 警告危险地形（示例实现）
        switch (terrain) {
            case com.coladungeon.levels.Terrain.CHASM:
                GLog.w("警告: 你正在靠近深渊!");
                break;
            case com.coladungeon.levels.Terrain.TRAP:
                GLog.w("警告: 你正在靠近陷阱!");
                break;
        }
    }
    
    /**
     * 处理英雄攻击事件
     */
    private void onHeroAttack(Hero hero, Object target, boolean hit, boolean wasEnemy) {
        // 示例：追踪成功命中敌人
        if (hit && target instanceof Mob) {
            Mob mob = (Mob) target;
            if (debugLogging) {
                GLog.p("攻击 %s: %s", mob.name(), hit ? "命中" : "未命中");
            }
            
            // 记录关键攻击
            if (hit && mob.HP < mob.HT / 4) {
                GLog.p("致命一击! %s已经濒临死亡!", mob.name());
            }
        }
    }
    
    /**
     * 处理英雄升级事件
     */
    private void onHeroLevelUp(Hero hero, int level) {
        // 始终记录升级，它们足够重要
        GLog.p("恭喜升级到 %d 级!", level);
        
        // 示例：在特定级别里程碑提供奖励
        if (level % 5 == 0) {
            GLog.h("你达到了里程碑等级 %d!", level);
        }
    }
} 