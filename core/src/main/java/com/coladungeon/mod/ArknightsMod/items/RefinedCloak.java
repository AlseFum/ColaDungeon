package com.coladungeon.mod.ArknightsMod.items;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Haste;
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
        defaultAction = AC_STEALTH;
        unique = true;
        chargeCap=12;
    }

    private static final String AC_ACTIVATE = "ACTIVATE";
    private static final String AC_CHARGE = "CHARGE";

    @Override
    public String name() {
        return "罗德岛精制潜行斗篷";
    }

    @Override
    public String desc() {
        String desc = "这是一件经过罗德岛工程部改良的暗影斗篷，可以通过充能来获得更强的隐身效果。\n\n";
        desc += "当前充能：" + charge + "/" + chargeCap + "\n\n";
        desc += "效果说明：\n";
        desc += "1点充能：隐身5回合\n";
        desc += "2点充能：隐身8回合并提升移动速度\n";
        desc += "3点充能：隐身12回合，提升移动速度并恢复生命\n\n";
        desc += "特性：\n";
        desc += "- 每回合自动恢复1点充能";
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
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_ACTIVATE)) {
            return "激活";
        } else if (action.equals(AC_CHARGE)) {
            return "充能";
        } else {
            return super.actionName(action, hero);
        }
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_ACTIVATE)) {
            if (charge > 0) {
                GameScene.show(new WndOptions(
                        "罗德岛精制暗影斗篷",
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
            if (charge < chargeCap) {
                charge++;
                GLog.p("充能完成！当前充能：" + charge + "/" + chargeCap);
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
                    if (activeBuff == null) {
                        activeBuff = activeBuff();
                        activeBuff.attachTo(hero);
                        hero.sprite.operate(hero.pos);
                    }
                    GLog.p("进入隐身状态！");
                    break;
                case 2:
                    // 二级效果：隐身并加速
                    if (activeBuff == null) {
                        activeBuff = activeBuff();
                        activeBuff.attachTo(hero);
                        hero.sprite.operate(hero.pos);
                    }
                    Buff.affect(hero, Haste.class, 8f);
                    GLog.p("进入隐身状态！移动速度提升！");
                    break;
                case 3:
                    // 三级效果：隐身且不消耗充能
                    if (activeBuff == null) {
                        activeBuff = activeBuff();
                        activeBuff.attachTo(hero);
                        hero.sprite.operate(hero.pos);
                    }
                    RefinedCloakBuff buff3 = new RefinedCloakBuff();
                    buff3.setNoChargeConsumption(true);
                    buff3.setCloak(this);
                    hero.add(buff3);
                    GLog.p("进入隐身状态！在敌人视野外不消耗充能！");
                    break;
            }
            
            updateQuickslot();
        }
    }

    @Override
    protected ArtifactBuff activeBuff() {
        return new cloakStealth();
    }

    public class cloakStealth extends CloakOfShadows.cloakStealth {
        private float extraTime = 0f;

        @Override
        public boolean act() {
            if (target instanceof Hero) {
                Hero hero = (Hero) target;
                // 检查是否有不消耗充能的buff
                RefinedCloakBuff buff = hero.buff(RefinedCloakBuff.class);
                if (buff != null && buff.noChargeConsumption) {
                    // 检查是否在敌人视野内
                    boolean inEnemySight = false;
                    for (com.coladungeon.actors.mobs.Mob mob : com.coladungeon.Dungeon.level.mobs) {
                        // 更新敌人的视野
                        if (mob.fieldOfView == null || mob.fieldOfView.length != com.coladungeon.Dungeon.level.length()) {
                            mob.fieldOfView = new boolean[com.coladungeon.Dungeon.level.length()];
                        }
                        com.coladungeon.Dungeon.level.updateFieldOfView(mob, mob.fieldOfView);
                        
                        // 检查英雄是否在敌人视野内
                        if (mob.fieldOfView[hero.pos] && mob.invisible <= 0) {
                            inEnemySight = true;
                            break;
                        }
                    }
                    
                    // 如果不在敌人视野内，不消耗时间
                    if (!inEnemySight) {
                        extraTime += TICK;
                        spend(TICK);
                        return true;
                    }
                }
            }
            return super.act();
        }

        @Override
        public void detach() {
            if (extraTime > 0) {
                // 在buff结束时，将额外的时间加回去
                spend(-extraTime);
            }
            super.detach();
        }
    }

    public static class RefinedCloakBuff extends Buff {
        private boolean noChargeConsumption = false;
        private RefinedCloak cloak;

        public void setNoChargeConsumption(boolean noChargeConsumption) {
            this.noChargeConsumption = noChargeConsumption;
        }

        public void setCloak(RefinedCloak cloak) {
            this.cloak = cloak;
        }

        @Override
        public boolean act() {
            spend(TICK);
            return true;
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
} 