package com.coladungeon.items.weapon.sniper;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.FlavourBuff;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.weapon.gun.Gun;
import com.coladungeon.mechanics.Ballistica;
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

    private static final float MAX_CHARGE = 3f; // 最大蓄力时间（秒）
    private static final float CHARGE_RATE = 0.25f; // 每回合增加的蓄力时间

    {
        image = ItemSpriteSheet.CROSSBOW; // 暂时使用十字弩的图标
        hitSound = Assets.Sounds.RAY;
        hitSoundPitch = 1.2f;

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
        SniperAim aim = Buff.affect(Dungeon.hero, SniperAim.class, 3f);
        aim.target = target;
        ActionIndicator.setAction(new FireAmmo(this,target));
        GLog.p("锁定目标：" + target.name());
        Dungeon.hero.spendAndNext(0.5f);
    }

    @Override
    protected void fire(int targetPos) {
        if (ammo <= 0) {
            GLog.w("弹药不足！");
            return;
        }

        SniperAim aim = curUser.buff(SniperAim.class);
        float multiplier = 1f;
        if (aim != null) {
            multiplier = Math.min(1f, aim.charge);
            GLog.i("狙击蓄力层数: " + aim.charge + " 倍率: " + multiplier);
        }
        // 保存原始伤害
        int originalPower = cartridge.power;
        // 设置新的伤害
        cartridge.power = (int)Math.round(originalPower * (1+multiplier*0.8));
        // 使用 Gun.shoot 进行射击
        ShotResult result = shoot(this, curUser, targetPos, cartridge, Ballistica.PROJECTILE);
        // 恢复原始伤害
        cartridge.power = originalPower;
        // 消耗弹药
        consumeAmmo(1);
        // 移除瞄准状态
        if (aim != null) {
            Buff.detach(curUser, SniperAim.class);
        }
        curUser.spendAndNext(0.5f);
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
            acc *= Math.min(4f, 1f / MAX_CHARGE * 3f);
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
        public static int MAX_CHARGE = 3;

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
            spend(TICK);
            
            // 检查目标是否还存在
            if (target == null || !target.isAlive() ) {
                GLog.w("目标已丢失！");
                detach();
                return true;
            }
            
            // 检查是否还装备着狙击枪
            if (!(Dungeon.hero.belongings.weapon instanceof SniperGun)) {
                GLog.w("狙击枪已卸下！");
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
            ActionIndicator.clearAction(this);
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
        public FireAmmo(SniperGun gun,Char target) {
            this.gun = gun;
            this.target = target;
        }

        @Override
        public void doAction() {
            if (Dungeon.hero.ready) {
                gun.fire(target.pos);
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
            ItemSpriteManager.ImageMapping mapping = ItemSpriteManager.getImageMapping("square");
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
