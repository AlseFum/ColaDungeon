package com.coladungeon.items.weapon.melee.assassin;

import com.coladungeon.actors.Char;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.items.weapon.melee.MeleeWeapon;
import com.coladungeon.utils.GLog;

public abstract class AssassinWeapon extends MeleeWeapon {

    // 伏击伤害比例（0.5f 表示从50%最大伤害到最大伤害）
    protected float SURPRISE_DMG_FACTOR = 0.5f;
    
    // 连击相关
    protected int combo = 0;                    // 连击计数
    protected int MAX_COMBO = 3;                // 最大连击数
    protected float COMBO_DMG_BONUS = 0.1f;     // 每次连击增加的伤害比例
    
    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.enemy();
            if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
                // 计算伏击伤害
                int diff = max() - min();
                int damage = augment.damageFactor(Hero.heroDamageIntRange(
                        min() + Math.round(diff * SURPRISE_DMG_FACTOR),
                        max()));
                
                // 力量加成
                int exStr = hero.STR() - STRReq();
                if (exStr > 0) {
                    damage += Hero.heroDamageIntRange(0, exStr);
                }
                
                // 连击加成
                if (combo < MAX_COMBO) {
                    combo++;
                    GLog.p("连击 +" + combo + "!");
                }
                damage = Math.round(damage * (1 + combo * COMBO_DMG_BONUS));
                
                return damage;
            } else {
                // 非伏击重置连击
                resetCombo();
            }
        }
        return super.damageRoll(owner);
    }
    
    // 重置连击计数
    public void resetCombo() {
        if (combo > 0) {
            combo = 0;
            GLog.w("连击重置！");
        }
    }
    
    // 获取当前连击数
    public int getCombo() {
        return combo;
    }
    
    // 获取当前连击加成
    public float getComboDamageBonus() {
        return combo * COMBO_DMG_BONUS;
    }
    
    @Override
    public String desc() {
        String desc = super.desc();
        if (isIdentified()) {
            desc += "\n\n伏击伤害：" + Math.round(SURPRISE_DMG_FACTOR * 100) + "%最大伤害 到 最大伤害"
                 + "\n当前连击：" + combo
                 + "\n伤害加成：+" + Math.round(getComboDamageBonus() * 100) + "%";
        }
        return desc;
    }
} 