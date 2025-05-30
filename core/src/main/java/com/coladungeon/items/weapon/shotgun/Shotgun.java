/*
 * Cola Dungeon
 */

package com.coladungeon.items.weapon.shotgun;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Paralysis;
import com.coladungeon.effects.MagicMissile;
import com.coladungeon.items.wands.WandOfBlastWave;
import com.coladungeon.items.weapon.gun.Gun;
import com.coladungeon.mechanics.Ballistica;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.EventBus;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
public class Shotgun extends Gun {
    //FIXME pellet should depend con cartridge
    private static final float CONE_ANGLE = 60f; // 60度的锥形范围
    private static final int MAX_DISTANCE = 4; // 最大射程
    private static final int PELLET_COUNT = 8; // 霰弹数量

    {
        image = ItemSpriteSheet.CROSSBOW;
        hitSound = Assets.Sounds.BLAST;
        hitSoundPitch = 1.2f;

        DLY = 1f;
        RCH = 1; // RCH只是近战伤害
        ACC = 1f; // 提高命中率
        
        // 设置弹药相关参数
        maxAmmo = 12; // 弹夹容量
        ammo = maxAmmo; // 初始装满弹药
        reloadTime = 1.5f; // 装弹时间
        usesTargeting = true;
    }
    static {
        EventBus.register("Hero:created",(hero)->{
            new Shotgun().collect();
            return null;
        });
    }

    @Override
    public float accuracyFactor(Char owner, Char target) {
        return 1.0f;
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
        
        // 计算基础方向
        PointF baseDirection = new PointF(
            (targetPos % Dungeon.level.width()) - (curUser.pos % Dungeon.level.width()),
            (targetPos / Dungeon.level.width()) - (curUser.pos / Dungeon.level.width())
        ).normalize();
        
        // 发射多颗霰弹
        for (int i = 0; i < PELLET_COUNT; i++) {
            // 计算偏移角度
            float angle = Random.Float(-CONE_ANGLE/2, CONE_ANGLE/2);
            float rad = (float) Math.toRadians(angle);
            
            // 计算旋转后的方向
            PointF direction = new PointF(
                baseDirection.x * (float)Math.cos(rad) - baseDirection.y * (float)Math.sin(rad),
                baseDirection.x * (float)Math.sin(rad) + baseDirection.y * (float)Math.cos(rad)
            );
            
            // 计算目标位置
            int pelletTarget = curUser.pos + 
                (int)(direction.x * MAX_DISTANCE) + 
                (int)(direction.y * MAX_DISTANCE) * Dungeon.level.width();
            
            // 如果目标超出地图，沿着方向找到最近的有效位置
            if (!Dungeon.level.insideMap(pelletTarget)) {
                int x = curUser.pos % Dungeon.level.width();
                int y = curUser.pos / Dungeon.level.width();
                int targetX = pelletTarget % Dungeon.level.width();
                int targetY = pelletTarget / Dungeon.level.width();
                
                // 计算方向向量
                float dx = targetX - x;
                float dy = targetY - y;
                float length = (float)Math.sqrt(dx * dx + dy * dy);
                dx /= length;
                dy /= length;
                
                // 从当前位置开始，沿着方向逐步检查直到找到有效位置
                float currentX = x;
                float currentY = y;
                while (!Dungeon.level.insideMap((int)currentX + (int)currentY * Dungeon.level.width())) {
                    currentX += dx;
                    currentY += dy;
                    // 防止无限循环
                    if (Math.abs(currentX - x) > MAX_DISTANCE || Math.abs(currentY - y) > MAX_DISTANCE) {
                        break;
                    }
                }
                pelletTarget = (int)currentX + (int)currentY * Dungeon.level.width();
            }
            
            // 确保目标在地图内
            if (Dungeon.level.insideMap(pelletTarget)) {
                ShotResult result = shoot(this, curUser, pelletTarget, cartridge, Ballistica.STOP_CHARS);
                
                // 显示霰弹轨迹
                Emitter emitter = GameScene.emitter();
                emitter.pos(curUser.pos, pelletTarget);
                emitter.pour(MagicMissile.MagicParticle.FACTORY, 0.01f);
                
                // 如果命中，添加额外效果
                if (result.hit && result.target != null) {
                    // 近距离额外效果
                    if (result.distance <= 2) {
                        // 击退效果
                        if (Random.Int(2) == 0) {
                            // 计算击退轨迹
                            Ballistica trajectory = new Ballistica(curUser.pos, result.target.pos, Ballistica.STOP_CHARS);
                            // 将轨迹延伸到目标后方
                            trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
                            // 击退目标
                            WandOfBlastWave.throwChar(result.target, trajectory, 2, true, false, curUser);
                        }
                        
                        if (Random.Float() > 0.75f) {
                            Buff.affect(result.target, Paralysis.class, 1f);
                        }
                    }
                }
            }
        }
        
        // 播放音效
        Sample.INSTANCE.play(Assets.Sounds.BLAST, 0.8f, Random.Float(0.85f, 1.15f));
                
        // 让英雄完成动作
        curUser.spendAndNext(1f);
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
        desc.append("- 每次射击发射8颗霰弹，呈60度扇形分布\n");
        desc.append("- 距离越近，命中率和伤害越高\n\n");
        
        return desc.toString();
    }
}