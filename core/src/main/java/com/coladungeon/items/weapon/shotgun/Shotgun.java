/*
 * Cola Dungeon
 */

package com.coladungeon.items.weapon.shotgun;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Bleeding;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Paralysis;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.Flare;
import com.coladungeon.effects.Splash;
import com.coladungeon.effects.particles.BlastParticle;
import com.coladungeon.items.weapon.gun.Gun;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class Shotgun extends Gun {

    private static final float CONE_ANGLE = 60f; // 60度的锥形范围
    private static final int MAX_DISTANCE = 4; // 最大射程

    {
        image = ItemSpriteSheet.CROSSBOW;
        hitSound = Assets.Sounds.BLAST;
        hitSoundPitch = 1.2f;

        DLY = 0.5f;
        RCH = MAX_DISTANCE; // 设置最大射程
        ACC = 2f; // 提高命中率
        
        // 设置弹药相关参数
        maxAmmo = 6; // 弹夹容量
        ammo = maxAmmo; // 初始装满弹药
        reloadTime = 1.5f; // 装弹时间
    }

    @Override
    public float accuracyFactor(Char owner, Char target) {
        float acc = super.accuracyFactor(owner, target);
        
        // 距离越近，命中率越高
        float distance = Dungeon.level.distance(owner.pos, target.pos);
        acc *= (1.5f - distance * 0.1f);
        
        return acc;
    }

    @Override
    public int STRReq(int lvl) {
        return 10 + Math.round(lvl * 0.5f); // 降低基础力量需求和等级缩放
    }

    @Override
    public int min(int lvl) {
        return 4 + 2 * lvl;
    }

    @Override
    public int max(int lvl) {
        return 15 + 5 * lvl;
    }

    @Override
    protected void fire(int targetPos) {
        // 消耗弹药
        consumeAmmo(1);
        
        // 获取锥形范围内的目标格子
        ArrayList<Integer> targetCells = getConeTargets(curUser.pos, targetPos);
        
        // 对锥形范围内的所有敌人造成伤害
        for (int cell : targetCells) {
            Char target = Actor.findChar(cell);
            if (target != null && target != curUser) {
                // 计算距离衰减的伤害
                float distance = Dungeon.level.distance(curUser.pos, cell);
                float damageMultiplier = Math.max(0.2f, 1f - (distance * 0.2f)) * getAmmoDamageMultiplier();
                
                // 近距离额外伤害加成
                if (distance <= 1) {
                    damageMultiplier *= 1.5f;
                }
                
                int damage = Math.round(damageRoll(curUser) * damageMultiplier);
                
                // 在目标头上显示红色标记
                new Flare(6, 32).color(0xFF0000, true).show(target.sprite, 0.5f);
                
                // 造成伤害
                target.damage(damage, this);
                
                // 应用弹药效果
                applyAmmoEffects(target, damage);
                
                // 击退效果
                if (Random.Int(2) == 0) {
                    Buff.affect(target, Paralysis.class, 1f);
                }
                
                // 流血效果（近距离更容易触发）
                if (Random.Float() > distance * 0.25f) {
                    Buff.affect(target, Bleeding.class).set(Math.round(damage * 0.5f));
                }
                
                // 视觉效果
                CellEmitter.center(cell).burst(BlastParticle.FACTORY, 4);
                Splash.at(target.sprite.center(), 0xFFBB0000, 5);
            }
        }
        
        // 播放音效
        Sample.INSTANCE.play(Assets.Sounds.BLAST, 0.8f, Random.Float(0.85f, 1.15f));
        
        // 添加射击光束特效
        shootBeam(targetPos);
        
        // 让英雄完成动作
        curUser.spendAndNext(1f);
    }

    private ArrayList<Integer> getConeTargets(int attackerPos, int targetPos) {
        ArrayList<Integer> cone = new ArrayList<>();
        
        // 计算攻击者到目标的方向向量
        Point attackerPt = new Point(attackerPos % Dungeon.level.width(), attackerPos / Dungeon.level.width());
        Point targetPt = new Point(targetPos % Dungeon.level.width(), targetPos / Dungeon.level.width());
        PointF direction = new PointF(targetPt.x - attackerPt.x, targetPt.y - attackerPt.y);
        direction.normalize();
        
        // 检查周围所有格子
        for (int dist = 1; dist <= MAX_DISTANCE; dist++) {
            // 获取当前距离的所有相邻格子
            int[] neighbours = PathFinder.NEIGHBOURS9;
            for (int i = 0; i < neighbours.length; i++) {
                for (int j = -dist; j <= dist; j++) {
                    int cell = attackerPos + neighbours[i] * dist + j * Dungeon.level.width();
                    
                    if (Dungeon.level.insideMap(cell)) {
                        Point cellPt = new Point(cell % Dungeon.level.width(), cell / Dungeon.level.width());
                        PointF cellDir = new PointF(cellPt.x - attackerPt.x, cellPt.y - attackerPt.y);
                        
                        if (cellDir.length() > 0) {
                            cellDir.normalize();
                            
                            // 计算向量之间的角度
                            float angle = Math.abs((float) Math.toDegrees(
                                Math.atan2(direction.x * cellDir.y - direction.y * cellDir.x,
                                         direction.x * cellDir.x + direction.y * cellDir.y)));
                            
                            // 如果在锥形范围内
                            if (angle <= CONE_ANGLE/2) {
                                cone.add(cell);
                                
                                // 添加视觉效果
                                CellEmitter.get(cell).burst(BlastParticle.FACTORY, 2);
                            }
                        }
                    }
                }
            }
        }
        
        return cone;
    }

    @Override
    public String name() {
        return "霰弹枪";
    }

    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder();
        desc.append("一把威力巨大的霰弹枪，能对近距离的敌人造成毁灭性的伤害。\n\n");
        
        desc.append("_被动效果:_\n");
        desc.append("- 基础命中率提升100%\n");
        desc.append("- 射程4格\n");
        desc.append("- 锥形攻击范围，可同时命中多个敌人\n");
        desc.append("- 距离越近，命中率和伤害越高\n\n");
        
        desc.append("_升级效果:_\n");
        desc.append("- 基础伤害：").append(min(0)).append("-").append(max(0)).append("\n");
        desc.append("- 力量需求：").append(STRReq(0)).append("\n");
        desc.append("- 每级提升：\n");
        desc.append("  * 最小伤害+2\n");
        desc.append("  * 最大伤害+5\n");
        desc.append("  * 力量需求+0.5");
        
        return desc.toString();
    }
} 