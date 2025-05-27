/*
 * Cola Dungeon
 */

package com.coladungeon.items.weapon.ammo;

import com.coladungeon.sprites.ItemSpriteSheet;

public class ExplosiveAmmo extends Ammo {
    
    {
        image = ItemSpriteSheet.THROWING_STONE; // 使用投掷石头图标表示爆炸弹药
        stackable = true;
    }
    
    public ExplosiveAmmo() {
        super(AmmoType.EXPLOSIVE);
        quantity = 10; // 默认数量较少
    }
    
    @Override
    public String name() {
        return "爆炸弹药";
    }
    
    @Override
    public String desc() {
        return "击中目标时会产生爆炸，对周围1格范围内的敌人造成额外伤害。威力强大但数量有限。";
    }
} 