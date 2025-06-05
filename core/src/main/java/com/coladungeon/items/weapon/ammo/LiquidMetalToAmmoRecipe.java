package com.coladungeon.items.weapon.ammo;

import com.coladungeon.items.LiquidMetal;
import com.coladungeon.items.Recipe;
import com.coladungeon.items.Item;
import java.util.ArrayList;

public class LiquidMetalToAmmoRecipe extends Recipe.SimpleRecipe {
    {
        inputs = new Class[]{LiquidMetal.class};
        inQuantity = new int[]{1};
        cost = 0;
        output = Ammo.class;
        outQuantity = 1;
    }

    @Override
    public boolean testIngredients(ArrayList<Item> ingredients) {
        // 只允许1个液态金属
        return super.testIngredients(ingredients);
    }
} 