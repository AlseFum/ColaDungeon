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
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.ui.ActionIndicator;
import com.coladungeon.ui.BuffIndicator;
import com.coladungeon.ui.HeroIcon;
import com.coladungeon.ui.HeroIconManager;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndOptions;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.noosa.particles.Emitter;

import com.coladungeon.sprites.ItemSpriteManager;

public class SniperGun extends Gun {

    private static final float MAX_CHARGE = 3f; // 最大蓄力时间（秒）
    private static final float CHARGE_RATE = 0.25f; // 每回合增加的蓄力时间

    {
        image = ItemSpriteSheet.CROSSBOW; // 暂时使用十字弩的图标
        hitSound = Assets.Sounds.RAY;
        hitSoundPitch = 1.2f;
        
        defaultAction = AC_AIM; // 默认动作为瞄准
        
        // 设置弹药参数
        ammo = 15; 
        maxAmmo = 15;
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
            GameScene.selectCell(new CellSelector.Listener() {
                @Override
                public void onSelect(Integer target) {
                    if (target != null) {
                        Char enemy = Actor.findChar(target);
                        if (enemy != null) {
                            startAiming(enemy);
                        } else {
                            GLog.w("必须选择一个敌人作为目标！");
                        }
                    }
                }
                
                @Override
                public String prompt() {
                    return "选择目标";
                }
            });
        }
    }

    private void startAiming(Char target) {
        if (ammo <= 0) {
            GLog.w("弹药不足！");
            return;
        }
        SniperAim aim = Buff.affect(Dungeon.hero, SniperAim.class, 3f);
        aim.setTarget(target);
        aim.gun = this;
        ActionIndicator.setAction(new FireAmmo(this));
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
            multiplier = Math.min(3f, aim.charge);
            GLog.i("狙击蓄力层数: " + aim.charge + " 倍率: " + multiplier);
        }
        // 保存原始伤害
        int originalPower = cartridge.power;
        // 设置新的伤害
        cartridge.power = Math.round(originalPower * multiplier);
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
            acc *= Math.min(4f, 1f  / MAX_CHARGE * 3f);
        }
        
        return acc;
    }
    
    // 将SniperAim类定义提到前面
    public static class SniperAim extends FlavourBuff implements ActionIndicator.Action {
        {
            type = buffType.POSITIVE;
        }
        private Char target = null;
        private SniperGun gun;
        private int charge = 1;
        private static final int MAX_CHARGE = 3;
        
        public void setTarget(Char target) {
            this.target = target;
        }

        @Override
        public boolean act() {
            if (charge < MAX_CHARGE) {
                charge++;
                GLog.i("狙击蓄力层数增加到 " + charge);
            }
            spend(TICK);
            return true;
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
    
    public static class FireAmmo implements ActionIndicator.Action {
        private SniperGun gun;
        
        public FireAmmo(SniperGun gun) {
            this.gun = gun;
        }
        
        @Override
        public void doAction() {
            if (Dungeon.hero.ready) {
                GameScene.selectCell(new CellSelector.Listener() {
                    @Override
                    public void onSelect(Integer target) {
                        if (target != null) {
                            gun.fire(target);
                        }
                    }
                    
                    @Override
                    public String prompt() {
                        return "选择目标";
                    }
                });
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
        public int actionIcon(){
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