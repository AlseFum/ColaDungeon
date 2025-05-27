/*
 * Cola Dungeon
 */

package com.coladungeon.items.weapon.handgun;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.FlavourBuff;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.Beam;
import com.coladungeon.effects.Lightning;
import com.coladungeon.items.Item;
import com.coladungeon.items.weapon.gun.Gun;
import com.coladungeon.items.weapon.ammo.Ammo;
import com.coladungeon.mechanics.Ballistica;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.tiles.DungeonTilemap;
import com.coladungeon.ui.ActionIndicator;
import com.coladungeon.ui.BuffIndicator;
import com.coladungeon.ui.HeroIcon;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.coladungeon.items.weapon.rifle.Rifle;
import com.coladungeon.effects.MagicMissile;
import com.coladungeon.effects.Splash;
import com.coladungeon.effects.Wound;
import com.coladungeon.effects.Flare;

import java.util.ArrayList;

/**
 * 手枪 - 一种轻型枪械，具有快速射击能力
 */
public class HandGun extends Gun {

    private static final String AC_QUICKDRAW = "快速射击";
    private static final float QUICKDRAW_SPEED = 0.5f; // 快速射击的延迟时间

    {
        image = ItemSpriteSheet.CROSSBOW; // 临时图标，需要替换为手枪图标
        defaultAction = AC_SHOOT;
        
        // 设置弹药相关属性
        maxAmmo = 10;
        ammo = maxAmmo;
        defaultAmmoType = Ammo.AmmoType.NORMAL;
        loadedAmmoType = defaultAmmoType;
        
        // 手枪属性
        DLY = 0.5f;     // 基础射击延迟
        RCH = 6;        // 射程
        ACC = 1.2f;     // 精准度
        
        usesTargeting = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_QUICKDRAW);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        
        if (action.equals(AC_QUICKDRAW)) {
            if (ammo < 1) {
                GLog.w("弹药不足！");
                return;
            }
            
            // 进入快速射击目标选择模式
            GameScene.selectCell(quickdrawShooter);
        }
    }

    @Override
    protected void fire(int targetPos) {
        if (ammo < 1) {
            GLog.w("弹药不足！");
            return;
        }

        Char ch = Actor.findChar(targetPos);
        
        // 计算弹道
        Ballistica shot = new Ballistica(curUser.pos, targetPos, Ballistica.PROJECTILE);
        int cell = shot.collisionPos;

        // 射击特效
        curUser.sprite.zap(cell);
        
        // 子弹轨迹特效
        MagicMissile.boltFromChar(curUser.sprite.parent,
                MagicMissile.FORCE,
                curUser.sprite,
                cell,
                () -> {
                    // 击中特效
                    if (ch != null) {
                        int dmg = damageRoll(ch);
                        ch.damage(dmg, this);
                        
                        // 显示伤害数字和击中效果
                        ch.sprite.bloodBurstA(ch.sprite.center(), dmg);
                        ch.sprite.flash();
                        Wound.hit(ch);
                    }
                    
                    // 击中点溅射特效
                    Splash.at(DungeonTilemap.tileCenterToWorld(cell), 0xFFFFFF, 10);
                    Sample.INSTANCE.play(Assets.Sounds.HIT);
                    
                    // 消耗弹药
                    ammo--;
                    curUser.spendAndNext(DLY);
                });
    }

    // 快速射击监听器
    private CellSelector.Listener quickdrawShooter = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                // 快速射击特效
                Flare flare = new Flare(6, 32);
                flare.color(0xFFFF00, true);
                flare.show(curUser.sprite, 0.5f);
                
                // 使用更短的延迟进行射击
                float originalDLY = DLY;
                DLY = QUICKDRAW_SPEED;
                fire(target);
                DLY = originalDLY;
            }
        }

        @Override
        public String prompt() {
            return "选择快速射击目标";
        }
    };

    @Override
    public int STRReq(int lvl) {
        return 8 + lvl; // 手枪力量需求较低
    }

    @Override
    public int min(int lvl) {
        return 3 + lvl; // 基础最小伤害3，每级+1
    }

    @Override
    public int max(int lvl) {
        return 6 + 3 * lvl; // 基础最大伤害6，每级+3
    }

    @Override
    public String name() {
        return "手枪";
    }
    
    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder();
        desc.append("一把轻巧的手枪，虽然伤害不及步枪，但具有快速射击能力。\n\n");
        
        desc.append("_属性：_\n");
        desc.append("- 射程：").append(RCH).append("格\n");
        desc.append("- 精准度：+").append((int)((ACC-1)*100)).append("%\n");
        desc.append("- 弹夹容量：").append(maxAmmo).append("发\n\n");
        
        desc.append("_快速射击：_\n");
        desc.append("- 以更快的速度进行单发射击\n");
        desc.append("- 射击延迟减少50%\n\n");
        
        desc.append("_当前状态：_\n");
        desc.append("- 弹药：").append(ammo).append("/").append(maxAmmo).append("\n");
        desc.append("- 伤害：").append(min()).append("-").append(max()).append("\n");
        desc.append("- 力量需求：").append(STRReq());
        
        return desc.toString();
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        try {
            super.restoreFromBundle(bundle);
        } catch (Exception e) {
            GLog.w("恢复HandGun出错，使用默认值");
            // 设置合理的默认值
            ammo = 0;
            maxAmmo = 10;
            defaultAmmoType = Ammo.AmmoType.NORMAL;
            loadedAmmoType = defaultAmmoType;
        }
    }

    // 新方法：转换为Rifle
    public Rifle convertToRifle() {
        Rifle rifle = new Rifle();
        
        // 使用Bundle来安全地传输数据
        Bundle bundle = new Bundle();
        this.storeInBundle(bundle);
        rifle.restoreFromBundle(bundle);
        
        // 设置基本属性
        rifle.level(this.level());
        
        GLog.i("你的手枪已升级为步枪！");
        return rifle;
    }

    @Override
    public boolean doEquip(Hero hero) {
        // 当装备时自动转换为Rifle
        Rifle rifle = convertToRifle();
        if (rifle.doEquip(hero)) {
            // 转换成功，移除旧的HandGun
            detach(hero.belongings.backpack);
            return true;
        }
        return false;
    }
} 