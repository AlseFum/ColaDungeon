package com.coladungeon.items.weapon.gun;

import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.weapon.Weapon;
import com.coladungeon.items.weapon.ammo.Ammo;
import com.coladungeon.items.weapon.ammo.Cartridge;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import java.util.ArrayList;

public class HandGun extends Gun {

    private static final String AC_QUICKDRAW = "快速射击";

    {
        image = ItemSpriteSheet.CROSSBOW; // 临时图标，需要替换为手枪图标
        defaultAction = AC_FIRE;

        // 设置弹药相关属性
        maxAmmo = 12;
        ammo = maxAmmo;
        cartridge = new Cartridge(8);
        tier=1;
    }
    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_QUICKDRAW);
        return actions;
    }

    @Override
    public boolean canLoad(Ammo ammo) {
        return true||ammo.cartridge.cartridgeType==Cartridge.CartridgeType.SMALL;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_QUICKDRAW)) {
            return "快速射击";
        }
        return super.actionName(action, hero);
    }

    @Override
    public void executeSubAction(Hero hero, String action) {
        if (action.equals(AC_QUICKDRAW)) {
            Weapon originalWeapon = (Weapon) hero.belongings.weapon;

            hero.belongings.weapon = this;

            GameScene.selectCell(new CellSelector.Listener() {
                @Override
                public void onSelect(Integer target) {
                    if (target != null) {
                        fire(target);
                    }
                    if (originalWeapon != null) {
                        hero.belongings.backpack.items.add(originalWeapon);
                    }
                }

                @Override
                public String prompt() {
                    return "选择快速射击目标";
                }
            });
        }
    }

    @Override
    public int STRReq(int lvl) {
        return 10 + lvl; // 手枪力量需求较低
    }

    @Override
    public int min(int lvl) {
        return 3 + lvl; // 基础最小伤害3，每级+1
    }

    @Override
    public int max(int lvl) {
        return 6 + 3 * lvl; // 基础最大伤害6，每级+3
    }

    @Override
    public float getAmmoPowerMultiplier() {
        return (float) Math.pow(1.2f + 0.3f * level(),tier);
    }


    @Override
    public String name() {
        return "手枪";
    }

    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder();
        desc.append("一把轻巧的手枪，具有快速射击能力。\n\n");
        desc.append("初期伤害相对较高，但后期乏力");
        desc.append("当前弹药类型：").append(cartridge != null ? cartridge.cartridgeType : "无").append("。\n");

        return desc.toString();
    }
}
