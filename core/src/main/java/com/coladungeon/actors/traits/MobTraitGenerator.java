package com.coladungeon.actors.traits;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.ChampionEnemy;
import com.coladungeon.actors.mobs.Mob;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于为怪物随机分配特质的生成器
 */
public class MobTraitGenerator {
    
    // 每个怪物最多拥有的特质数量
    private static final int MAX_TRAITS_PER_MOB = 3;
    
    // 怪物获得特质的基础概率(0-1)
    private static final float BASE_TRAIT_CHANCE = 0.3f;
    
    // 根据深度增加特质概率的因子
    private static final float DEPTH_FACTOR = 0.02f;
    
    // 特质值范围
    private static final float MIN_TRAIT_VALUE = 0.5f;
    private static final float MAX_TRAIT_VALUE = 2.0f;
    
    // 特质类别的权重
    private static final Map<Trait.TraitCategory, Float> CATEGORY_WEIGHTS = new HashMap<>();
    
    static {
        CATEGORY_WEIGHTS.put(Trait.TraitCategory.PHYSICAL, 1.0f);
        CATEGORY_WEIGHTS.put(Trait.TraitCategory.ELEMENTAL, 0.7f);
        CATEGORY_WEIGHTS.put(Trait.TraitCategory.BEHAVIOR, 0.6f);
        CATEGORY_WEIGHTS.put(Trait.TraitCategory.SPECIAL, 0.4f);
        CATEGORY_WEIGHTS.put(Trait.TraitCategory.MENTAL, 0.5f);
    }
    
    /**
     * 为指定的怪物随机分配特质
     * @param mob 目标怪物
     */
    public static void assignTraits(Mob mob) {
        // 检查怪物是否已经有特质
        if (mob.traits() != null && !mob.traits().getTraits().isEmpty()) {
            return;
        }
        
        // 检查是否是Boss或者精英怪物，如果是则使用不同的特质分配策略
        if (isBoss(mob)) {
            assignBossTraits(mob);
            return;
        } else if (isElite(mob)) {
            assignEliteTraits(mob);
            return;
        }
        
        // 计算特质概率
        float traitChance = calculateTraitChance(Dungeon.depth);
        
        // 随机决定特质数量
        int traitCount = 0;
        while (traitCount < MAX_TRAITS_PER_MOB && Random.Float() < traitChance) {
            traitCount++;
            // 每多一个特质，获得下一个的概率降低
            traitChance *= 0.6f;
        }
        
        // 确保至少有一个特质
        if (traitCount == 0) {
            traitCount = 1;
        }
        
        // 为怪物分配特质
        assignRandomTraits(mob, traitCount);
    }
    
    /**
     * 为Boss怪物分配特质，Boss怪物拥有更多和更强的特质
     * @param mob Boss怪物
     */
    private static void assignBossTraits(Mob mob) {
        // Boss怪物必定拥有3-5个特质
        int traitCount = Random.IntRange(3, 5);
        
        // Boss特质值的范围更高
        float minValue = 1.0f;
        float maxValue = 3.0f;
        
        // 为Boss分配特质
        ArrayList<Trait> availableTraits = Trait.getAllTraits();
        
        for (int i = 0; i < traitCount; i++) {
            // 选择一个特质类别，Boss更倾向于获得物理和特殊特质
            Trait.TraitCategory category;
            float roll = Random.Float();
            if (roll < 0.4f) {
                category = Trait.TraitCategory.PHYSICAL;
            } else if (roll < 0.6f) {
                category = Trait.TraitCategory.SPECIAL;
            } else if (roll < 0.8f) {
                category = Trait.TraitCategory.ELEMENTAL;
            } else if (roll < 0.9f) {
                category = Trait.TraitCategory.MENTAL;
            } else {
                category = Trait.TraitCategory.BEHAVIOR;
            }
            
            // 从该类别中选择一个特质
            ArrayList<Trait> categoryTraits = new ArrayList<>();
            for (Trait trait : availableTraits) {
                if (trait.getCategory() == category) {
                    categoryTraits.add(trait);
                }
            }
            
            if (categoryTraits.isEmpty()) continue;
            
            // 随机选择一个特质
            Trait selectedTrait = Random.element(categoryTraits);
            
            // 如果Boss已经有这个特质，跳过
            if (mob.hasTrait(selectedTrait)) {
                i--; // 重试
                continue;
            }
            
            // 生成特质值
            float traitValue = Random.Float(minValue, maxValue);
            
            // 为Boss添加特质
            mob.addTrait(selectedTrait, traitValue);
        }
    }
    
    /**
     * 为精英怪物分配特质，精英怪物拥有2-3个特质
     * @param mob 精英怪物
     */
    private static void assignEliteTraits(Mob mob) {
        // 精英怪物必定拥有2-3个特质
        int traitCount = Random.IntRange(2, 3);
        
        // 精英特质值的范围也比普通怪物高
        float minValue = 0.8f;
        float maxValue = 2.5f;
        
        // 为精英怪物分配特质
        ArrayList<Trait> availableTraits = Trait.getAllTraits();
        
        for (int i = 0; i < traitCount; i++) {
            // 选择一个特质类别
            Trait.TraitCategory category = selectRandomCategory();
            
            // 从该类别中选择一个特质
            ArrayList<Trait> categoryTraits = new ArrayList<>();
            for (Trait trait : availableTraits) {
                if (trait.getCategory() == category) {
                    categoryTraits.add(trait);
                }
            }
            
            if (categoryTraits.isEmpty()) continue;
            
            // 随机选择一个特质
            Trait selectedTrait = Random.element(categoryTraits);
            
            // 如果精英怪物已经有这个特质，跳过
            if (mob.hasTrait(selectedTrait)) {
                i--; // 重试
                continue;
            }
            
            // 生成特质值
            float traitValue = Random.Float(minValue, maxValue);
            
            // 为精英怪物添加特质
            mob.addTrait(selectedTrait, traitValue);
        }
    }
    
    /**
     * 判断一个怪物是否是Boss
     * @param mob 要检查的怪物
     * @return 是否是Boss
     */
    private static boolean isBoss(Mob mob) {
        return mob.properties().contains(Char.Property.BOSS);
    }
    
    /**
     * 判断一个怪物是否是精英
     * @param mob 要检查的怪物
     * @return 是否是精英
     */
    private static boolean isElite(Mob mob) {
        return mob.properties().contains(Char.Property.MINIBOSS) || 
               mob.buff(ChampionEnemy.class) != null;
    }
    
    /**
     * 根据当前深度计算怪物获得特质的基础概率
     * @param depth 当前深度
     * @return 特质概率
     */
    private static float calculateTraitChance(int depth) {
        return BASE_TRAIT_CHANCE + (depth - 1) * DEPTH_FACTOR;
    }
    
    /**
     * 为怪物分配指定数量的随机特质
     * @param mob 目标怪物
     * @param count 特质数量
     */
    private static void assignRandomTraits(Mob mob, int count) {
        // 获取所有可用特质
        ArrayList<Trait> availableTraits = Trait.getAllTraits();
        
        for (int i = 0; i < count; i++) {
            // 选择一个特质类别
            Trait.TraitCategory category = selectRandomCategory();
            
            // 从该类别中选择一个特质
            ArrayList<Trait> categoryTraits = new ArrayList<>();
            for (Trait trait : availableTraits) {
                if (trait.getCategory() == category) {
                    categoryTraits.add(trait);
                }
            }
            
            if (categoryTraits.isEmpty()) continue;
            
            // 随机选择一个特质
            Trait selectedTrait = Random.element(categoryTraits);
            
            // 如果怪物已经有这个特质，跳过
            if (mob.hasTrait(selectedTrait)) {
                i--; // 重试
                continue;
            }
            
            // 生成特质值
            float traitValue = Random.Float(MIN_TRAIT_VALUE, MAX_TRAIT_VALUE);
            
            // 为怪物添加特质
            mob.addTrait(selectedTrait, traitValue);
        }
    }
    
    /**
     * 根据权重随机选择一个特质类别
     * @return 选择的特质类别
     */
    private static Trait.TraitCategory selectRandomCategory() {
        float totalWeight = 0;
        for (float weight : CATEGORY_WEIGHTS.values()) {
            totalWeight += weight;
        }
        
        float roll = Random.Float() * totalWeight;
        float currentWeight = 0;
        
        for (Map.Entry<Trait.TraitCategory, Float> entry : CATEGORY_WEIGHTS.entrySet()) {
            currentWeight += entry.getValue();
            if (roll < currentWeight) {
                return entry.getKey();
            }
        }
        
        // 默认返回物理特质
        return Trait.TraitCategory.PHYSICAL;
    }
} 