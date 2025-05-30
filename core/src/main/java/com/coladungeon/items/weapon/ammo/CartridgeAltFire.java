package com.coladungeon.items.weapon.ammo;
import com.coladungeon.actors.hero.Hero;
public class CartridgeAltFire extends Cartridge {
    public CartridgeAltFire() {
        super();
    }
    public void fire(Hero hero, int targetPos){
        System.out.println("alt fire");
    }
}
