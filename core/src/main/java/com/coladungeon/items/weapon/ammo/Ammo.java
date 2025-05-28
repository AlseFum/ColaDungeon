package com.coladungeon.items.weapon.ammo;

import com.coladungeon.items.Item;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class Ammo extends Item {

    private static final int DEFAULT_MAX_STACK = 999;

    {
        image = ItemSpriteSheet.DARTS;
        stackable = true;
        defaultAction = AC_THROW;
    }

    public enum Calibre {
        SMALL("小口径"),
        MEDIUM("中口径"),
        LARGE("大口径"),
        PELLET("散弹"),
        SLUG("独头弹"),
        SPIKE("长钉"),
        GRENADE("榴弹");

        private final String name;

        Calibre(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Capacity {
        BURN("燃烧"),
        FROST("冰冻"),
        POISON("毒素"),
        EXPLOSIVE("爆炸"),
        PIERCING("穿透"),
        NONE("无");

        private final String name;

        Capacity(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    private AmmoType type = AmmoType.NORMAL;

    //compatiability
    public enum AmmoType {
        NORMAL(Calibre.MEDIUM, Capacity.NONE),
        PIERCING(Calibre.MEDIUM, Capacity.PIERCING),
        EXPLOSIVE(Calibre.MEDIUM, Capacity.EXPLOSIVE),
        INCENDIARY(Calibre.MEDIUM, Capacity.BURN),
        FROST(Calibre.MEDIUM, Capacity.FROST),
        POISON(Calibre.MEDIUM, Capacity.POISON);

        public Calibre cal;
        public Capacity cap;

        AmmoType(Calibre cal, Capacity cap) {
            this.cal = cal;
            this.cap = cap;
        }

        public String getName() {
            return cal.getName() + cap.getName();
        }

        public float getDamageMultiplier() {
            switch (this) {
                case PIERCING:
                    return 1.2f;
                case EXPLOSIVE:
                    return 1.3f;
                case INCENDIARY:
                    return 1.1f;
                case FROST:
                    return 1.0f;
                case POISON:
                    return 1.0f;
                case NORMAL:
                default:
                    return 1.0f;
            }
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
        return type.cal.getName() + " " + type.cap.getName() + "弹药";
    }

    @Override
    public String desc() {
        return "由" + type.cal.getName() + "口径的枪械使用，" + type.cap.getName() + "弹药。";
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
        //compatiablity
        return 1.0f;
    }

    @Override
    public int price() {
        return 10 * quantity;
    }

    @Override
    public int value() {
        //compatiability
        return 10;
    }

    @Override
    public int getMaxStack() {
        return DEFAULT_MAX_STACK;
    }

    private static final String TYPE = "type";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TYPE, type.name());
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        type = AmmoType.valueOf(bundle.getString(TYPE));
    }

    public void setType(AmmoType type) {
        this.type = type;
    }
}
