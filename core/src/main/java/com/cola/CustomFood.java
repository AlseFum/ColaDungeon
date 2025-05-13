package com.cola;

import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;

import java.util.function.Consumer;
import java.util.function.Function;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import java.util.HashMap;
import com.watabou.utils.Bundle;
public class CustomFood extends Food {
    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        if (onEat != null) {
            onEat.accept(hero);
        }
    }
    public String id;
    public String label = "Custom Food";
    public Consumer<Hero> onEat = h -> {};
    public Function<Integer, Integer> price = (useless) -> 10 * quantity;

    @Override
    public String name() {
        return label;
    }

    @Override
    public int value() {
        return price.apply(quantity);
    }

    public static Factory order(String id) {
        return new Factory(id);
    }

    protected CustomFood clone() {
        CustomFood clone = new CustomFood();
        clone.id=id;
        clone.energy = energy;
        clone.onEat = onEat;
        clone.label = label;
        clone.price = price;
        return clone;
    }

    public static HashMap<String, CustomFood> food_records = new HashMap<>();
    public static CustomFood getFood(String id) {
        return food_records.get(id).clone();
    }
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        id=bundle.getString("id");
        //need to copy from register;
        CustomFood food=food_records.get(id);
        if(food!=null){
            energy=food.energy;
            onEat=food.onEat;
            label=food.label;
            price=food.price;
        }
    }
    @Override
    public void storeInBundle(Bundle bundle) {
        
        super.storeInBundle(bundle);
        bundle.put("id", id);
    }
    public static class Factory {
        public Factory(String id){
            baking.id=id;
        }
        private CustomFood baking = new CustomFood();

        public Factory setHunger(float Energy) {
            baking.energy = Energy;
            return this;
        }

        public Factory setOnEat(Consumer<Hero> onEat) {
            baking.onEat = onEat;
            return this;
        }

        public Factory setLabel(String label) {
            baking.label = label;
            return this;
        }

        public Factory setImage(int image) {
            baking.image = image;
            return this;
        }

        public Factory setPrice(int price) {
            baking.price = (useless) -> price;
            return this;
        }

        public Factory setPrice(Function<Integer, Integer> price) {
            baking.price = (useless) -> 114514;
            return this;
        }

        public CustomFood make() {
            return baking.clone();
        }

        public void register(String id) {
            food_records.put(id, baking.clone());
        }
    }
}