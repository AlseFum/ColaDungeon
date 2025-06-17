package com.coladungeon.items.weapon.ammo;

import com.coladungeon.items.Item;
import com.coladungeon.items.Recipe;
import com.coladungeon.items.Torch;

import java.util.ArrayList;

public class ExplosiveAmmoRecipe extends Recipe.SimpleRecipe {
    {
        // 定义输入物品
        inputs = new Class[]{
            Ammo.class,
            Torch.class
        };
        
        // 定义每种输入物品的数量
        inQuantity = new int[]{
            1,  // 需要1个普通弹药
            1   // 需要1个火把
        };
        
        // 定义合成所需能量
        cost = 2;
        
        // 定义输出物品
        output = ExplosiveAmmo.class;
        
        // 定义输出数量
        outQuantity = 1;
    }

    @Override
    public boolean testIngredients(ArrayList<Item> ingredients) {
        if (!super.testIngredients(ingredients)) return false;
        
        // 检查弹药是否被诅咒
        for (Item item : ingredients) {
            if (item instanceof Ammo && item.cursed) {
                return false;
            }
        }
        
        return true;
    }
} 