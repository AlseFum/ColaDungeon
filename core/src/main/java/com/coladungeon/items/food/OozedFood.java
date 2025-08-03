package com.coladungeon.items.food;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Ooze;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Item;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.EventBus;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class OozedFood extends Food {
    {
        image = ItemSpriteSheet.RATION; // Placeholder, replace with appropriate sprite
        stackable = false;
    }
    public static int a=1;
    static{
        System.out.println("OozedFood static block");
        EventBus.register("ooze", (data) -> {
            if (data instanceof com.coladungeon.utils.EventBus.EventData eventData) {
                Object posObj = eventData.get("pos");
                
                if (posObj instanceof Integer pos) {
                    // Check the heap at the position
                    if (Dungeon.level.heaps.get(pos) != null) {
                        // Create a copy of the items list to avoid concurrent modification
                        java.util.List<Item> itemsCopy = new java.util.ArrayList<>(Dungeon.level.heaps.get(pos).items);
                        for (Item item : itemsCopy) {
                            // Check if the item is a food item
                            if (item instanceof Food && !(item instanceof OozedFood)) {
                                // Remove the original food
                                Dungeon.level.heaps.get(pos).items.remove(item);

                                // Create an OozedFood version
                                OozedFood oozedFood = new OozedFood(item);

                                // Add the OozedFood back to the heap
                                Dungeon.level.heaps.get(pos).items.add(oozedFood);
                            }
                        }
                    }
                }
            }
            return null; // EventBus.register expects a return value
        });
    }
    // New blessing buff class
    public static class OozedBlessing extends Buff {
        {
            type = buffType.POSITIVE;
        }

        private static final float DURATION = 300f;
        private float left = DURATION;

        @Override
        public boolean act() {
            left -= TICK;
            if (left <= 0) {
                detach();
            }
            return true;
        }

        @Override
        public String desc() {
            return "被淤泥祝福的力量正在庇护着你，赋予你超乎寻常的活力和韧性。";
        }

        @Override
        public int icon() {
            return 0; // Default icon
        }
    }

    private Item originalFood;
    private String customName;

    // Default constructor for serialization
    public OozedFood() {}

    public OozedFood(Item originalFood) {
        this.originalFood = originalFood;
        this.customName = "被淤泥祝福的" + originalFood.name();
    }

    @Override
    public String name() {
        return customName != null ? customName : super.name();
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_EAT)) {
            // Apply the blessing buff first
            Buff.affect(hero, OozedBlessing.class);

            // Try to execute the original food's effect
            if (originalFood instanceof Food originalFoodAsFood) {
                try {
                    originalFoodAsFood.execute(hero, action);
                } catch (Exception e) {
                    // Log or handle any exceptions
                    GLog.w("无法完全应用原食物的效果");
                }
            }

            // Only apply Ooze if blessing is not active
            if (hero.buff(OozedBlessing.class) == null) {
                // Apply a stronger Ooze buff
                Ooze ooze = Buff.affect(hero, Ooze.class);
                float oozeDuration = Ooze.DURATION * 1.5f; // 50% longer
                ooze.set(oozeDuration);

                GLog.w("这份被淤泥污染的食物看起来非常恶心。吃下去后，你感到一股腐蚀性的黏液迅速蔓延全身！");
            } else {
                GLog.w("这份被淤泥祝福的食物散发出诡异的光芒。吃下去后，你感到一股神秘的力量在体内流动！");
            }
            
            // Consume the item
            detach(hero.belongings.backpack);
        } else {
            super.execute(hero, action);
        }
    }

    @Override
    public String desc() {
        return "这是一份被神秘淤泥祝福的" + originalFood.name() + "。食用后不仅会获得原食物的效果，还会获得长达300回合的神秘祝福。但同时，腐蚀性的黏液也会侵蚀你的身体。";
    }

    @Override
    public int value() {
        // Oozed food has no value
        return 0;
    }

    @Override
    public Item random() {
        // Prevent random generation
        return null;
    }

    private static final String ORIGINAL_FOOD = "original_food";
    private static final String CUSTOM_NAME = "custom_name";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        
        // Store the original food item
        if (originalFood != null) {
            bundle.put(ORIGINAL_FOOD, originalFood);
        }
        
        // Store the custom name
        if (customName != null) {
            bundle.put(CUSTOM_NAME, customName);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        
        // Restore the original food item
        if (bundle.contains(ORIGINAL_FOOD)) {
            originalFood = (Item) bundle.get(ORIGINAL_FOOD);
        }
        
        // Restore the custom name
        if (bundle.contains(CUSTOM_NAME)) {
            customName = bundle.getString(CUSTOM_NAME);
        }
    }
}
