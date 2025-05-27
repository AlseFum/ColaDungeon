/*
 * Cola Dungeon
 */

package com.coladungeon.items.weapon.ammo;

import com.coladungeon.sprites.ItemSpriteSheet;

public class NormalAmmo extends Ammo {
    
    {
        image = ItemSpriteSheet.THROWING_KNIFE; // 使用投掷匕首图标表示普通弹药
        stackable = true;
    }
    
    public NormalAmmo() {
        super(AmmoType.NORMAL);
        quantity = 20; // 默认数量较多
    }
    
    @Override
    public String name() {
        return "普通弹药";
    }
    
    @Override
    public String desc() {
        return "标准弹药，适用于大多数枪械。没有特殊效果，但数量充足。";
    }
} 