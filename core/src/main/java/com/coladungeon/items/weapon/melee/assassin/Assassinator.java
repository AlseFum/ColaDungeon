package com.coladungeon.items.weapon.melee.assassin;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.blobs.SmokeScreen;
import com.coladungeon.actors.buffs.Bleeding;
import com.coladungeon.actors.buffs.Blindness;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Invisibility;
import com.coladungeon.actors.buffs.Paralysis;
import com.coladungeon.actors.buffs.Stunned;
import com.coladungeon.actors.buffs.Terror;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.items.weapon.melee.MeleeWeapon;
import com.coladungeon.utils.GLog;

public abstract class Assassinator extends MeleeWeapon {

    // 伏击相关
    protected static float BASE_SURPRISE_DMG_FACTOR = 0.5f;  // 基础伏击伤害比例
    protected static final float SURPRISE_DMG_PER_LEVEL = 0.5f;    // 每级伏击等级增加的伤害比例
    public static float DMG_AMP_PER_LEVEL = 0.3f;
    protected static final float SE_RATIO = 0.95f;

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero) owner;
            Char enemy = hero.enemy();

            // Calculate ambush level
            short ambushLevel = ambushLevel(hero, enemy);

            if ((enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero))) {
                // Calculate base damage with ambush factor
                int diff = max() - min();
                int damage = Hero.heroDamageIntRange(
                        min() + Math.round(diff * (BASE_SURPRISE_DMG_FACTOR + ambushLevel * DMG_AMP_PER_LEVEL)),
                        max()
                );

                // Apply strength bonus
                int exStr = hero.STR() - STRReq();
                if (exStr > 0) {
                    damage += Hero.heroDamageIntRange(0, exStr);
                }

                return damage;
            }
        }
        return super.damageRoll(owner);
    }

    private static short ambushLevel(Char hero, Char enemy) {
        short level = 0;
        // 敌人状态检查
        if (enemy.buff(Stunned.class) != null || enemy.buff(Paralysis.class) != null) {
            level += 2;  // 眩晕/麻痹状态给予更高的伏击等级
        }

        if (enemy.buff(Terror.class) != null) {
            level += 1;  // 恐惧状态
        }

        // 视野/隐蔽状态检查
        if (hero.buff(Invisibility.class) != null) {
            level += 2;  // 自身处于阴影中给予更高的伏击等级
        }
        //查询我方是否处于Smokescreen中
        var smokeScreen = Dungeon.level.blobs.get(SmokeScreen.class);
        if (smokeScreen != null) {
            boolean heroInSmoke = smokeScreen.volumeAt(hero.pos, SmokeScreen.class) > 0;
            boolean enemyInSmoke = smokeScreen.volumeAt(enemy.pos, SmokeScreen.class) > 0;
            if (heroInSmoke || enemyInSmoke) {
                level += 2;
            }
        }

        if (enemy.buff(Blindness.class) != null) {
            level += 1;  // 敌人处于阴影或失明状态
        }

        if((enemy instanceof Mob && ((Mob)enemy).surprisedBy(hero))){
            level += 1;
        }

        return level;
    }

    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder(super.desc());
        if (isIdentified()) {
            desc.append("\n\n伏击机制：")
                .append("\n- 基础伏击伤害：")
                .append(Math.round(BASE_SURPRISE_DMG_FACTOR * 100))
                .append("%最大伤害 到 最大伤害")
                .append("\n- 每级伏击等级增加：")
                .append(Math.round(SURPRISE_DMG_PER_LEVEL * 100))
                .append("%伤害");
        }
        return desc.toString();
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // 计算伏击等级并应用伤害加成
        short ambushLevel = ambushLevel(attacker, defender);
        if (ambushLevel > 0) {
            damage = Math.round(damage * 1f + (ambushLevel * SURPRISE_DMG_PER_LEVEL));
            GLog.n("ambush lv."+ambushLevel+":"+damage+"!");
            // if (damage > max() * SE_RATIO) {
            //     GLog.n("Special Effect!");
            //     special_effect(attacker, defender, damage);
            // }
        }
        return super.proc(attacker, defender, damage);
    }

    public void special_effect(Char attacker, Char defender, int damage) {
        // Apply bleeding effect with a level based on damage
        Buff.affect(defender, Bleeding.class).set(damage / 10f); // Example level calculation
    }
    @Override
    public int max(int lvl) {
        return 4 * (tier + 1) + lvl * (tier + 1);
    }
}
