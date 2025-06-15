package com.coladungeon.mod.ArknightsMod.items.weapon;

import java.util.ArrayList;

import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.weapon.melee.MeleeWeapon;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class FlagBearerWeapon extends MeleeWeapon {
    private static final String AC_CHARGE = "CHARGE";
    private static final String AC_ACTIVATE = "ACTIVATE";
    private static final String CHARGE = "charge";
    private static final int MAX_CHARGE = 3;
    private int charge = 0;
    {
        image=114;
        tier=1;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_CHARGE);
        actions.add(AC_ACTIVATE);
        return actions;
    }
    public FlagBearerWeapon() {
        super();
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_CHARGE)) {
            if (charge < MAX_CHARGE) {
                charge++;
                GLog.p("充能完成！当前充能：" + charge + "/" + MAX_CHARGE);
                updateQuickslot();
            } else {
                GLog.w("充能已满！");
            }
        } else if (action.equals(AC_ACTIVATE)) {
            if (charge > 0) {
                charge--;
                GLog.p("激活成功！当前充能：" + charge + "/" + MAX_CHARGE);
                updateQuickslot();
            } else {
                GLog.w("充能不足！");
            }
        }
    }

    @Override
    public String name() {
        return "旗手武器";
    }

    @Override
    public String desc() {
        String desc = "这是一把特殊的武器，可以通过激活充能。\n\n";
        desc += "当前充能：" + charge + "/" + MAX_CHARGE + "\n\n";
        desc += "效果说明：\n";
        desc += "- 通过激活充能，最多可充能3次";
        return desc;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CHARGE, charge);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        charge = bundle.getInt(CHARGE);
    }

    @Override
    public int max(int lvl) {
        return 4 * (tier + 1) + lvl * (tier + 1);
    }

    @Override
    public int min(int lvl) {
        return 1 + tier + lvl;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_CHARGE)) {
            return "充能";
        } else if (action.equals(AC_ACTIVATE)) {
            return "激活";
        }
        return super.actionName(action, hero);
    }
}

//充能后给buff，缓慢迅速恢复，禁止攻击