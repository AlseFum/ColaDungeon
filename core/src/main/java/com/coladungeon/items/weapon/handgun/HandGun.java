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
import com.coladungeon.items.weapon.Weapon;
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

import java.util.ArrayList;

public class HandGun extends Weapon {

    private static final int MAX_AMMO = 12; // 最大弹药量
    private int ammo = MAX_AMMO; // 初始弹药量

    {
        image = ItemSpriteSheet.CROSSBOW; // 暂时使用十字弩的图标
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch = 1.4f;

        DLY = 0.5f; // 较快的攻击速度
        RCH = 8; // 射程
        ACC = 1.2f; // 命中率
        
        defaultAction = AC_QUICK_SHOT; // 默认动作为快速射击
    }

    private static final String AC_QUICK_SHOT = "快速射击";
    private static final String AC_RELOAD = "装弹";

    @Override
    public String name() {
        return "手枪";
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_QUICK_SHOT)) return "快速射击";
        if (action.equals(AC_RELOAD)) return "装弹";
        return super.actionName(action, hero);
    }

    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder();
        desc.append("一把小巧灵活的手枪，可以快速拔出并射击。\n\n");
        
        desc.append("_被动效果:_\n");
        desc.append("- 基础命中率提升20%\n");
        desc.append("- 射程8格\n\n");
        
        desc.append("_主动技能 - 快速射击:_\n");
        desc.append("- 即使未装备也能直接使用\n");
        desc.append("- 自动装备并立即射击\n");
        desc.append("- 50%几率造成暴击（200%伤害）\n");
        desc.append("- 消耗1发弹药\n\n");
        
        desc.append("_弹药系统:_\n");
        desc.append("- 最大弹药：").append(MAX_AMMO).append("发\n");
        desc.append("- 当前弹药：").append(ammo).append("发\n");
        desc.append("- 装弹时间：1秒\n\n");
        
        desc.append("_升级效果:_\n");
        desc.append("- 基础伤害：").append(min()).append("-").append(max()).append("\n");
        desc.append("- 力量需求：").append(STRReq()).append("\n");
        desc.append("- 每级提升：\n");
        desc.append("  * 最小伤害+2\n");
        desc.append("  * 最大伤害+3\n");
        desc.append("  * 力量需求+0.5");
        
        return desc.toString();
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_QUICK_SHOT);
        actions.add(AC_RELOAD);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        
        if (action.equals(AC_QUICK_SHOT)) {
            if (ammo <= 0) {
                GLog.w("弹药不足！");
                return;
            }
            
            if (!isEquipped(hero)) {
                // 如果没有装备，先装备
                if (!doEquip(hero)) {
                    return;
                }
            }
            
            // 进入选择目标模式
            GameScene.selectCell(shooter);
            
        } else if (action.equals(AC_RELOAD)) {
            if (ammo >= MAX_AMMO) {
                GLog.w("弹药已满！");
                return;
            }
            reload();
        }
    }

    private void reload() {
        ammo = MAX_AMMO;
        GLog.p("重新装填完成！弹药：%d/%d", ammo, MAX_AMMO);
        curUser.spend(0.5f);
        curUser.busy();
        curUser.sprite.operate(curUser.pos);
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                fire(target);
            }
        }
        
        @Override
        public String prompt() {
            return "选择射击目标";
        }
    };

    private void fire(int targetPos) {
        if (ammo <= 0) {
            GLog.w("弹药不足！");
            return;
        }

        final Ballistica shot = new Ballistica(curUser.pos, targetPos, Ballistica.PROJECTILE);
        int cell = shot.collisionPos;

        curUser.sprite.zap(cell);
        Sample.INSTANCE.play(Assets.Sounds.HIT);
        
        // 添加射击光束特效
        curUser.sprite.parent.add(new Lightning(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(cell), null));
        
        Char enemy = Actor.findChar(cell);
        if (enemy != null) {
            boolean crit = Random.Float() < 0.5f;
            float damageMultiplier = crit ? 2f : 1f;
            
            int damage = Math.round(damageRoll(curUser) * damageMultiplier);
            
            if (crit) {
                GLog.p("暴击！");
            }
            
            enemy.damage(damage, this);
        }
        
        // 消耗弹药
        ammo--;
        if (ammo <= 0) {
            GLog.w("弹药耗尽！");
        }
        
        curUser.spendAndNext(0.5f);
    }

    @Override
    public int STRReq(int lvl) {
        return 6 + Math.round(lvl * 0.5f); // 较低的力量需求
    }

    @Override
    public int min(int lvl) {
        return 5 + 2 * lvl; // 较低的基础伤害
    }

    @Override
    public int max(int lvl) {
        return 15 + 4 * lvl; // 较低的最大伤害
    }

    @Override
    public String status() {
        return ammo + "/" + MAX_AMMO;
    }

    private static final String AMMO = "ammo";
    
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(AMMO, ammo);
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        ammo = bundle.getInt(AMMO);
    }
} 