/*
 * Cola Dungeon
 */

package com.coladungeon.items.weapon.rifle;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.FlavourBuff;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.Beam;
import com.coladungeon.effects.Lightning;
import com.coladungeon.items.weapon.Weapon;
import com.coladungeon.mechanics.Ballistica;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.tiles.DungeonTilemap;
import com.coladungeon.ui.ActionIndicator;
import com.coladungeon.ui.BuffIndicator;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Rifle extends Weapon {

    private static final int MAX_AMMO = 30; // 最大弹药量
    private int ammo = MAX_AMMO; // 初始弹药量
    private static final int SHOTS_PER_BURST = 3; // 每次射击发射的子弹数
    private static final int SPRAY_AMMO_COST = 10; // 扫射消耗的弹药量
    private static final float SPRAY_CHANCE = 0.4f; // 扫射命中周围格子的基础概率

    {
        image = ItemSpriteSheet.CROSSBOW; // 暂时使用十字弩的图标
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch = 1.2f;

        DLY = 0.75f; // 射击间隔
        RCH = 8; // 射程
        ACC = 1.1f; // 命中率
        
        defaultAction = AC_SHOOT; // 默认动作为射击
    }

    private static final String AC_SHOOT = "点射";
    private static final String AC_SPRAY = "扫射";
    private static final String AC_RELOAD = "装弹";

    @Override
    public String name() {
        return "步枪";
    }

    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder();
        desc.append("一把自动步枪，可以连续发射多发子弹。\n\n");
        
        desc.append("_被动效果:_\n");
        desc.append("- 基础命中率提升10%\n");
        desc.append("- 射程8格\n\n");
        
        desc.append("_主动技能 - 点射:_\n");
        desc.append("- 一次发射3发子弹\n");
        desc.append("- 每发子弹独立计算伤害\n");
        desc.append("- 消耗3发弹药\n\n");
        
        desc.append("_主动技能 - 扫射:_\n");
        desc.append("- 以目标为中心进行范围扫射\n");
        desc.append("- 中心点必定命中\n");
        desc.append("- 周围8格有40%几率命中敌人\n");
        desc.append("- 消耗10发弹药\n\n");
        
        desc.append("_弹药系统:_\n");
        desc.append("- 最大弹药：").append(MAX_AMMO).append("发\n");
        desc.append("- 当前弹药：").append(ammo).append("发\n");
        desc.append("- 装弹时间：1秒\n\n");
        
        desc.append("_升级效果:_\n");
        desc.append("- 基础伤害：").append(min()).append("-").append(max()).append("\n");
        desc.append("- 力量需求：").append(STRReq()).append("\n");
        desc.append("- 每级提升：\n");
        desc.append("  * 最小伤害+2\n");
        desc.append("  * 最大伤害+4\n");
        desc.append("  * 力量需求+0.5");
        
        return desc.toString();
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_SHOOT);
        actions.add(AC_SPRAY);
        actions.add(AC_RELOAD);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_SHOOT)) return "点射";
        if (action.equals(AC_SPRAY)) return "扫射";
        if (action.equals(AC_RELOAD)) return "装弹";
        return super.actionName(action, hero);
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        
        if (action.equals(AC_SHOOT)) {
            if (ammo < SHOTS_PER_BURST) {
                GLog.w("弹药不足！需要%d发子弹。", SHOTS_PER_BURST);
                return;
            }
            
            // 进入选择目标模式
            GameScene.selectCell(shooter);
            
        } else if (action.equals(AC_SPRAY)) {
            if (ammo < SPRAY_AMMO_COST) {
                GLog.w("弹药不足！需要%d发子弹。", SPRAY_AMMO_COST);
                return;
            }
            
            // 进入选择目标模式
            GameScene.selectCell(sprayer);
            
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
        curUser.spend(1f);
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
            return "选择点射目标";
        }
    };

    private CellSelector.Listener sprayer = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                spray(target);
            }
        }
        
        @Override
        public String prompt() {
            return "选择扫射中心点";
        }
    };

    private void fire(int targetPos) {
        if (ammo < SHOTS_PER_BURST) {
            GLog.w("弹药不足！");
            return;
        }

        final Ballistica shot = new Ballistica(curUser.pos, targetPos, Ballistica.PROJECTILE);
        int cell = shot.collisionPos;

        // 消耗弹药
        ammo -= SHOTS_PER_BURST;

        curUser.sprite.zap(cell);
        Sample.INSTANCE.play(Assets.Sounds.HIT);

        // 添加射击光束特效
        curUser.sprite.parent.add(new Lightning(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(cell), null));

        Char enemy = Actor.findChar(cell);
        if (enemy != null) {
            // 进行多次伤害计算
            for (int i = 0; i < SHOTS_PER_BURST; i++) {
                int damage = damageRoll(curUser);
                enemy.damage(damage, this);
                
                if (i < SHOTS_PER_BURST - 1) {
                    // 在连发之间添加小延迟的音效
                    float pitch = 0.8f + Random.Float(0.4f);
                    Sample.INSTANCE.play(Assets.Sounds.HIT, pitch);
                }
            }
        }
        
        // 只消耗一次行动时间
        curUser.spendAndNext(DLY);
    }

    private void spray(int targetPos) {
        if (ammo < SPRAY_AMMO_COST) {
            GLog.w("弹药不足！");
            return;
        }

        // 消耗弹药
        ammo -= SPRAY_AMMO_COST;

        curUser.sprite.zap(targetPos);
        Sample.INSTANCE.play(Assets.Sounds.HIT);

        // 获取目标周围的格子
        boolean[] passable = Dungeon.level.passable;
        
        // 对中心点造成必定伤害
        Char centerEnemy = Actor.findChar(targetPos);
        if (centerEnemy != null) {
            int damage = damageRoll(curUser);
            centerEnemy.damage(damage, this);
            curUser.sprite.parent.add(new Lightning(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(targetPos), null));
        }

        // 对周围一格进行概率伤害
        int[] neighbours = PathFinder.NEIGHBOURS8;
        for (int i = 0; i < neighbours.length; i++) {
            int pos = targetPos + neighbours[i];
            if (passable[pos]) {
                Char enemy = Actor.findChar(pos);
                if (enemy != null && Random.Float() < SPRAY_CHANCE) {
                    int damage = damageRoll(curUser);
                    enemy.damage(damage, this);
                    curUser.sprite.parent.add(new Lightning(
                        DungeonTilemap.raisedTileCenterToWorld(targetPos),
                        DungeonTilemap.raisedTileCenterToWorld(pos),
                        null));
                    Sample.INSTANCE.play(Assets.Sounds.HIT, 0.8f + Random.Float(0.4f));
                }
            }
        }

        // 只消耗一次行动时间
        curUser.spendAndNext(DLY);
    }

    @Override
    public int STRReq(int lvl) {
        return 8 + Math.round(lvl * 0.5f); // 较低的力量需求
    }

    @Override
    public int min(int lvl) {
        return 3 + 2 * lvl; // 较低的单发伤害
    }

    @Override
    public int max(int lvl) {
        return 8 + 4 * lvl; // 较低的单发伤害
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