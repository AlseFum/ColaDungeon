package com.coladungeon.items.weapon.ammo;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.particles.BlastParticle;
import com.watabou.utils.PathFinder;

public enum CartridgeEffect {
    Normal("标准弹", (hero, pos, power, damage) -> -1),
    Explosive("爆炸弹", (hero, pos, power, damage) -> {
        //在pos造成微型爆炸
        int explosionDamage = (int) (power * 0.5f);

        // 添加爆炸粒子效果
        CellEmitter.center(pos).burst(BlastParticle.FACTORY, 30);

        // 造成伤害
        for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
            int n = pos + PathFinder.NEIGHBOURS9[i];
            if (n >= 0 && n < Dungeon.level.length()) {
                Char ch = Actor.findChar(n);
                if (ch != null && ch != hero) {
                    ch.damage(explosionDamage, hero);
                }
            }
        }
        return 0;
    });

    @FunctionalInterface
    public interface OnHit {
        int apply(Hero hero, int pos, int power, int damage);
    }

    public String name;
    public OnHit onHit;

    CartridgeEffect(String name, OnHit onHit) {
        this.name = name;
        this.onHit = onHit;
    }
}
