package com.coladungeon.items.weapon.ammo;

import java.util.ArrayList;

import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Item;
import com.coladungeon.items.weapon.gun.Gun;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.windows.WndBag;

public class Ammo extends Item {
    public static final String AC_RELOAD = "重装子弹";
    public static final int DEFAULT_MAX_STACK = 999;
    public static int max_amount = 6;
    
    public            int amount = 0;
    public boolean full_reload= false;
    private Ammo _this=this;
    //if is full_reload, the amount will be set to gun.max_amount
    public Cartridge cartridge;
    {
        image = ItemSpriteSheet.DARTS;
        stackable = true;
        defaultAction = AC_RELOAD;
    }

    public Ammo() {
        quantity = 1;
        cartridge = new Cartridge(10,CartridgeEffect.Supply);
        amount = max_amount;
        full_reload = true;
    }

    @Override
    public String name() {
        return "弹药";
    }

    @Override
    public String desc() {
        return "通用的弹药。";
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int price() {
        return 10 * quantity;
    }

    @Override
    public int value() {
        return 10;
    }

    @Override
    public int getMaxStack() {
        return DEFAULT_MAX_STACK;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_RELOAD);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (AC_RELOAD.equals(action)) {
            GameScene.selectItem(new WndBag.ItemSelector() {
                    @Override
                    public String textPrompt() {
                        return "选择要装填子弹的枪";
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        if (!(item instanceof Gun gun)) return false;
                        return gun.canLoad(_this);
                    }

                    @Override
                    public void onSelect(Item item) {
                        ((Gun)item).reload(_this);
                    }
                });
        }
    }

    @Override
    public boolean isSimilar(Item item) {
        if (item instanceof Ammo ammo) {
            return this.getClass() == ammo.getClass() 
                && this.cartridge == ammo.cartridge 
                && this.amount == max_amount
                && ammo.amount == max_amount;
        }
        return false;
    }

    public void transfer(Ammo ammo) {
        if (this.isSimilar(ammo)) {
            int totalAmount = ammo.amount + amount;
            if (totalAmount > max_amount) {
                ammo.amount = max_amount;
                amount = totalAmount - max_amount;
            } else {
                amount = totalAmount;
                ammo.amount = 0;
            }
        }
    }
}
