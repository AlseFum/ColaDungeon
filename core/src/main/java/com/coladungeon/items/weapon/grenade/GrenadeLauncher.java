package com.coladungeon.items.weapon.grenade;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Paralysis;
import com.coladungeon.actors.buffs.Vertigo;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.particles.BlastParticle;
import com.coladungeon.effects.particles.SmokeParticle;
import com.coladungeon.items.weapon.ammo.Ammo;
import com.coladungeon.items.weapon.gun.Gun;
import com.coladungeon.mechanics.Ballistica;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.EventBus;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class GrenadeLauncher extends Gun {

    private static final int BLAST_RADIUS = 2; // 爆炸范围

    private static final String AC_FIRE = "开火";
    private static final String AC_RELOAD = "装弹";

    {
        image = ItemSpriteSheet.CROSSBOW; // 暂时使用十字弩的图标
        hitSound = Assets.Sounds.BLAST;
        hitSoundPitch = 0.8f;

        DLY = 1.5f; // 较慢的攻击速度
        RCH = 8; // 射程
        ACC = 0.8f; // 命中率较低

        defaultAction = AC_FIRE; // 默认动作为开火

        maxAmmo = 3; // 最大弹药量
        ammo = maxAmmo; // 初始弹药量
        reloadTime = 2f; // 装弹时间
        defaultAmmoType = Ammo.AmmoType.EXPLOSIVE; // 使用爆炸弹
    }

    @Override
    protected void addGunActions(Hero hero, ArrayList<String> actions) {
        // actions.add(AC_SPRAY);
    }

    @Override
    protected void executeGunAction(Hero hero, String action) {
        // if (action.equals(AC_SPRAY)) {
        //     if (ammo < 2) {
        //         GLog.w("弹药不足！需要至少2发弹药进行扫射！");
        //         return;
        //     }
        //     GameScene.selectCell(new CellSelector.Listener() {
        //         @Override
        //         public void onSelect(Integer target) {
        //             if (target != null) {
        //                 sprayFire(target);
        //             }
        //         }

        //         @Override
        //         public String prompt() {
        //             return "选择扫射目标区域";
        //         }
        //     });
        // }
    }

    // private void sprayFire(int targetPos) {
    //     final Ballistica shot = new Ballistica(curUser.pos, targetPos, Ballistica.STOP_SOLID);
    //     int cell = shot.collisionPos;
    //     curUser.sprite.zap(cell);
    //     Sample.INSTANCE.play(Assets.Sounds.BLAST, 0.6f, 0.6f);
    //     curUser.sprite.operate(curUser.pos, new Callback() {
    //         @Override
    //         public void call() {
    //             // 扫射消耗2发弹药，造成更大范围的伤害
    //             explode(cell);
    //             // 对周围额外造成爆炸
    //             for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
    //                 if (Random.Float() < 0.5f) {
    //                     int c = cell + PathFinder.NEIGHBOURS8[i];
    //                     if (c >= 0 && c < Dungeon.level.length() && !Dungeon.level.solid[c]) {
    //                         explode(c);
    //                     }
    //                 }
    //             }
    //             consumeAmmo(2);
    //             curUser.spendAndNext(DLY * 1.5f);
    //         }
    //     });
    // }
    @Override
    public String name() {
        return "榴弹发射器";
    }

    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder();
        desc.append("一把威力巨大的榴弹发射器，可以发射爆炸榴弹造成范围伤害。\n\n");

        desc.append("_被动效果:_\n");
        desc.append("- 弹药容量较少，但伤害巨大\n");
        desc.append("- 击中造成大范围爆炸伤害\n\n");

        desc.append("_主动技能 - 爆炸榴弹:_\n");
        desc.append("- 发射一枚爆炸榴弹\n");
        desc.append("- 命中点附近2格范围内造成爆炸伤害\n");
        desc.append("- 爆炸会击退并眩晕敌人\n");
        desc.append("- 消耗1发弹药\n\n");

        desc.append("_主动技能 - 扫射:_\n");
        desc.append("- 发射多枚榴弹进行扫射\n");
        desc.append("- 在目标区域造成多次爆炸\n");
        desc.append("- 有50%几率在相邻格子产生额外爆炸\n");
        desc.append("- 消耗2发弹药\n\n");

        desc.append("_弹药系统:_\n");
        desc.append("- 最大弹药：").append(maxAmmo).append("发\n");
        desc.append("- 当前弹药：").append(ammo).append("发\n");
        desc.append("- 装弹时间：2秒\n\n");

        desc.append("_升级效果:_\n");
        desc.append("- 基础伤害：").append(min()).append("-").append(max()).append("\n");
        desc.append("- 力量需求：").append(STRReq()).append("\n");
        desc.append("- 每级提升：\n");
        desc.append("  * 最小伤害+5\n");
        desc.append("  * 最大伤害+10\n");
        desc.append("  * 力量需求+1");

        return desc.toString();
    }

    @Override
    protected void fire(int targetPos) {
        if (ammo <= 0) {
            GLog.w("弹药不足！");
            return;
        }

        final Ballistica shot = new Ballistica(curUser.pos, targetPos, Ballistica.STOP_SOLID);
        int cell = shot.collisionPos;

        curUser.sprite.zap(cell);
        Sample.INSTANCE.play(Assets.Sounds.MISS, 0.6f, 0.6f);

        curUser.sprite.operate(curUser.pos, () -> {
            
            consumeAmmo(1);
            curUser.spendAndNext(DLY);
            EventBus.fire("Grenade:explode", "where", cell,"which",this);
            explode(cell);
        });
    }
    private void explode(int cell) {
        Sample.INSTANCE.play(Assets.Sounds.BLAST);
        Camera.main.shake(3, 0.7f);

        // 爆炸粒子效果
        CellEmitter.get(cell).burst(BlastParticle.FACTORY, 30);

        // 爆炸后的烟雾效果
        for (int i = 0; i < 10; i++) {
            int c = -1;
            // 随机选择相邻格子
            int[] neighbours = PathFinder.NEIGHBOURS8.clone();
            for (int j = 0; j < neighbours.length; j++) {
                int k = Random.Int(j, neighbours.length);
                int temp = neighbours[j];
                neighbours[j] = neighbours[k];
                neighbours[k] = temp;
            }
            for (int n : neighbours) {
                int candidate = cell + n;
                if (candidate >= 0 && candidate < Dungeon.level.length() && !Dungeon.level.solid[candidate]) {
                    c = candidate;
                    break;
                }
            }
            if (c != -1) {
                CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
                CellEmitter.center(c).burst(BlastParticle.FACTORY, 5);
            }
        }

        boolean terrainAffected = false;

        // 造成范围伤害
        for (int n : PathFinder.NEIGHBOURS9) {
            int c = cell + n;
            if (c >= 0 && c < Dungeon.level.length()) {
                if (Dungeon.level.heroFOV[c]) {
                    CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
                    CellEmitter.get(c).burst(BlastParticle.FACTORY, 5);
                }

                Char ch = Actor.findChar(c);
                if (ch != null) {
                    // 角色离爆炸中心越近，伤害越高
                    float distFactor = (n == 0) ? 1 : 0.5f;
                    int damage = Math.round(Random.NormalIntRange(min(), max()) * distFactor);

                    ch.damage(damage, this);

                    // 爆炸眩晕
                    if (ch.isAlive() && !ch.properties().contains(Char.Property.IMMOVABLE)) {
                        Buff.prolong(ch, Paralysis.class, 2f);
                        if (n != 0) { // 不在中心点
                            Buff.prolong(ch, Vertigo.class, 3f);
                        }
                    }
                }

                // 爆炸会摧毁一些地形
                if (Dungeon.level.flamable[c]) {
                    Dungeon.level.destroy(c);
                    GameScene.updateMap(c);
                    terrainAffected = true;
                }
            }
        }

        // 扩展爆炸范围
        for (int n : PathFinder.NEIGHBOURS8) {
            int c = cell + n;
            if (c >= 0 && c < Dungeon.level.length()) {
                for (int nn : PathFinder.NEIGHBOURS8) {
                    int cc = c + nn;
                    if (cc >= 0 && cc < Dungeon.level.length() && Random.Float() < 0.5f) {
                        if (Dungeon.level.heroFOV[cc]) {
                            CellEmitter.get(cc).burst(SmokeParticle.FACTORY, 2);
                        }

                        Char ch = Actor.findChar(cc);
                        if (ch != null) {
                            // 二级伤害范围
                            int damage = Math.round(Random.NormalIntRange(min() / 3, max() / 3));
                            ch.damage(damage, this);

                            if (ch.isAlive() && !ch.properties().contains(Char.Property.IMMOVABLE)) {
                                Buff.prolong(ch, Vertigo.class, 2f);
                            }
                        }
                    }
                }
            }
        }

        if (terrainAffected) {
            Dungeon.observe();
        }
    }

    @Override
    public int STRReq(int lvl) {
        return 12 + lvl; // 较高的力量需求
    }

    @Override
    public int min(int lvl) {
        return 15 + 5 * lvl; // 较高的基础伤害
    }

    @Override
    public int max(int lvl) {
        return 35 + 10 * lvl; // 较高的最大伤害
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_FIRE)) {
            return "开火";
        }
        if (action.equals(AC_RELOAD)) {
            return "装弹";
        }
        return super.actionName(action, hero);
    }
}
