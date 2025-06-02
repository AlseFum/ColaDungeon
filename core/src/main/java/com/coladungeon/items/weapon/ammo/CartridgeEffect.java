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
    }),
    HighExplosive("高爆弹", (hero, pos, power, damage) -> {
        // 高爆弹造成更大的爆炸范围和伤害
        int explosionDamage = (int) (power * 0.8f); // 更高的基础伤害

        // 添加更强烈的爆炸粒子效果
        CellEmitter.center(pos).burst(BlastParticle.FACTORY, 50);

        // 造成更大范围的伤害
        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int n = pos + PathFinder.NEIGHBOURS8[i];
            if (n >= 0 && n < Dungeon.level.length()) {
                // 第一层爆炸
                Char ch = Actor.findChar(n);
                if (ch != null && ch != hero) {
                    ch.damage(explosionDamage, hero);
                }
                
                // 第二层爆炸
                for (int j = 0; j < PathFinder.NEIGHBOURS8.length; j++) {
                    int nn = n + PathFinder.NEIGHBOURS8[j];
                    if (nn >= 0 && nn < Dungeon.level.length()) {
                        // 根据距离计算伤害衰减
                        float distance = Dungeon.level.distance(pos, nn);
                        int finalDamage = (int) (explosionDamage * (1f - distance * 0.3f));
                        
                        Char ch2 = Actor.findChar(nn);
                        if (ch2 != null && ch2 != hero) {
                            ch2.damage(finalDamage, hero);
                        }
                    }
                }
            }
        }
        return 0;
    }),
    Supply("补充弹药", (hero, pos, power, damage) -> -1)
    ;

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
