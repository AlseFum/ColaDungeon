package com.coladungeon.items.weapon.gun;

import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.items.weapon.ammo.Ammo;
import com.coladungeon.mechanics.Ballistica;
import com.coladungeon.sprites.ItemSpriteSheet;

import java.util.List;
//和Gun别无二致，除了对Ammo特别要求为Grenade

public class GrenadeLauncher extends Gun {

    {
        image = ItemSpriteSheet.CROSSBOW; // 暂时使用十字弩的图标

        defaultAction = AC_FIRE; // 默认动作为开火

        maxAmmo = 6; // 最大弹药量
        ammo = maxAmmo; // 初始弹药量
        reloadTime = 2f; // 装弹时间
    }
    private int bullet = 3; // Number of bullets to fire

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

    // 只允许Grenade类型弹药
    @Override
    public boolean canLoad(Ammo ammo) {
        return true;
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
        return 25 + 10 * lvl; // 较高的最大伤害
    }

    @Override
    public HitResult[] fire_hits(Char shooter, int targetPos, int projectileType) {
        // Use Ballistica with STOP_TARGET to calculate the trajectory
        Ballistica trajectory = new Ballistica(shooter.pos, targetPos, Ballistica.STOP_TARGET);
        
        // Calculate positions for hits
        List<Integer> path = trajectory.subPath(0, trajectory.dist);
        int[] positions = path.stream().mapToInt(Integer::intValue).toArray();
        
        // Skip the starting position and calculate hits
        int numHits = Math.min(positions.length - 1, bullet);
        HitResult[] results = new HitResult[numHits];
        
        // Distribute hits evenly along the trajectory, excluding the start position
        for (int i = 0; i < numHits; i++) {
            // Add 1 to skip the starting position
            int pos = positions[1 + i * (positions.length - 2) / (numHits - 1)];
            Char target = Actor.findChar(pos);
            results[i] = new HitResult(pos, target);
        }
        
        return results;
    }
}
