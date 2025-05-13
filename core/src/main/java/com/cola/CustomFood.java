package com.cola;

import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;

import java.util.function.Consumer;
import java.util.function.Function;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;

public class CustomFood extends Food {
    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        hero.HP = Math.min(hero.HP + 5, hero.HT);
        if(onEat != null){
            onEat.accept(hero);
        }
    }

    public Consumer<Hero> onEat;
    public String label="Custom Food";
    public Function<Integer,Integer> price=(useless)->10*quantity;
    @Override
    public String name(){
        return label;
    }
    @Override
    public int value(){
        return price.apply(quantity);
    }

    public static CustomFoodFactory order(){
        return new CustomFoodFactory();
    }
    protected CustomFood clone(){
        CustomFood clone = new CustomFood();
        clone.energy = energy;
        clone.onEat = onEat;
        clone.label = label;
        return clone;
    }   
    public static class CustomFoodFactory  {
        private CustomFood baking = new CustomFood();

        public CustomFoodFactory setHunger(float hunger){
            baking.energy = hunger;
            return this;
        }

        public CustomFoodFactory setOnEat(Consumer<Hero> onEat){
            baking.onEat = onEat;
            return this;
        }

        

        public CustomFoodFactory setLabel(String label){
            baking.label = label;
            return this;
        }


        public CustomFoodFactory setPrice(Function<Integer,Integer> price){
            baking.price = (useless)->114514;
            return this;
        }
        public CustomFood make(){
            return baking.clone();
        }   
        
    }
}