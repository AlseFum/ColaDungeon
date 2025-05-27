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
        image = ItemSpriteSheet.DARTS;
        stackable = true;
        defaultAction = AC_THROW;
    }

    private AmmoType type = AmmoType.NORMAL;
    
    public enum AmmoType {
        NORMAL("普通弹", 1.0f),
        PIERCING("穿甲弹", 1.2f),
        EXPLOSIVE("爆炸弹", 0.8f),
        INCENDIARY("燃烧弹", 0.9f),
        FROST("冰冻弹", 0.9f),
        POISON("毒气弹", 0.9f);
        
        private final String name;
        private final float damageMultiplier;
        
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
        switch (type) {
            case NORMAL:
                return "标准弹药，没有特殊效果。";
            case PIERCING:
                return "能够穿透敌人护甲的特殊弹药。";
            case EXPLOSIVE:
                return "击中目标时会产生爆炸的弹药。";
            case INCENDIARY:
                return "能够点燃目标的燃烧弹药。";
            case FROST:
                return "能够冰冻并减速目标的弹药。";
            case POISON:
                return "能够使目标中毒的毒气弹药。";
            default:
                return "未知类型的弹药。";
        }
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
        switch (type) {
            case NORMAL:
                return 5;
            case PIERCING:
                return 10;
            case EXPLOSIVE:
                return 15;
            case INCENDIARY:
            case FROST:
            case POISON:
                return 12;
            default:
                return 5;
        }
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
        try {
            String typeName = bundle.getString(TYPE);
            type = AmmoType.valueOf(typeName);
        } catch (Exception e) {
            type = AmmoType.NORMAL;
        }
    }

    public void setType(AmmoType type) {
        this.type = type;
    }
} 