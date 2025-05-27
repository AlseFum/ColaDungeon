/*
 * Cola Dungeon
 */

package com.coladungeon.items.weapon.ammo;

import com.coladungeon.items.Item;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class Ammo extends Item {

    private static final int DEFAULT_MAX_STACK = 99;
    
    {
        image = ItemSpriteSheet.THROWING_KNIFE; // 暂时使用飞刀的图标
        stackable = true;
        defaultAction = AC_THROW;
    }

    private AmmoType type = AmmoType.NORMAL;
    
    public enum AmmoType {
        NORMAL("普通弹药", 1.0f),
        PIERCING("穿甲弹", 1.2f),
        EXPLOSIVE("爆炸弹", 1.5f),
        INCENDIARY("燃烧弹", 1.3f),
        FROST("冰冻弹", 1.3f),
        POISON("毒气弹", 1.2f);
        
        private String name;
        private float damageMultiplier;
        
        AmmoType(String name, float damageMultiplier) {
            this.name = name;
            this.damageMultiplier = damageMultiplier;
        }
        
        public String getName() {
            return name;
        }
        
        public float getDamageMultiplier() {
            return damageMultiplier;
        }
    }
    
    public Ammo() {
        this(AmmoType.NORMAL);
    }
    
    public Ammo(AmmoType type) {
        this.type = type;
        quantity = 1;
    }
    
    @Override
    public String name() {
        return type.getName();
    }
    
    @Override
    public String desc() {
        String desc = "一种" + type.getName() + "。\n\n";
        
        switch (type) {
            case NORMAL:
                desc += "标准弹药，没有特殊效果。";
                break;
            case PIERCING:
                desc += "能够穿透敌人的装甲，造成额外伤害。";
                break;
            case EXPLOSIVE:
                desc += "击中目标时会产生爆炸，对周围造成范围伤害。";
                break;
            case INCENDIARY:
                desc += "击中目标时会点燃目标，造成持续燃烧伤害。";
                break;
            case FROST:
                desc += "击中目标时会冻结目标，降低其移动和攻击速度。";
                break;
            case POISON:
                desc += "击中目标时会释放毒气，造成持续毒素伤害。";
                break;
        }
        
        return desc;
    }
    
    @Override
    public boolean isUpgradable() {
        return false;
    }
    
    @Override
    public boolean isIdentified() {
        return true;
    }
    
    public AmmoType getType() {
        return type;
    }
    
    public float getDamageMultiplier() {
        return type.getDamageMultiplier();
    }
    
    @Override
    public int price() {
        return 10 * quantity;
    }
    
    public int value() {
        return 2 * quantity;
    }
    
    @Override
    public int getMaxStack() {
        return DEFAULT_MAX_STACK;
    }
    
    private static final String TYPE = "type";
    
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TYPE, type);
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        type = AmmoType.valueOf(bundle.getString(TYPE));
    }
} 