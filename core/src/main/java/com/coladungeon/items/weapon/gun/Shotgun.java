/*
 * Cola Dungeon
 */

package com.coladungeon.items.weapon.gun;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Paralysis;
import com.coladungeon.items.wands.WandOfBlastWave;
import com.coladungeon.mechanics.Ballistica;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.List;
import com.coladungeon.actors.Actor;
public class Shotgun extends Gun {
    //FIXME pellet should depend con cartridge
    private static final float CONE_ANGLE = 60f; // 60度的锥形范围
    private static final int MAX_DISTANCE = 4; // 最大射程
    private static final int PELLET_COUNT = 8; // 霰弹数量

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
        return 8 + 2 * lvl;
    }

    @Override
    public int max(int lvl) {
        return 15 + 5 * lvl;
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

    @Override
    public HitResult[] fire_hits(Char shooter, int targetPos, int projectileType) {
        // Calculate base direction
        PointF baseDirection = new PointF(
            (targetPos % Dungeon.level.width()) - (shooter.pos % Dungeon.level.width()),
            (targetPos / Dungeon.level.width()) - (shooter.pos / Dungeon.level.width())
        ).normalize();

        List<HitResult> results = new ArrayList<>();

        // Fire multiple pellets
        for (int i = 0; i < PELLET_COUNT; i++) {
            // Calculate offset angle
            float angle = Random.Float(-CONE_ANGLE / 2, CONE_ANGLE / 2);
            float rad = (float) Math.toRadians(angle);

            PointF direction = new PointF(
                baseDirection.x * (float) Math.cos(rad) - baseDirection.y * (float) Math.sin(rad),
                baseDirection.x * (float) Math.sin(rad) + baseDirection.y * (float) Math.cos(rad)
            );

            // Calculate target position using Ballistica
            int pelletTarget = shooter.pos +
                (int) (direction.x * MAX_DISTANCE) +
                (int) (direction.y * MAX_DISTANCE) * Dungeon.level.width();

            Ballistica trajectory = new Ballistica(shooter.pos, pelletTarget, projectileType);
            int collisionPos = trajectory.collisionPos;
            Char target = Actor.findChar(collisionPos);

            results.add(new HitResult(collisionPos, target));
        }

        return results.toArray(new HitResult[0]);
    }

    public static int calculateDistance(int pos1, int pos2) {
        int x1 = pos1 % Dungeon.level.width();
        int y1 = pos1 / Dungeon.level.width();
        int x2 = pos2 % Dungeon.level.width();
        int y2 = pos2 / Dungeon.level.width();

        int dx = x2 - x1;
        int dy = y2 - y1;
        return (int) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    protected int fire_proc(Char shooter, Char target, int damage) {
        // Calculate distance using the static method
        int distance = calculateDistance(shooter.pos, target.pos);

        // Apply knockback if close
        if (distance <= 2) {
            if (Random.Int(2) == 0) {
                Ballistica trajectory = new Ballistica(shooter.pos, target.pos, Ballistica.STOP_CHARS);
                trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
                WandOfBlastWave.throwChar(target, trajectory, 2, true, false, shooter);
            }

            if (Random.Float() > 0.75f) {
                Buff.affect(target, Paralysis.class, 1f);
            }
        }

        return super.fire_proc(shooter, target, damage);
    }
    @Override
    public float getAmmoPowerMultiplier() {
        //针对单颗弹丸
        return (0.5f + 0.3f * level());
    }
}