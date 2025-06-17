package com.coladungeon.items.food;

import com.coladungeon.actors.hero.Hero;
import com.coladungeon.sprites.ItemSpriteManager;
import com.watabou.utils.Bundle;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomFood extends Food {

    public String id = "nihaofood";
    public String label = "nihao";

    public CustomFood() {
        super();
    }

    public CustomFood(CFTemplate template) {
        super();
        receive_template(template);
    }
    public CustomFood(String id){
        super();
        receive_template(id);
    }

    @Override
    public String name() {
        return label;
    }

    @Override
    public void satisfy(Hero hero) {
        super.satisfy(hero);
        CFTemplate template = food_records.get(id);
        if (template != null) {
            template.onEat.accept(hero);
        }
    }

    @Override
    public int value() {
        CFTemplate template = food_records.get(id);
        if (template != null) {
            return template.price.apply(quantity);
        }
        return 10 * quantity;
    }

    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        id = bundle.getString("id");
        receive_template(id);
    }

    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("id", id);
    }

    private void receive_template(CFTemplate template) {
        if (template == null) {
            System.out.println("[CustomFood::receive_template] template is null");
            return;
        }
        this.id = template.key;
        this.energy = template.energy;
        this.label = template.label;
        this.image = template.image;
    }

    private void receive_template(String id) {
        CFTemplate template = food_records.get(id);
        receive_template(template);
    }

    public static class CFTemplate {
        public CFTemplate() {
        }

        public CFTemplate(String key) {
            this.key = key;
        }

        public String key = "Cola";
        public String label = "Cola";
        public float energy = 10.0f;
        public int image = ItemSpriteManager.ByName("skel");
        public Consumer<Hero> onEat = h -> {
        };
        public Function<Integer, Integer> price = i -> 10;
    }

    public static class Factory {
        private CFTemplate baking = new CFTemplate();

        public Factory(String key) {
            baking.key = key;
        }

        public Factory setLabel(String label) {
            baking.label = label;
            return this;
        }

        public Factory setEnergy(float energy) {
            baking.energy = energy;
            return this;
        }

        public Factory onEat(Consumer<Hero> onEat) {
            baking.onEat = onEat;
            return this;
        }
        public Factory setImage(int image){
            baking.image=image;
            return this;
        }
        public Factory setPrice(int price) {
            baking.price = i -> price;
            return this;
        }
        public Factory setPrice(Function<Integer, Integer> price) {
            baking.price = price;
            return this;
        }

        public Factory register() {
            food_records.put(baking.key, baking);
            
            // 同时注册到生成器系统中
            if (!com.coladungeon.items.Generator.categoryContainsType(
                    com.coladungeon.items.Generator.Category.CUSTOM_FOOD,
                    com.coladungeon.items.food.CustomFood.class)) {
                com.coladungeon.items.Generator.registerItem(
                    com.coladungeon.items.Generator.Category.CUSTOM_FOOD,
                    com.coladungeon.items.food.CustomFood.class, 1f);
            }
            
            return this;
        }

        public CustomFood make() {
            return new CustomFood(baking);
        }
    }

    public static HashMap<String, CFTemplate> food_records = new HashMap<>();
    static {
        new Factory("p")
            .setLabel("Custom food")
            .setEnergy(10.0f)
            .onEat(h -> System.out.println("wdmnd"))
            .register();
    }
}