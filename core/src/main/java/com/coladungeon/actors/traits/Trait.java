package com.coladungeon.actors.traits;

import com.coladungeon.messages.Messages;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 特质类，定义角色可拥有的各种特质
 * 使用动态类实现，而非枚举，以支持运行时添加新特质
 */
public class Trait {
    
    // 全局特质注册表
    private static final Map<String, Trait> ALL_TRAITS = new HashMap<>();
    
    // 特质类别
    public enum TraitCategory {
        PHYSICAL,   // 物理特质
        ELEMENTAL,  // 元素特质
        BEHAVIOR,   // 行为特质
        SPECIAL,    // 特殊特质
        MENTAL      // 心智特质
    }
    
    // 特质属性
    private String id;              // 唯一标识符
    private String name;            // 显示名称
    private String desc;            // 描述
    private TraitCategory category; // 类别
    
    /**
     * 创建一个新特质
     * @param id 特质唯一标识符
     * @param name 特质名称
     * @param desc 特质描述
     * @param category 特质类别
     */
    public Trait(String id, String name, String desc, TraitCategory category) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.category = category;
    }
    
    /**
     * 获取特质ID
     * @return 特质ID
     */
    public String id() {
        return id;
    }
    
    /**
     * 获取特质名称
     * @return 特质名称
     */
    public String name() {
        return name != null ? name : Messages.get(this, "name");
    }
    
    /**
     * 获取特质描述
     * @return 特质描述
     */
    public String desc() {
        return desc != null ? desc : Messages.get(this, "desc");
    }
    
    /**
     * 获取特质类别
     * @return 特质类别
     */
    public TraitCategory getCategory() {
        return category;
    }
    
    /**
     * 注册一个新特质到全局特质注册表
     * @param trait 要注册的特质
     * @return 注册的特质
     */
    public static Trait register(Trait trait) {
        ALL_TRAITS.put(trait.id, trait);
        return trait;
    }
    
    /**
     * 根据ID获取特质
     * @param id 特质ID
     * @return 特质实例，如果不存在则返回null
     */
    public static Trait get(String id) {
        return ALL_TRAITS.get(id);
    }
    
    /**
     * 获取所有注册的特质
     * @return 所有特质的列表
     */
    public static ArrayList<Trait> getAllTraits() {
        return new ArrayList<>(ALL_TRAITS.values());
    }
    
    /**
     * 获取特定类别的所有特质
     * @param category 特质类别
     * @return 该类别的所有特质
     */
    public static ArrayList<Trait> getTraitsByCategory(TraitCategory category) {
        ArrayList<Trait> result = new ArrayList<>();
        for (Trait trait : ALL_TRAITS.values()) {
            if (trait.category == category) {
                result.add(trait);
            }
        }
        return result;
    }
    
    /**
     * 移除一个特质从全局注册表
     * @param id 要移除的特质ID
     * @return 被移除的特质，如果不存在则返回null
     */
    public static Trait unregister(String id) {
        return ALL_TRAITS.remove(id);
    }
    
    // 保存和加载特质信息
    private static final String TRAIT_ID = "trait_id";
    private static final String TRAIT_VALUE = "trait_value";
    
    /**
     * 将特质信息存储到Bundle中
     * @param bundle 目标Bundle
     * @param key Bundle中的键
     * @param trait 要存储的特质
     * @param value 特质的值
     */
    public static void storeTrait(Bundle bundle, String key, Trait trait, float value) {
        Bundle traitBundle = new Bundle();
        traitBundle.put(TRAIT_ID, trait.id);
        traitBundle.put(TRAIT_VALUE, value);
        bundle.put(key, traitBundle);
    }
    
    /**
     * 从Bundle加载特质信息
     * @param bundle 源Bundle
     * @param key Bundle中的键
     * @return 加载的特质
     */
    public static Trait restoreTrait(Bundle bundle, String key) {
        Bundle traitBundle = bundle.getBundle(key);
        String traitId = traitBundle.getString(TRAIT_ID);
        Trait trait = get(traitId);
        return trait;
    }
    
    /**
     * 从Bundle加载特质值
     * @param bundle 源Bundle
     * @param key Bundle中的键
     * @return 特质的值
     */
    public static float restoreTraitValue(Bundle bundle, String key) {
        Bundle traitBundle = bundle.getBundle(key);
        return traitBundle.getFloat(TRAIT_VALUE);
    }
    
    /**
     * 将多个特质存储到Bundle中
     * @param bundle 目标Bundle
     * @param traits 要存储的特质列表
     * @param values 特质值的映射
     */
    public static void storeTraits(Bundle bundle, ArrayList<Trait> traits, Map<Trait, Float> values) {
        Bundle traitsBundle = new Bundle();
        for (int i = 0; i < traits.size(); i++) {
            Trait trait = traits.get(i);
            Bundle traitBundle = new Bundle();
            traitBundle.put(TRAIT_ID, trait.id);
            traitBundle.put(TRAIT_VALUE, values.getOrDefault(trait, 1.0f));
            traitsBundle.put(Integer.toString(i), traitBundle);
        }
        bundle.put("traits", traitsBundle);
    }
    
    /**
     * 从Bundle恢复多个特质
     * @param bundle 源Bundle
     * @param traits 特质列表（将被清空并填充）
     * @param values 特质值的映射（将被清空并填充）
     */
    public static void restoreTraits(Bundle bundle, ArrayList<Trait> traits, Map<Trait, Float> values) {
        traits.clear();
        values.clear();
        if (bundle.contains("traits")) {
            Bundle traitsBundle = bundle.getBundle("traits");
            for (String key : traitsBundle.getKeys()) {
                Bundle traitBundle = traitsBundle.getBundle(key);
                String traitId = traitBundle.getString(TRAIT_ID);
                Trait trait = get(traitId);
                if (trait != null) {
                    float value = traitBundle.getFloat(TRAIT_VALUE);
                    traits.add(trait);
                    values.put(trait, value);
                }
            }
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trait trait = (Trait) o;
        return Objects.equals(id, trait.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    // 初始化一些默认特质作为示例
    static {
        // 物理特质
        register(new Trait("resilient", "坚韧", "生命值增加，更能抵抗伤害", TraitCategory.PHYSICAL));
        register(new Trait("frail", "脆弱", "生命值减少，更容易受到伤害", TraitCategory.PHYSICAL));
        register(new Trait("strong", "强壮", "物理攻击造成更多伤害", TraitCategory.PHYSICAL));
        register(new Trait("weak", "虚弱", "物理攻击造成更少伤害", TraitCategory.PHYSICAL));
        register(new Trait("agile", "敏捷", "移动更快，闪避更高", TraitCategory.PHYSICAL));
        register(new Trait("clumsy", "笨拙", "移动更慢，闪避更低", TraitCategory.PHYSICAL));
        
        // 元素特质
        register(new Trait("fiery", "烈焰", "攻击附带火焰伤害", TraitCategory.ELEMENTAL));
        register(new Trait("cold", "冰寒", "攻击附带寒冰伤害", TraitCategory.ELEMENTAL));
        register(new Trait("shocking", "雷电", "攻击附带电击伤害", TraitCategory.ELEMENTAL));
        register(new Trait("toxic", "毒性", "攻击附带毒素伤害", TraitCategory.ELEMENTAL));
        
        // 行为特质
        register(new Trait("aggressive", "好斗", "主动攻击距离更远", TraitCategory.BEHAVIOR));
        register(new Trait("cautious", "谨慎", "更容易发现隐藏的危险", TraitCategory.BEHAVIOR));
        register(new Trait("protective", "护卫", "保护同类生物", TraitCategory.BEHAVIOR));
        register(new Trait("cowardly", "怯懦", "血量低时逃跑", TraitCategory.BEHAVIOR));
        
        // 特殊特质
        register(new Trait("regenerative", "再生", "随时间回复生命值", TraitCategory.SPECIAL));
        register(new Trait("thorny", "荆棘", "受到近战攻击时反弹伤害", TraitCategory.SPECIAL));
        register(new Trait("reflective", "反射", "有几率反射远程攻击", TraitCategory.SPECIAL));
        register(new Trait("vampiric", "吸血", "攻击时吸取生命值", TraitCategory.SPECIAL));
        register(new Trait("explosive", "爆炸", "死亡时爆炸", TraitCategory.SPECIAL));
        register(new Trait("stealthy", "潜行", "更难被发现", TraitCategory.SPECIAL));
        register(new Trait("giant", "巨大", "体型更大，力量更强", TraitCategory.SPECIAL));
        register(new Trait("tiny", "微小", "体型更小，更难被击中", TraitCategory.SPECIAL));
        register(new Trait("durable", "耐久", "减少受到的伤害", TraitCategory.SPECIAL));
        register(new Trait("ethereal", "虚幻", "部分物理攻击会穿过", TraitCategory.SPECIAL));
        
        // 心智特质
        register(new Trait("intelligent", "智慧", "可以使用物品和技能", TraitCategory.MENTAL));
        register(new Trait("cunning", "狡猾", "更善于寻找弱点攻击", TraitCategory.MENTAL));
        register(new Trait("ferocious", "凶猛", "血量低时攻击更强", TraitCategory.MENTAL));
        register(new Trait("venomous", "剧毒", "攻击造成强力毒素", TraitCategory.MENTAL));
        register(new Trait("corrupted", "腐化", "攻击有几率施加负面效果", TraitCategory.MENTAL));
        register(new Trait("blessed", "祝福", "行动更有效率", TraitCategory.MENTAL));
        register(new Trait("cursed", "诅咒", "行动更低效", TraitCategory.MENTAL));
    }
    
    /**
     * 创建并注册一个新特质
     * @param id 特质ID
     * @param name 特质名称
     * @param desc 特质描述
     * @param category 特质类别
     * @return 新创建的特质
     */
    public static Trait createTrait(String id, String name, String desc, TraitCategory category) {
        // 检查ID是否已存在
        if (ALL_TRAITS.containsKey(id)) {
            return ALL_TRAITS.get(id);
        }
        
        Trait trait = new Trait(id, name, desc, category);
        return register(trait);
    }
} 