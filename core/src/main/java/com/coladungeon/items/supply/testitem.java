package com.coladungeon.items.supply;

import java.util.ArrayList;

import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Item;
import com.coladungeon.utils.GLog;


public class testitem extends Item {
    public static final String AC_USE = "USE";
    
    {
        defaultAction = AC_USE;
    }
    @Override
    public void execute(Hero hero, String action) {
      
        if (action.equals(AC_USE)) {
            hero.sprite.showStatus(0xFF0000, "%d", 12);  // 红色显示伤害数值
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_USE);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_USE)) {
            return "使用";
        }
        return super.actionName(action, hero);
    }

    @Override
    public String name() {
        return "TabWindow 示例";
    }

    @Override
    public String desc() {
        return "这是一个用于展示如何使用 TabWindow 的测试物品。";
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return 0;
    }
}
