package com.cola;

import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;

import java.util.function.Consumer;
import java.util.function.Function;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;

public class CustomFood extends Food {
    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        if(onEat != null){
            onEat.accept(hero);
        }
    }

    
    public String label="Custom Food";
    public Consumer<Hero> onEat=h->{};
    public Function<Integer,Integer> price=(useless)->10*quantity;
    @Override
    public String name(){
        return label;
    }
    @Override
    public int value(){
        return price.apply(quantity);
    }

    public static Factory order(){
        return new Factory();
    }
    protected CustomFood clone(){
        CustomFood clone = new CustomFood();
        clone.energy = energy;
        clone.onEat = onEat;
        clone.label = label;
        clone.price = price;
        return clone;
    }   
    public static class Factory  {
        private CustomFood baking = new CustomFood();

        public Factory setHunger(float Energy){
            baking.energy = Energy;
            return this;
        }

        public Factory setOnEat(Consumer<Hero> onEat){
            baking.onEat = onEat;
            return this;
        }

        public Factory setLabel(String label){
            baking.label = label;
            return this;
        }

        public Factory setImage(int image){
            baking.image = image;
            return this;
        }

        public Factory setPrice(int price){
            baking.price = (useless)->price;
            return this;
        }
        public Factory setPrice(Function<Integer,Integer> price){
            baking.price = (useless)->114514;
            return this;
        }
        public CustomFood make(){
            return baking.clone();
        }   
        
    }
}