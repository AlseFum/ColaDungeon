package com.coladungeon.items.supply;

import com.coladungeon.items.bags.AmmoHolder;
import com.coladungeon.items.weapon.ammo.Ammo;
import com.coladungeon.items.weapon.ammo.ExplosiveAmmo;
import com.coladungeon.items.weapon.chakram.Chakram;
import com.coladungeon.items.weapon.gun.GrenadeLauncher;
import com.coladungeon.items.weapon.gun.HandGun;
import com.coladungeon.items.weapon.gun.Rifle;
import com.coladungeon.items.weapon.gun.Shotgun;
import com.coladungeon.items.weapon.gun.SniperGun;
public class GunSupply extends Supply {
    public GunSupply() {
        super();
        this.put_in(Shotgun.class)
            .put_in(SniperGun.class)
            .put_in(HandGun.class)
            .put_in(Rifle.class)
            .put_in(GrenadeLauncher.class)
            .put_in(Chakram.class)
            .put_in(AmmoHolder.class)
            .put_in(Ammo.class,1145)
            .put_in(ExplosiveAmmo.class,233)
            .name("枪械补给包")
            .desc("一个装满了枪械的包，可以从中获取到各种枪械。");
    }
}
