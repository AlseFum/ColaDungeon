package com.coladungeon.mod.ArknightsMod.items.weapon;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Item;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class CommandTerminal extends Item {

    {
        image = ItemSpriteSheet.ARTIFACT_HORN1;
        defaultAction = AC_ACTIVATE;
        unique = true;
    }

    private static final String AC_ACTIVATE = "ACTIVATE";
    private static final String AC_CHARGE = "CHARGE";

    private int charge = 0;
    private static final int MAX_CHARGE = 3;

    @Override
    public String name() {
        return "罗德岛指挥终端";
    }

    @Override
    public String desc() {
        String desc = "这是罗德岛博士专用的指挥终端，可以通过充能来获得不同的增益效果。\n\n";
        desc += "当前充能：" + charge + "/" + MAX_CHARGE + "\n\n";
        desc += "效果说明：\n";
        desc += "1点充能：提升20%攻击力\n";
        desc += "2点充能：提升50%攻击力并恢复5点生命\n";
        desc += "3点充能：提升100%攻击力并恢复15点生命";
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
        super.execute(hero, action);

        if (action.equals(AC_ACTIVATE)) {
            if (charge > 0) {
                GameScene.show(new WndOptions(
                        "指挥终端",
                        "选择要使用的充能点数：",
                        "1点充能 - 提升攻击力",
                        "2点充能 - 提升攻击力并恢复生命",
                        "3点充能 - 大幅提升攻击力并恢复大量生命") {
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
        }
    }

    private void activate(Hero hero, int power) {
        if (charge >= power) {
            charge -= power;
            Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
            
            // 根据power等级释放不同效果
            switch (power) {
                case 1:
                    // 一级效果：短暂提升攻击力
                    CommandTerminalBuff buff1 = new CommandTerminalBuff();
                    buff1.setPower(1.2f);
                    hero.add(buff1);
                    GLog.p("攻击力提升20%！");
                    break;
                case 2:
                    // 二级效果：中等提升攻击力并恢复少量生命
                    CommandTerminalBuff buff2 = new CommandTerminalBuff();
                    buff2.setPower(1.5f);
                    hero.add(buff2);
                    hero.HP = Math.min(hero.HP + 5, hero.HT);
                    GLog.p("攻击力提升50%！恢复5点生命！");
                    break;
                case 3:
                    // 三级效果：大幅提升攻击力并恢复较多生命
                    CommandTerminalBuff buff3 = new CommandTerminalBuff();
                    buff3.setPower(2.0f);
                    hero.add(buff3);
                    hero.HP = Math.min(hero.HP + 15, hero.HT);
                    GLog.p("攻击力提升100%！恢复15点生命！");
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

    public static class CommandTerminalBuff extends Buff {
        private float power = 1.0f;

        public void setPower(float power) {
            this.power = power;
        }

        @Override
        public boolean act() {
            if (target instanceof Hero) {
                Hero hero = (Hero) target;
                // 在这里实现buff效果
                // 例如：提升攻击力等
            }
            spend(TICK);
            return true;
        }
    }
}
