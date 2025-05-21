package com.coladungeon.actors.traits;

import com.coladungeon.actors.Char;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 特质集合，管理角色的各种特质
 */
public class TraitSet implements Bundlable {
    
    private Char owner;
    private ArrayList<Trait> traits = new ArrayList<>();
    private Map<Trait, Float> traitValues = new HashMap<>(); // 特质的强度值
    
    // 互斥特质集
    private static final Map<String, Set<String>> MUTUALLY_EXCLUSIVE = new HashMap<>();
    
    static {
        // 初始化互斥特质集
        HashSet<String> physicalOppositesA = new HashSet<>();
        physicalOppositesA.add("resilient");
        physicalOppositesA.add("frail");
        
        HashSet<String> physicalOppositesB = new HashSet<>();
        physicalOppositesB.add("strong");
        physicalOppositesB.add("weak");
        
        HashSet<String> physicalOppositesC = new HashSet<>();
        physicalOppositesC.add("agile");
        physicalOppositesC.add("clumsy");
        
        HashSet<String> behaviorOppositesA = new HashSet<>();
        behaviorOppositesA.add("brave");
        behaviorOppositesA.add("cowardly");
        
        HashSet<String> behaviorOppositesB = new HashSet<>();
        behaviorOppositesB.add("aggressive");
        behaviorOppositesB.add("cautious");
        
        HashSet<String> specialOppositesA = new HashSet<>();
        specialOppositesA.add("cursed");
        specialOppositesA.add("blessed");
        
        HashSet<String> mentalOppositesA = new HashSet<>();
        mentalOppositesA.add("intelligent");
        mentalOppositesA.add("mindless");
        
        // 将互斥特质添加到映射中
        for (String t : physicalOppositesA) MUTUALLY_EXCLUSIVE.put(t, physicalOppositesA);
        for (String t : physicalOppositesB) MUTUALLY_EXCLUSIVE.put(t, physicalOppositesB);
        for (String t : physicalOppositesC) MUTUALLY_EXCLUSIVE.put(t, physicalOppositesC);
        for (String t : behaviorOppositesA) MUTUALLY_EXCLUSIVE.put(t, behaviorOppositesA);
        for (String t : behaviorOppositesB) MUTUALLY_EXCLUSIVE.put(t, behaviorOppositesB);
        for (String t : specialOppositesA) MUTUALLY_EXCLUSIVE.put(t, specialOppositesA);
        for (String t : mentalOppositesA) MUTUALLY_EXCLUSIVE.put(t, mentalOppositesA);
    }
    
    public TraitSet(Char owner) {
        this.owner = owner;
    }
    
    /**
     * 添加一个新特质
     * @param trait 要添加的特质
     * @param value 特质的强度值
     * @return 是否成功添加
     */
    public boolean add(Trait trait, float value) {
        if (trait == null) return false;
        
        if (!traits.contains(trait)) {
            traits.add(trait);
        }
        traitValues.put(trait, value);
        
        return true;
    }
    
    /**
     * 添加一个新特质，使用默认强度值
     * @param trait 要添加的特质
     * @return 是否成功添加
     */
    public boolean add(Trait trait) {
        return add(trait, 1.0f);
    }
    
    /**
     * 添加一个特质通过ID
     * @param traitId 特质ID
     * @param value 特质的强度值
     * @return 是否成功添加
     */
    public boolean add(String traitId, float value) {
        Trait trait = Trait.get(traitId);
        if (trait != null) {
            return add(trait, value);
        }
        return false;
    }
    
    /**
     * 添加一个特质通过ID，使用默认强度值
     * @param traitId 特质ID
     * @return 是否成功添加
     */
    public boolean add(String traitId) {
        return add(traitId, 1.0f);
    }
    
    /**
     * 移除一个特质
     * @param trait 要移除的特质
     * @return 是否成功移除
     */
    public boolean remove(Trait trait) {
        boolean result = traits.remove(trait);
        if (result) {
            traitValues.remove(trait);
        }
        return result;
    }
    
    /**
     * 移除一个特质通过ID
     * @param traitId 特质ID
     * @return 是否成功移除
     */
    public boolean remove(String traitId) {
        Trait trait = Trait.get(traitId);
        if (trait != null) {
            return remove(trait);
        }
        return false;
    }
    
    /**
     * 检查是否具有某个特质
     * @param trait 要检查的特质
     * @return 是否拥有该特质
     */
    public boolean has(Trait trait) {
        return traits.contains(trait);
    }
    
    /**
     * 检查是否具有某个特质通过ID
     * @param traitId 特质ID
     * @return 是否拥有该特质
     */
    public boolean has(String traitId) {
        Trait trait = Trait.get(traitId);
        return trait != null && has(trait);
    }
    
    /**
     * 获取特质的强度值
     * @param trait 特质
     * @return 强度值，如果没有该特质则返回0
     */
    public float getValue(Trait trait) {
        return traitValues.getOrDefault(trait, 0f);
    }
    
    /**
     * 获取特质的强度值通过ID
     * @param traitId 特质ID
     * @return 强度值，如果没有该特质则返回0
     */
    public float getValue(String traitId) {
        Trait trait = Trait.get(traitId);
        return trait != null ? getValue(trait) : 0f;
    }
    
    /**
     * 设置特质的强度值
     * @param trait 特质
     * @param value 强度值
     * @return 是否成功设置
     */
    public boolean setValue(Trait trait, float value) {
        if (has(trait)) {
            traitValues.put(trait, value);
            return true;
        }
        return false;
    }
    
    /**
     * 设置特质的强度值通过ID
     * @param traitId 特质ID
     * @param value 强度值
     * @return 是否成功设置
     */
    public boolean setValue(String traitId, float value) {
        Trait trait = Trait.get(traitId);
        return trait != null && setValue(trait, value);
    }
    
    /**
     * 获取所有特质
     * @return 特质列表
     */
    public ArrayList<Trait> getTraits() {
        ArrayList<Trait> result = new ArrayList<>(traits);
        return result;
    }
    
    /**
     * 获取特定类别的特质
     * @param category 特质类别
     * @return 该类别的特质列表
     */
    public ArrayList<Trait> getTraitsByCategory(Trait.TraitCategory category) {
        ArrayList<Trait> result = new ArrayList<>();
        for (Trait trait : traits) {
            if (trait.getCategory() == category) {
                result.add(trait);
            }
        }
        return result;
    }
    
    /**
     * 清除所有特质
     */
    public void clear() {
        traits.clear();
        traitValues.clear();
    }
    
    // 保存和加载特质集
    private static final String TRAITS_DATA = "traits_data";
    
    @Override
    public void storeInBundle(Bundle bundle) {
        Trait.storeTraits(bundle, traits, traitValues);
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        Trait.restoreTraits(bundle, traits, traitValues);
    }
    
    /**
     * 获取特质集合的所有者
     * @return 所有者
     */
    public Char getOwner() {
        return owner;
    }
    
    /**
     * 批量添加多个特质
     * @param traitIds 特质ID数组
     * @return 成功添加的特质数量
     */
    public int addTraits(String... traitIds) {
        int count = 0;
        for (String id : traitIds) {
            if (add(id)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 批量添加多个特质和对应的强度值
     * @param traitsWithValues 特质ID与强度值的映射
     * @return 成功添加的特质数量
     */
    public int addTraitsWithValues(Map<String, Float> traitsWithValues) {
        int count = 0;
        for (Map.Entry<String, Float> entry : traitsWithValues.entrySet()) {
            if (add(entry.getKey(), entry.getValue())) {
                count++;
            }
        }
        return count;
    }
} 