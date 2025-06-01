package com.coladungeon.items.weapon.grenade;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.items.weapon.ammo.Ammo;
import com.coladungeon.actors.buffs.Paralysis;
import com.coladungeon.actors.buffs.Vertigo;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.particles.BlastParticle;
import com.coladungeon.effects.particles.SmokeParticle;
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
//和Gun别无二致，除了对Ammo特别要求为Grenade

public class GrenadeLauncher extends Gun {

    {
        image = ItemSpriteSheet.CROSSBOW; // 暂时使用十字弩的图标

        defaultAction = AC_FIRE; // 默认动作为开火

        maxAmmo = 6; // 最大弹药量
        ammo = maxAmmo; // 初始弹药量
        reloadTime = 2f; // 装弹时间
    }

    @Override
    public String name() {
        return "榴弹发射器";
    }

    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder();
        desc.append("一把威力巨大的榴弹发射器，可以发射爆炸榴弹造成范围伤害。\n\n");

        desc.append("- 发射一枚爆炸榴弹\n");
        desc.append("- 命中点附近2格范围内造成爆炸伤害\n");
        desc.append("- 爆炸会击退并眩晕敌人\n");
        desc.append("- 消耗1发弹药\n\n");

        return desc.toString();
    }

    @Override
    protected void fire(int targetPos) {
        if (ammo <= 0) {
            GLog.w("弹药不足！");
            return;
        }

        final Ballistica shot = new Ballistica(curUser.pos, targetPos, Ballistica.STOP_TARGET);
        int cell = shot.collisionPos;

        curUser.sprite.zap(cell);
        Sample.INSTANCE.play(Assets.Sounds.MISS, 0.6f, 0.6f);

        curUser.sprite.operate(curUser.pos, () -> {
            curUser.spendAndNext(DLY);
            EventBus.fire("Grenade:explode", "where", cell, "which", this);
            // explode(cell);
            updateQuickslot();
        });
    }

    // 只允许Grenade类型弹药
    @Override
    public boolean canLoad(Ammo ammo) {
        return true;
    }

    //@deprecated
    // private void explode(int cell) {
    //     Sample.INSTANCE.play(Assets.Sounds.BLAST);
    //     Camera.main.shake(3, 0.7f);

    //     // 爆炸粒子效果
    //     CellEmitter.get(cell).burst(BlastParticle.FACTORY, 30);

    //     // 爆炸后的烟雾效果
    //     for (int i = 0; i < 10; i++) {
    //         int c = -1;
    //         // 随机选择相邻格子
    //         int[] neighbours = PathFinder.NEIGHBOURS8.clone();
    //         for (int j = 0; j < neighbours.length; j++) {
    //             int k = Random.Int(j, neighbours.length);
    //             int temp = neighbours[j];
    //             neighbours[j] = neighbours[k];
    //             neighbours[k] = temp;
    //         }
    //         for (int n : neighbours) {
    //             int candidate = cell + n;
    //             if (candidate >= 0 && candidate < Dungeon.level.length() && !Dungeon.level.solid[candidate]) {
    //                 c = candidate;
    //                 break;
    //             }
    //         }
    //         if (c != -1) {
    //             CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
    //             CellEmitter.center(c).burst(BlastParticle.FACTORY, 5);
    //         }
    //     }

    //     boolean terrainAffected = false;

    //     // 造成范围伤害
    //     for (int n : PathFinder.NEIGHBOURS9) {
    //         int c = cell + n;
    //         if (c >= 0 && c < Dungeon.level.length()) {
    //             if (Dungeon.level.heroFOV[c]) {
    //                 CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
    //                 CellEmitter.get(c).burst(BlastParticle.FACTORY, 5);
    //             }

    //             Char ch = Actor.findChar(c);
    //             if (ch != null) {
    //                 // 角色离爆炸中心越近，伤害越高
    //                 float distFactor = (n == 0) ? 1 : 0.5f;
    //                 int damage = Math.round(Random.NormalIntRange(min(), max()) * distFactor);

    //                 ch.damage(damage, this);

    //                 // 爆炸眩晕
    //                 if (ch.isAlive() && !ch.properties().contains(Char.Property.IMMOVABLE)) {
    //                     Buff.prolong(ch, Paralysis.class, 2f);
    //                     if (n != 0) { // 不在中心点
    //                         Buff.prolong(ch, Vertigo.class, 3f);
    //                     }
    //                 }
    //             }

    //             // 爆炸会摧毁一些地形
    //             if (Dungeon.level.flamable[c]) {
    //                 Dungeon.level.destroy(c);
    //                 GameScene.updateMap(c);
    //                 terrainAffected = true;
    //             }
    //         }
    //     }

    //     // 扩展爆炸范围
    //     for (int n : PathFinder.NEIGHBOURS8) {
    //         int c = cell + n;
    //         if (c >= 0 && c < Dungeon.level.length()) {
    //             for (int nn : PathFinder.NEIGHBOURS8) {
    //                 int cc = c + nn;
    //                 if (cc >= 0 && cc < Dungeon.level.length() && Random.Float() < 0.5f) {
    //                     if (Dungeon.level.heroFOV[cc]) {
    //                         CellEmitter.get(cc).burst(SmokeParticle.FACTORY, 2);
    //                     }

    //                     Char ch = Actor.findChar(cc);
    //                     if (ch != null) {
    //                         // 二级伤害范围
    //                         int damage = Math.round(Random.NormalIntRange(min() / 3, max() / 3));
    //                         ch.damage(damage, this);

    //                         if (ch.isAlive() && !ch.properties().contains(Char.Property.IMMOVABLE)) {
    //                             Buff.prolong(ch, Vertigo.class, 2f);
    //                         }
    //                     }
    //                 }
    //             }
    //         }
    //     }

    //     if (terrainAffected) {
    //         Dungeon.observe();
    //     }
    // }

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
}
