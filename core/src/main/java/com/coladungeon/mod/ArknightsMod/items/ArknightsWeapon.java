package com.coladungeon.mod.ArknightsMod.items;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;

import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.weapon.melee.MeleeWeapon;
import com.coladungeon.utils.GLog;

public class ArknightsWeapon extends MeleeWeapon {
    private static String defaultActionName;

    static {
        searchdefaultaction(ArknightsWeapon.class);
    }

    public static void searchdefaultaction(Class<?> which) {
        // 使用反射获取所有带有@Action注解的方法
        for (java.lang.reflect.Method method : which.getDeclaredMethods()) {
            Action actionAnnotation = method.getAnnotation(Action.class);
            if (actionAnnotation != null && actionAnnotation.isDefault()) {
                defaultActionName = actionAnnotation.raw();
                return;
            }
        }
    }

    public ArknightsWeapon() {
        super();
        if (defaultActionName != null) {
            defaultAction = defaultActionName;
        }
    }

    public enum ChargeType{
        Nope,
        Accu,
        Once
    }
    public ChargeType useCharge=ChargeType.Nope;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Action {
        String raw();  // 动作的标识符
        String title() default "";  // 动作的显示名称
        boolean isHidden() default false;
        boolean isDefault() default false;
    }

    //example
    @Action(raw = "test", title = "测试")
    public void test(Hero hero,String action){
        GLog.i("test");
    }
    @Action(raw="attack",title="攻击",isDefault=true)
    public void attack(Hero hero,String action){
        GLog.i("attack");
    }
    @Override
    public void execute(Hero hero,String action){
        GLog.i("execute:"+action);
        // 使用反射获取所有带有@Action注解的方法
        for (java.lang.reflect.Method method : this.getClass().getDeclaredMethods()) {
            Action actionAnnotation = method.getAnnotation(Action.class);
            if (actionAnnotation != null && actionAnnotation.raw().equals(action)) {
                try {
                    method.invoke(this,hero,action);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions =super.actions(hero);
        
        // 使用反射获取所有带有@Action注解的方法
        for (java.lang.reflect.Method method : this.getClass().getDeclaredMethods()) {
            Action actionAnnotation = method.getAnnotation(Action.class);
            if (actionAnnotation != null && !actionAnnotation.isHidden()) {
                actions.add(actionAnnotation.raw());
            }
        }
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        // 使用反射获取所有带有@Action注解的方法
        for (java.lang.reflect.Method method : this.getClass().getDeclaredMethods()) {
            Action actionAnnotation = method.getAnnotation(Action.class);
            if (actionAnnotation != null && actionAnnotation.raw().equals(action)) {
                return actionAnnotation.title().isEmpty() ? action : actionAnnotation.title();
            }
        }
        return super.actionName(action, hero);
    }

} 