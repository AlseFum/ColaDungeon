/*
 * Cola Dungeon
 */

package com.coladungeon.items.weapon.sniper;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.FlavourBuff;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.Beam;
import com.coladungeon.effects.Lightning;
import com.coladungeon.effects.particles.ShadowParticle;
import com.coladungeon.items.weapon.gun.Gun;
import com.coladungeon.mechanics.Ballistica;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.scenes.PixelScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.tiles.DungeonTilemap;
import com.coladungeon.ui.ActionIndicator;
import com.coladungeon.ui.BuffIndicator;
import com.coladungeon.ui.HeroIcon;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndOptions;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class SniperGun extends Gun {

    private static final float MAX_CHARGE = 3f; // 最大蓄力时间（秒）
    private static final float CHARGE_RATE = 0.25f; // 每回合增加的蓄力时间

    {
        image = ItemSpriteSheet.CROSSBOW; // 暂时使用十字弩的图标
        hitSound = Assets.Sounds.RAY;
        hitSoundPitch = 1.2f;

        DLY = 0.8f; // 略微提高攻击速度
        RCH = 10; // 增加射程
        ACC = 1.5f; // 提高基础命中率
        
        defaultAction = AC_AIM; // 默认动作为瞄准
        
        // 设置弹药参数
        ammo = 15; 
        maxAmmo = 15;
        reloadTime = 1.5f;
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
        
        desc.append("_被动效果:_\n");
        desc.append("- 基础命中率提升50%\n");
        desc.append("- 射程增加到10格\n\n");
        
        desc.append("_主动技能 - 瞄准:_\n");
        desc.append("- 进入瞄准模式，每回合增加蓄力\n");
        desc.append("- 蓄力越多，伤害越高（最多300%）\n");
        desc.append("- 最大蓄力时间：3秒\n");
        desc.append("- 消耗1发弹药\n\n");
        
        desc.append("_弹药系统:_\n");
        desc.append("- 最大弹药：").append(maxAmmo).append("发\n");
        desc.append("- 当前弹药：").append(ammo).append("发 (").append(loadedAmmoType.getName()).append(")\n");
        desc.append("- 装弹时间：").append(reloadTime).append("秒\n\n");
        
        desc.append("_升级效果:_\n");
        desc.append("- 基础伤害：").append(min()).append("-").append(max()).append("\n");
        desc.append("- 力量需求：").append(STRReq()).append("\n");
        desc.append("- 每级提升：\n");
        desc.append("  * 最小伤害+2\n");
        desc.append("  * 最大伤害+5\n");
        desc.append("  * 力量需求+0.5");
        
        return desc.toString();
    }

    @Override
    protected void addGunActions(Hero hero, ArrayList<String> actions) {
        actions.add(AC_AIM);
    }

    @Override
    protected void executeGunAction(Hero hero, String action) {
        if (action.equals(AC_AIM)) {
            if (ammo <= 0) {
                GLog.w("弹药不足！");
                return;
            }
            GameScene.show(new WndOptions(
                new HeroIcon(new ActionIndicator.Action(){
                    @Override
                    public String actionName() { return "精准射击"; }
                    @Override
                    public int actionIcon() { return HeroIcon.PREPARATION; }
                    @Override
                    public void doAction() { /* 这是一个临时图标，不需要实际动作 */ }
                    @Override
                    public int indicatorColor() { return 0xFF0000; }  // 红色指示器
                }),
                "精准射击",
                "是否进入瞄准状态？\n\n" +
                "当前弹药：" + ammo + "/" + maxAmmo + " (" + loadedAmmoType.getName() + ")\n" +
                "蓄力时间：" + MAX_CHARGE + "秒\n" +
                "最大伤害：" + max() * 3 * 2.5f * getAmmoDamageMultiplier() + "\n" +
                "最大命中：" + (ACC * 4) + "x",
                "确定",
                "取消"
            ) {
                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        startAiming();
                    }
                }
            });
        }
    }

    private void startAiming() {
        if (ammo <= 0) {
            GLog.w("弹药不足！");
            return;
        }
        Buff.affect(Dungeon.hero, SniperAim.class);
        ActionIndicator.setAction(Dungeon.hero.buff(SniperAim.class));
        GLog.p("进入瞄准状态");
        Dungeon.hero.spendAndNext(0.5f);
    }

    @Override
    protected void fire(int targetPos) {
        if (ammo <= 0) {
            GLog.w("弹药不足！");
            return;
        }

        final Ballistica shot = new Ballistica(curUser.pos, targetPos, Ballistica.PROJECTILE);
        int cell = shot.collisionPos;

        curUser.sprite.zap(cell);
        Sample.INSTANCE.play(Assets.Sounds.RAY);
        
        // 添加射击光束特效
        curUser.sprite.parent.add(new Lightning(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(cell), null));
        new Beam.DeathRay(curUser.sprite.center(), DungeonTilemap.tileCenterToWorld(cell));

        SniperAim aim = curUser.buff(SniperAim.class);
        float damageMultiplier = 1f;
        if (aim != null) {
            float chargeRatio = aim.charge() / MAX_CHARGE;
            damageMultiplier = Math.min(3f, 1f + chargeRatio * 2f);
            
            if (Random.Float() < chargeRatio) {
                damageMultiplier *= 2.5f;
                GLog.p("爆头！造成 %.0f%% 伤害！", damageMultiplier * 100);
            } else {
                GLog.i("造成 %.0f%% 伤害", damageMultiplier * 100);
            }
        }
        
        // 应用弹药伤害倍率
        damageMultiplier *= getAmmoDamageMultiplier();
        
        Char enemy = Actor.findChar(cell);
        if (enemy != null) {
            int damage = Math.round(damageRoll(curUser) * damageMultiplier);
            
            if (damageMultiplier > 2.5f) {
                enemy.sprite.emitter().burst(ShadowParticle.UP, 5);
            }
            
            enemy.damage(damage, this);
            
            // 应用弹药特殊效果
            applyAmmoEffects(enemy, damage);
        }
        
        // 消耗弹药
        consumeAmmo(1);
        
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
        if (aim != null) {
            // 根据蓄力时间提高命中率，最高400%
            acc *= Math.min(4f, 1f + aim.charge() / MAX_CHARGE * 3f);
        }
        
        return acc;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_AIM)) return "瞄准";
        return super.actionName(action, hero);
    }

    public static class SniperAim extends FlavourBuff implements ActionIndicator.Action {
        
        private float chargeTime = 0f;
        private int lastPos = -1;
        private boolean isCharging = true;
        
        @Override
        public boolean attachTo(Char target) {
            boolean result = super.attachTo(target);
            if (result && target == Dungeon.hero) {
                lastPos = target.pos;
                ActionIndicator.setAction(this);
            }
            return result;
        }
        
        @Override
        public boolean act() {
            if (target == Dungeon.hero) {
                if (isCharging && target.pos == lastPos) {
                chargeTime = Math.min(MAX_CHARGE, chargeTime + CHARGE_RATE);
                    target.sprite.showStatus(0x00FFFF, "蓄力: %.1f秒", chargeTime);
                    
                if (chargeTime >= MAX_CHARGE) {
                        target.sprite.showStatus(0x00FF00, "蓄力完成!");
                        isCharging = false;
                    }
                } else if (target.pos != lastPos) {
                    // 如果移动，则重置蓄力
                    if (chargeTime > 0) {
                        target.sprite.showStatus(0xFF0000, "蓄力中断!");
                        chargeTime = 0;
                    }
                    lastPos = target.pos;
                    isCharging = true;
                }
                
                ActionIndicator.refresh();
                
                // 每回合都花费1/5点能量
                spend(Actor.TICK / 5f);
                return true;
            } else {
                detach();
                return false;
            }
        }
        
        public float charge() {
            return chargeTime;
        }
        
        @Override
        public void detach() {
            super.detach();
            ActionIndicator.clearAction(this);
        }

        @Override
        public String actionName() {
            if (chargeTime >= MAX_CHARGE) {
                return "开火";
            } else {
                return String.format("蓄力 (%.1f秒)", chargeTime);
            }
        }

        @Override
        public int actionIcon() {
            return HeroIcon.PREPARATION;
        }

        @Override
        public Visual primaryVisual() {
            return new Image(Assets.Interfaces.BUFFS_LARGE, 208, 32, 16, 16);
        }
        
        @Override
        public Visual secondaryVisual() {
            BitmapText txt = new BitmapText(PixelScene.pixelFont);
            txt.text(String.format("%.1f", chargeTime));
            txt.measure();
            txt.hardlight(0x00FFFF);
            return txt;
        }

        @Override
        public int indicatorColor() {
            if (chargeTime >= MAX_CHARGE) {
                return 0x00FF00;
            } else {
                return 0x00FFFF;
            }
        }

        @Override
        public void doAction() {
            Hero hero = Dungeon.hero;
            final SniperGun sniper;
            
            // 找到狙击枪
            if (hero.belongings.weapon instanceof SniperGun) {
                sniper = (SniperGun) hero.belongings.weapon;
            } else {
                SniperGun temp = null;
                for (SniperGun s : hero.belongings.getAllItems(SniperGun.class)) {
                    temp = s;
                    break;
                }
                sniper = temp;
            }
            
            if (sniper == null || sniper.ammo <= 0) {
                GLog.w("找不到可用的狙击枪或弹药不足！");
                detach();
                return;
            }
            
            GameScene.selectCell(new CellSelector.Listener() {
                @Override
                public void onSelect(Integer target) {
                    if (target != null) {
                        sniper.fire(target);
                    }
                }
                
                @Override
                public String prompt() {
                    return "选择射击目标";
                }
            });
        }
        
        @Override
        public int icon() {
            return BuffIndicator.PREPARATION;
        }
        
        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0x00FFFF);
        }
        
        @Override
        public float iconFadePercent() {
            return Math.max(0, 1 - chargeTime / MAX_CHARGE);
        }
        
        @Override
        public String toString() {
            return "狙击瞄准";
        }
        
        @Override
        public String desc() {
            String desc = "你正在瞄准，每回合增加蓄力，最多可以蓄力" + MAX_CHARGE + "秒。\n\n";
            
            desc += "当前蓄力：" + String.format("%.1f", chargeTime) + "秒 / " + MAX_CHARGE + "秒\n";
            desc += "伤害倍率：" + String.format("%.1f", Math.min(3f, 1f + chargeTime / MAX_CHARGE * 2f)) + "x\n";
            desc += "爆头几率：" + String.format("%.0f", chargeTime / MAX_CHARGE * 100) + "%\n";
            desc += "命中提升：" + String.format("%.1f", Math.min(4f, 1f + chargeTime / MAX_CHARGE * 3f)) + "x\n\n";
            
            if (chargeTime >= MAX_CHARGE) {
                desc += "_蓄力已满！可以射击了！_\n\n";
            } else {
                desc += "_继续保持站立姿势进行蓄力。_\n\n";
            }
            
            desc += "点击动作栏中的狙击图标以进行射击。\n";
            desc += "移动会重置蓄力。";
            
            return desc;
        }
        
        public boolean canMove() {
            return true; // 允许移动，但会重置蓄力
        }
        
        private static final String CHARGE_TIME = "chargeTime";
        private static final String LAST_POS = "lastPos";
        private static final String IS_CHARGING = "isCharging";
        
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(CHARGE_TIME, chargeTime);
            bundle.put(LAST_POS, lastPos);
            bundle.put(IS_CHARGING, isCharging);
        }
        
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            chargeTime = bundle.getFloat(CHARGE_TIME);
            lastPos = bundle.getInt(LAST_POS);
            isCharging = bundle.getBoolean(IS_CHARGING);
            
            if (target == Dungeon.hero) {
                ActionIndicator.setAction(this);
            }
        }
    }
}