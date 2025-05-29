package com.coladungeon.items.bags;

import com.coladungeon.items.Item;
import com.coladungeon.items.weapon.ammo.Ammo;
import com.coladungeon.items.weapon.gun.Gun;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.items.Ankh;
public class AmmoHolder extends Bag {

    {
        image = ItemSpriteSheet.HOLSTER; // 使用枪套的图标
        
        unique = true;
    }

    @Override
    public boolean canHold(Item item) {
        if (super.canHold(item)) {
            return item instanceof Ammo || item instanceof Gun || item instanceof Ankh;
        }
        return false;
    }

    @Override
    public int capacity() {
        return 30; // 弹药和枪械的容量限制
    }

    @Override
    public String name() {
        return "弹药包";
    }

    @Override
    public String desc() {
        return "一个专门用于存放弹药和枪械的背包，可以有效地整理和携带这些物品。";
    }
} 