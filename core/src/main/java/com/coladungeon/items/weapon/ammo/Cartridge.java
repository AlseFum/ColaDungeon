package com.coladungeon.items.weapon.ammo;

import com.watabou.utils.Bundle;
import com.watabou.utils.Bundlable;

public class Cartridge implements Bundlable{
    //power is to be used as initial value to onHit(.value)
    public int power=10;
    public CartridgeEffect onHit;
    public CartridgeType cartridgeType=CartridgeType.MEDIUM;

    // 默认构造函数
    public Cartridge() {
        this.onHit = CartridgeEffect.Normal;
    }

    public Cartridge(int power){
        this.power=power;
    }
    public Cartridge(int power, CartridgeEffect onHit) {
        this.power = power;
        this.onHit = onHit != null ? onHit : CartridgeEffect.Normal;
    }
    public Cartridge(int power, CartridgeEffect onHit, CartridgeType cartridgeType) {
        this.power = power;
        this.onHit = onHit != null ? onHit : CartridgeEffect.Normal;
        this.cartridgeType = cartridgeType;
    }

    // 存储到Bundle
    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put("power", power);
        bundle.put("onHit", onHit.name());
        bundle.put("cartridgeType", cartridgeType.name());
    }

    // 从Bundle恢复
    @Override
    public void restoreFromBundle(Bundle bundle) {
        power = bundle.getInt("power");
        
        // 尝试从名称恢复CartridgeEffect
        String effectName = bundle.getString("onHit");
        try {
            onHit = CartridgeEffect.valueOf(effectName);
        } catch (IllegalArgumentException e) {
            // 如果无法恢复，使用标准弹
            onHit = CartridgeEffect.Normal;
            System.out.println("无法恢复CartridgeEffect，使用标准弹");
        }
        String cartridgeTypeName = bundle.getString("cartridgeType");
        try {
            cartridgeType = CartridgeType.valueOf(cartridgeTypeName);
        } catch (IllegalArgumentException e) {
            cartridgeType = CartridgeType.MEDIUM;
        }
    }
    public  enum CartridgeType{
        SMALL,
        MEDIUM,
        LARGE,
        SPIKE,
        GRENADE,
        BUCKSHOT,
        SLUG,
        LASER,
        FLUID
    }
}
