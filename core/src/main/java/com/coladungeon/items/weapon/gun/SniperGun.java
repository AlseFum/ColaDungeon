package com.coladungeon.items.weapon.gun;

import java.util.ArrayList;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.FlavourBuff;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteManager;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.ui.ActionIndicator;
import com.coladungeon.ui.BuffIndicator;
import com.coladungeon.ui.HeroIcon;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;

public class SniperGun extends Gun {
    public int MAX_CHARGE = 8;

    {
        image = ItemSpriteSheet.CROSSBOW; // 暂时使用十字弩的图标

        defaultAction = AC_AIM; // 默认动作为瞄准

        // 设置弹药参数
        ammo = 10;
        maxAmmo = 10;
        reloadTime = 1.5f;

        usesTargeting = true;
    }

    private static final String AC_AIM = "瞄准";

    @Override
    public String name() {
        return "狙击步枪";
    }

    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder();
        desc.append("一把精密的狙击步枪，可以进行蓄力瞄准以提高伤害。\n\n");

        return desc.toString();
    }

    @Override
    protected void addSubActions(Hero hero, ArrayList<String> actions) {
        actions.add(AC_AIM);
    }

    @Override
    public String subActionName(String action, Hero hero) {
        if (action.equals(AC_AIM)) {
            return "瞄准";
        }
        return null;
    }

    @Override
    protected void executeSubAction(Hero hero, String action) {
        if (action.equals(AC_AIM)) {
            if (ammo <= 0) {
                GLog.w("弹药不足！");
                return;
            }
            GameScene.selectCell(
                    "选择目标",
                    (Integer target) -> {
                        if (target == null) {
                            return;
                        }
                        Char enemy = Actor.findChar(target);
                        if (enemy == null) {
                            GLog.w("必须选择一个敌人作为目标！");
                            return;
                        }
                        startAiming(enemy);

                    });
        }
    }

    private void startAiming(Char target) {
        if (ammo <= 0) {
            GLog.w("弹药不足！");
            return;
        }
        SniperAim aim = Buff.affect(
            Dungeon.hero, SniperAim.class, 1
            );
        aim.target = target;
        ActionIndicator.setAction(new FireAmmo(this,target,aim));
        Dungeon.hero.spendAndNext(0.5f);
    }
    @Override
    protected int fire_proc(Char shooter, Char target, int damage) {
        // Subtract the target's defense from the damage
        int defense = target != null && target instanceof Char targetChar ? targetChar.drRoll() : 0;
        // Get the SniperAim buff from the shooter
        SniperAim aim = shooter.buff(SniperAim.class);
        double chargeMultiplier = (aim != null) ? 1+(0.4+0.2*level()*(1+0.5*tier)+0.1*tier)*aim.charge : 1;
        // Multiply damage by the charge multiplier
        int actualDamage = Math.max((int)Math.round(damage * chargeMultiplier) - defense, 0);
        return actualDamage;
    }
    @Override
    public int STRReq(int lvl) {
        return 8 + Math.round(lvl * 0.5f); // 降低力量需求
    }

    @Override
    public int min(int lvl) {
        return 8 + 3 * lvl; // 提高基础伤害
    }

    @Override
    public int max(int lvl) {
        return 25 + 6 * lvl; // 提高最大伤害
    }

    @Override
    public float accuracyFactor(Char owner, Char target) {
        float acc = super.accuracyFactor(owner, target);

        SniperAim aim = owner.buff(SniperAim.class);
        if (aim != null && aim.target == target) {
            acc *= Math.min(4f, (aim.charge / MAX_CHARGE) * 3f+1f);
        }

        return acc;
    }

    // 将SniperAim类定义提到前面
    public static class SniperAim extends FlavourBuff implements ActionIndicator.Action {

        {
            type = buffType.POSITIVE;
            
        }
        private Char target = null;
        
        private int charge = 1;
        public static int MAX_CHARGE = 8;

        @Override
        public String name() {
            return "瞄准中";
        }

        @Override
        public String desc() {
            return "你瞄准了"+target.name()+"，蓄力"+charge+"层";
        }

        @Override
        public boolean act() {
            spend(1f);
            
            // 检查目标是否还存在
            if (target == null || !target.isAlive() ) {
                GLog.w("目标已丢失！");
                detach();
                return true;
            }
            
            if (charge < MAX_CHARGE) {
                charge++;
                GLog.i("狙击蓄力层数增加到 " + charge);
            }
            
            return true;
        }

        @Override
        public void detach() {
            ActionIndicator.clearAction(null);
            System.out.println("Should detach action");
            super.detach();
            
        }

        @Override
        public int icon() {
            return BuffIndicator.MARK;
        }

        @Override
        public String iconTextDisplay() {
            return String.valueOf(charge);
        }

        @Override
        public void doAction() {
            GLog.i("点击了瞄准BuffIndicator");
        }

        @Override
        public int indicatorColor() {
            return 0x00AA00; // 绿色，表示瞄准
        }

        @Override
        public String actionName() {
            return "瞄准";
        }

        @Override
        public int actionIcon() {
            return HeroIcon.SNIPERS_MARK;
        }

    }

    public static class FireAmmo implements ActionIndicator.Action {

        private SniperGun gun;
        public Char target;
        public SniperAim aim;
        public FireAmmo(SniperGun gun,Char target,SniperAim aim) {
            this.gun = gun;
            this.target = target;
            this.aim = aim;
        }

        @Override
        public void doAction() {
            if (Dungeon.hero.ready) {
                gun.consumeAmmo(1);
                gun.fire(target.pos);
                aim.detach();
                GLog.w("should fire and clear");
            }
        }

        @Override
        public int indicatorColor() {
            return 0xAA0000; // 红色，表示射击
        }

        @Override
        public String actionName() {
            return "开火";
        }

        @Override
        public int actionIcon() {
            return HeroIcon.SNIPER;
        }

        @Override
        public Visual primaryVisual() {
            ItemSpriteManager.ImageMapping mapping = ItemSpriteManager.getImageMapping("gunfire");
            Image icon = new Image(mapping.texture);
            icon.frame(mapping.rect);
            return icon;
        }

        @Override
        public Visual secondaryVisual() {
            return null;
        }
    }
}
