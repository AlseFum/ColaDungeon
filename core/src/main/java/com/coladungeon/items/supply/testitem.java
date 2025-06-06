package com.coladungeon.items.supply;
import com.coladungeon.items.Item;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.sprites.ItemSpriteSheet;
import java.util.ArrayList;

public class testitem extends Item{
    
    
    @Override
    public void execute(Hero hero,String action){
        if(action.equals(AC_TEST)){
            hero.spend(1);
            hero.sprite.operate(hero.pos);
        }
    }
    @Override
    public ArrayList<String> actions(Hero hero){
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_TEST);
        return actions;
    }
    @Override
    public String actionName(String action,Hero hero){
        if(action.equals(AC_TEST)){
            return "测试";
        }
        return super.actionName(action, hero);
    }
    public static final String AC_TEST = "TEST";
    public testitem(){
        super();
    }
    {
        image=ItemSpriteSheet.SOMETHING;
    }
    @Override
    public String name(){
        return "测试物品";
    }
    @Override
    public String desc(){
        return "现在还不知道有什么用。";
    }
}