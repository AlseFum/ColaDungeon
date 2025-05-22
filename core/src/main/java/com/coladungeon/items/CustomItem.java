package com.coladungeon.items;

import com.coladungeon.actors.hero.Hero;
import com.coladungeon.sprites.ItemSpriteManager;
import com.watabou.utils.Bundle;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomItem extends Item {
    //not well implemented

    public String id = "default_item";
    public String label = "Custom Item";
    
    public CustomItem() {
        super();
    }
    
    public CustomItem(CITemplate template) {
        super();
        receive_template(template);
    }
    
    public CustomItem(String id) {
        super();
        receive_template(id);
    }
    
    @Override
    public String name() {
        return label;
    }
    
    @Override
    public int value() {
        CITemplate template = item_records.get(id);
        if (template != null) {
            return template.price.apply(quantity);
        }
        return 10 * quantity;
    }
    
    @Override
    public boolean isUpgradable() {
        CITemplate template = item_records.get(id);
        if (template != null) {
            return template.upgradable;
        }
        return super.isUpgradable();
    }
    
    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        CITemplate template = item_records.get(id);
        if (template != null && template.onUse != null) {
            template.onUse.accept(hero);
        }
    }
    
    @Override
    public String desc() {
        CITemplate template = item_records.get(id);
        if (template != null && template.description != null) {
            return template.description;
        }
        return super.desc();
    }
    
    @Override
    public boolean isSimilar(Item item) {
        if (item instanceof CustomItem) {
            return ((CustomItem)item).id.equals(this.id);
        }
        return super.isSimilar(item);
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        id = bundle.getString("id");
        receive_template(id);
    }
    
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("id", id);
    }
    private void receive_template(String id) {
        CITemplate template = item_records.get(id);
        if (template == null) {
            System.out.println("[CustomItem::receive_template] template is null");
            return;
        }
        receive_template(template);
    }
    private void receive_template(CITemplate template) {
        if (template == null) {
            System.out.println("[CustomItem::receive_template] template is null");
            return;
        }
        this.id = template.key;
        this.label = template.label;
        this.image = template.image;
        this.stackable = template.stackable;
        this.defaultAction = template.defaultAction;
    }
    
    
    
    public static class CITemplate {
        public CITemplate() {
        }
        
        public CITemplate(String key) {
            this.key = key;
        }
        
        public String key = "default";
        public String label = "Custom Item";
        public String description = null;
        public int image = 0; // Default sprite ID
        public boolean upgradable = false;
        public boolean stackable = false;
        public String defaultAction = null;
        public Consumer<Hero> onUse = null;
        public Function<Integer, Integer> price = i -> 10;
    }
    
    public static class Factory {
        private CITemplate building = new CITemplate();
        
        public Factory(String key) {
            building.key = key;
        }
        
        public Factory setLabel(String label) {
            building.label = label;
            return this;
        }
        
        public Factory setDescription(String description) {
            building.description = description;
            return this;
        }
        
        public Factory setImage(int image) {
            building.image = image;
            return this;
        }
        
        public Factory setUpgradable(boolean upgradable) {
            building.upgradable = upgradable;
            return this;
        }
        
        public Factory setStackable(boolean stackable) {
            building.stackable = stackable;
            return this;
        }
        
        public Factory setDefaultAction(String action) {
            building.defaultAction = action;
            return this;
        }
        
        public Factory onUse(Consumer<Hero> onUse) {
            building.onUse = onUse;
            return this;
        }
        
        public Factory setPrice(int price) {
            building.price = i -> price;
            return this;
        }
        
        public Factory setPrice(Function<Integer, Integer> price) {
            building.price = price;
            return this;
        }
        
        public Factory setCategory(String categoryName, float probability) {
            // This method is intentionally empty as we're removing DynamicGenerator dependency
            return this;
        }
        
        public Factory setCategory(Generator.Category category, float probability) {
            // This method is intentionally empty as we're removing DynamicGenerator dependency
            return this;
        }
        
        public Factory register() {
            item_records.put(building.key, building);
            
            // 同时注册到生成器系统中
            if (!Generator.categoryContainsType(Generator.Category.CUSTOM_ITEM, CustomItem.class)) {
                Generator.registerItem(Generator.Category.CUSTOM_ITEM, CustomItem.class, 1f);
            }
            
            return this;
        }
        
        public CustomItem make() {
            return new CustomItem(building);
        }
    }
    
    public static HashMap<String, CITemplate> item_records = new HashMap<>();
    
    static {
        // 注册几个示例物品
        new Factory("healing_potion")
            .setLabel("治疗药水")
            .setDescription("一瓶可以恢复生命值的药水。")
            .setImage(0)
            .setStackable(true)
            .onUse(hero -> {
                hero.HP = Math.min(hero.HP + 20, hero.HT);
                // hero.spend(1f);
                // hero.busy();
                hero.sprite.operate(hero.pos);
            })
            .setPrice(50)
            .register();
            
        new Factory("magic_amulet")
            .setLabel("魔法护符")
            .setDescription("一个充满魔力的护符，可以保护佩戴者。")
            .setImage(1)
            .setStackable(false)
            .setUpgradable(true)
            .onUse(hero -> {
                hero.spend(1f);
                hero.sprite.operate(hero.pos);
            })
            .setPrice(200)
            .register();
    }
} 