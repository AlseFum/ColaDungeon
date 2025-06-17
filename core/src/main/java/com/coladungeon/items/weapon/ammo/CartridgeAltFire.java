package com.coladungeon.items.weapon.ammo;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.weapon.gun.Gun;
public class CartridgeAltFire extends Cartridge {
    public CartridgeAltFire() {
        super();
    }
    public void fire(Hero hero, int targetPos){
        System.out.println("alt fire");
    }
    public void fire(Hero hero, int targetPos, CartridgeAltFire catf){
        System.out.println("alt fire");
    }
     public void fire(Hero hero, int targetPos, CartridgeAltFire catf, Gun gun){
        System.out.println("alt fire");
    }
}
