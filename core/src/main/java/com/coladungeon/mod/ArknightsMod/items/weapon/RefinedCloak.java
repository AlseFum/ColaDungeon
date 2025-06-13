package com.coladungeon.mod.ArknightsMod.items.weapon;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Invisibility;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.artifacts.CloakOfShadows;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class RefinedCloak extends CloakOfShadows {

    {
        image = ItemSpriteSheet.ARTIFACT_CLOAK;
        defaultAction = AC_ACTIVATE;
        unique = true;
    }

    private static final String AC_ACTIVATE = "ACTIVATE";
    private static final String AC_CHARGE = "CHARGE";

    private int charge = 0;
    private static final int MAX_CHARGE = 3;

    @Override
    public String name() {
        return "精制暗影斗篷";
    }

    @Override
    public String desc() {
        String desc = "这是一件经过罗德岛工程部改良的暗影斗篷，可以通过充能来获得更强的隐身效果。\n\n";
        desc += "当前充能：" + charge + "/" + MAX_CHARGE + "\n\n";
        desc += "效果说明：\n";
        desc += "1点充能：隐身5回合\n";
        desc += "2点充能：隐身8回合并提升移动速度\n";
        desc += "3点充能：隐身12回合，提升移动速度并恢复生命";
        return desc;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ACTIVATE);
        actions.add(AC_CHARGE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_ACTIVATE)) {
            if (charge > 0) {
                GameScene.show(new WndOptions(
                        "精制暗影斗篷",
                        "选择要使用的充能点数：",
                        "1点充能 - 基础隐身",
                        "2点充能 - 隐身并加速",
                        "3点充能 - 隐身、加速并恢复") {
                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            activate(hero, 1);
                        } else if (index == 1) {
                            activate(hero, 2);
                        } else if (index == 2) {
                            activate(hero, 3);
                        }
                    }
                });
            } else {
                GLog.w("充能不足！");
            }
        } else if (action.equals(AC_CHARGE)) {
            if (charge < MAX_CHARGE) {
                charge++;
                GLog.p("充能完成！当前充能：" + charge + "/" + MAX_CHARGE);
                updateQuickslot();
            } else {
                GLog.w("充能已满！");
            }
        } else {
            super.execute(hero, action);
        }
    }

    private void activate(Hero hero, int power) {
        if (charge >= power) {
            charge -= power;
            Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
            
            // 根据power等级释放不同效果
            switch (power) {
                case 1:
                    // 一级效果：基础隐身
                    Buff.affect(hero, Invisibility.class, 5f);
                    GLog.p("进入隐身状态，持续5回合！");
                    break;
                case 2:
                    // 二级效果：隐身并加速
                    Buff.affect(hero, Invisibility.class, 8f);
                    RefinedCloakBuff buff2 = new RefinedCloakBuff();
                    buff2.setSpeed(1.5f);
                    hero.add(buff2);
                    GLog.p("进入隐身状态，持续8回合！移动速度提升！");
                    break;
                case 3:
                    // 三级效果：隐身、加速并恢复
                    Buff.affect(hero, Invisibility.class, 12f);
                    RefinedCloakBuff buff3 = new RefinedCloakBuff();
                    buff3.setSpeed(2.0f);
                    hero.add(buff3);
                    hero.HP = Math.min(hero.HP + 15, hero.HT);
                    GLog.p("进入隐身状态，持续12回合！移动速度大幅提升！恢复15点生命！");
                    break;
            }
            
            updateQuickslot();
        }
    }

    private static final String CHARGE = "charge";

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

    public static class RefinedCloakBuff extends Buff {
        private float speed = 1.0f;

        public void setSpeed(float speed) {
            this.speed = speed;
        }

        @Override
        public boolean act() {
            if (target instanceof Hero) {
                Hero hero = (Hero) target;
                // 在这里实现加速效果
                // 例如：修改移动速度等
            }
            spend(TICK);
            return true;
        }
    }
} 