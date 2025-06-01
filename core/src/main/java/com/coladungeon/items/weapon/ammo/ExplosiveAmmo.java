package com.coladungeon.items.weapon.ammo;

public class ExplosiveAmmo extends Ammo {
    public ExplosiveAmmo() {
        super();
        this.cartridge = new Cartridge(10, CartridgeEffect.Explosive);
    }
}
