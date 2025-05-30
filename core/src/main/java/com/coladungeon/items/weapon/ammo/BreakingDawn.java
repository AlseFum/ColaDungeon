package com.coladungeon.items.weapon.ammo;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.FlavourBuff;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.particles.BlastParticle;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Random;

public class BreakingDawn extends Ammo {
    public static int max_amount = 6;
    {
        cartridge= new RoaringDeadSoul();
    }

    

    public static class RoaringDeadSoul extends CartridgeAltFire {
        @Override
        public void fire(Hero hero, int targetPos) {
            // 超大范围爆炸
            int explosionRadius = 2; // 以目标为中心，半径2格
            int baseDamage = 40; // 可根据实际需要调整
            float debuffChance = 0.5f; // 50%概率上debuff
            float enhancedMultiplier = 2.0f; // 已有debuff时的伤害倍率

            // 爆炸粒子效果
            CellEmitter.center(targetPos).burst(BlastParticle.FACTORY, 40);

            for (int dx = -explosionRadius; dx <= explosionRadius; dx++) {
                for (int dy = -explosionRadius; dy <= explosionRadius; dy++) {
                    int cell = targetPos + dx + dy * Dungeon.level.width();
                    if (cell < 0 || cell >= Dungeon.level.length()) continue;
                    if (Dungeon.level.distance(targetPos, cell) > explosionRadius) continue;

                    Char ch = Actor.findChar(cell);
                    if (ch != null && ch.isAlive()) {
                        // 检查是否已有WhirlingDeadSoul debuff
                        WhirlingDeadSoul debuff = ch.buff(WhirlingDeadSoul.class);
                        if (debuff != null) {
                            // 已有debuff，造成更高伤害并移除debuff
                            int dmg = Math.round(baseDamage * enhancedMultiplier);
                            ch.damage(dmg, hero);
                            Buff.detach(ch, WhirlingDeadSoul.class);
                            GLog.n(ch.name() + " 被灵魂咆哮引爆，受到" + dmg + "点伤害！");
                        } else {
                            // 没有debuff，概率上debuff并造成基础伤害
                            ch.damage(baseDamage, hero);
                            if (Random.Float() < debuffChance) {
                                Buff.affect(ch, WhirlingDeadSoul.class, 5f); // 持续5回合
                                GLog.i(ch.name() + " 被灵魂咆哮附加了灵魂旋涡debuff！");
                            }
                        }
                    }
                }
            }
            hero.spendAndNext(0.5f);
        }
    }

    // FlavourBuff支持Buff.affect(ch, BuffClass, duration)
    public static class WhirlingDeadSoul extends FlavourBuff {
        @Override
        public boolean act() {
            spend(TICK);
            return true;
        }

        @Override
        public String desc() {
            return "灵魂旋涡：受到RoaringDeadSoul再次爆炸时会受到更高伤害并移除该debuff。";
        }
    }
}