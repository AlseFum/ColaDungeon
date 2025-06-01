package com.coladungeon.items.weapon.melee.assassin;

import com.coladungeon.actors.Char;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.actors.buffs.*;
import com.coladungeon.items.weapon.melee.MeleeWeapon;
import com.coladungeon.utils.GLog;

public abstract class AssassinWeapon extends MeleeWeapon {

    // 伏击相关
    protected static final float BASE_SURPRISE_DMG_FACTOR = 0.5f;  // 基础伏击伤害比例
    protected static final float SURPRISE_DMG_PER_LEVEL = 0.1f;    // 每级伏击等级增加的伤害比例
    protected static final float MAX_SURPRISE_DMG = 2.0f;          // 最大伏击伤害倍率
    
    // 连击相关
    protected int combo = 0;                    // 连击计数
    protected static final int MAX_COMBO = 3;   // 最大连击数
    protected static final float COMBO_DMG_BONUS = 0.15f;  // 每次连击增加的伤害比例
    
    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.enemy();
            
            // 计算伏击等级
            short ambushLevel = calculateAmbushLevel(hero, enemy);
            
            if (ambushLevel > 0 || (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero))) {
                // 计算基础伤害
                int damage = calculateBaseDamage(hero);
                
                // 应用伏击加成
                float surpriseBonus = Math.min(1f + (ambushLevel * SURPRISE_DMG_PER_LEVEL), MAX_SURPRISE_DMG);
                damage = Math.round(damage * surpriseBonus);
                
                // 应用连击加成
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
    
    private int calculateBaseDamage(Hero hero) {
        // 计算基础伤害范围
        int diff = max() - min();
        int damage = augment.damageFactor(Hero.heroDamageIntRange(
                min() + Math.round(diff * BASE_SURPRISE_DMG_FACTOR),
                max()));
        
        // 力量加成
        int exStr = hero.STR() - STRReq();
        if (exStr > 0) {
            damage += Hero.heroDamageIntRange(0, exStr);
        }
        
        return damage;
    }
    
    private short calculateAmbushLevel(Char hero, Char enemy) {
        short level = 0;
        
        // 敌人状态检查
        if (enemy.buff(Stunned.class) != null || enemy.buff(Paralysis.class) != null) {
            level += 2;  // 眩晕/麻痹状态给予更高的伏击等级
        }
        
        if (enemy.buff(Terror.class) != null) {
            level += 1;  // 恐惧状态
        }
        
        // 视野/隐蔽状态检查
        if (hero.buff(Shadows.class) != null) {
            level += 2;  // 自身处于阴影中给予更高的伏击等级
        }
        
        if (enemy.buff(Shadows.class) != null || enemy.buff(Blindness.class) != null) {
            level += 1;  // 敌人处于阴影或失明状态
        }
        
        // 检查敌人是否被偷袭
        if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
            level += 2;  // 偷袭状态给予更高的伏击等级
        }
        
        return level;
    }
    
    public void resetCombo() {
        if (combo > 0) {
            combo = 0;
            GLog.w("连击重置！");
        }
    }
    
    public int getCombo() {
        return combo;
    }
    
    public float getComboDamageBonus() {
        return combo * COMBO_DMG_BONUS;
    }
    
    @Override
    public String desc() {
        String desc = super.desc();
        if (isIdentified()) {
            desc += "\n\n伏击机制："
                 + "\n- 基础伏击伤害：" + Math.round(BASE_SURPRISE_DMG_FACTOR * 100) + "%最大伤害 到 最大伤害"
                 + "\n- 每级伏击等级增加：" + Math.round(SURPRISE_DMG_PER_LEVEL * 100) + "%伤害"
                 + "\n- 最大伏击伤害：" + Math.round(MAX_SURPRISE_DMG * 100) + "%"
                 + "\n\n连击机制："
                 + "\n- 当前连击：" + combo + "/" + MAX_COMBO
                 + "\n- 每次连击增加：" + Math.round(COMBO_DMG_BONUS * 100) + "%伤害"
                 + "\n- 当前伤害加成：+" + Math.round(getComboDamageBonus() * 100) + "%";
        }
        return desc;
    }
    
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // 计算伏击等级并应用伤害加成
        short ambushLevel = calculateAmbushLevel(attacker, defender);
        if (ambushLevel > 0) {
            float surpriseBonus = Math.min(1f + (ambushLevel * SURPRISE_DMG_PER_LEVEL), MAX_SURPRISE_DMG);
            damage = Math.round(damage * surpriseBonus);
            GLog.n("伏击等级 " + ambushLevel + " 造成额外伤害！");
        }
        return super.proc(attacker, defender, damage);
    }
} 