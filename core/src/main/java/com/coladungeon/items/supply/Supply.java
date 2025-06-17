package com.coladungeon.items.supply;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Item;
import com.watabou.utils.Bundle;

public class Supply extends Item {

    private static final String AC_OPEN = "启用";
    public String name="物资包";
    public String desc="一个装满了物资的包，可以从中获取到各种物品。";
    public Supply() {
        super();
    }

    public Supply name(String _name){
        this.name=_name;
        return this;
    }

    public Supply desc(String _desc){
        this.desc=_desc;
        return this;
    }

    public Supply image(int image){
        this.image=image;
        return this;
    }
    @Override
    public String name(){
        return name;
    }

    @Override
    public String desc(){
        return desc;
    }

    @Override
    public int image(){
        return image;
    }

    public ArrayList<Supplier<Item>> supplies = new ArrayList<>();
    public Supplier<Void> onOpen = null;
    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_OPEN);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_OPEN)) {
            return "启用";
        }
        return super.actionName(action, hero);
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_OPEN)) {
            open(hero);
        }
    }

    public void open(Hero hero) {
        for (Supplier<Item> supply : supplies) {
            supply.get().identify().collect();
        }
        if (onOpen != null) {
            onOpen.get();
        }
        this.detach(hero.belongings.backpack);
    }

    public Supply put_in(Class<? extends Item> item) {
        this.supplies.add(
                () -> {
                    try {
                        return item.getConstructor().newInstance();
                    } catch (Exception e) {
                        System.err.println("Failed to create instance of " + item.getName() + ": " + e.getMessage());
                        return null;
                    }
                }
        );
        return this;
    }
    public Supply put_in(Class<? extends Item> item, int quantity) {
        this.supplies.add(
                () -> {
                    try {
                        return item.getConstructor().newInstance().quantity(quantity);
                    } catch (Exception e) {
                        System.err.println("Failed to create instance of " + item.getName() + ": " + e.getMessage());
                        return null;
                    }
                }
        );
        return this;
    }

    public Supply put_in(Item item) {
        this.supplies.add(
                () -> item.duplicate()
        );
        return this;
    }

    public Supply put_in(Supplier<Item> supplier) {
        this.supplies.add(supplier);
        return this;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("name", name);
        bundle.put("desc", desc);
        bundle.put("image", image);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        name = bundle.getString("name");
        desc = bundle.getString("desc");
        image = bundle.getInt("image");
    }
    
}
