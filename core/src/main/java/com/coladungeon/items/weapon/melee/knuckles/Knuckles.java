package com.coladungeon.items.weapon.melee.knuckles;

import com.coladungeon.items.weapon.melee.MeleeWeapon;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Stunned;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Random;

public class Knuckles extends MeleeWeapon {
    
    // 基础属性
    private static final float BASE_STUN_CHANCE = 0.25f;    // 基础眩晕概率：25%
    private static final int STUN_DURATION = 2;             // 眩晕持续回合数
    private static final float COMBO_STUN_BONUS = 0.05f;    // 每次连击增加的眩晕概率：5%
    
    private int comboCount = 0;                             // 连击计数器
    
    {
        initCommon();
    }

    private void initCommon() {
        image = ItemSpriteSheet.GLOVES;  // 使用手套图标作为指虎的临时图标
        
        // 设置攻击延迟（非常低=超高攻速）
        DLY = 0.4f;
        
        // 设置基础伤害（较低，因为攻速快）
        tier = 1;
        
        // 设置需求力量和精确度（较低，容易使用）
        ACC = 1.2f;
        RCH = 1;
    }
    
    public Knuckles() {
        super();
        initCommon();
    }
    
    @Override
    public String desc() {
        return "这副精钢打造的指虎能让你进行快速的连续打击。"
             + "每次命中都有机会使敌人眩晕，连续命中会提高眩晕概率。"
             + "\n\n基础眩晕概率: " + Math.round(BASE_STUN_CHANCE * 100) + "%"
             + "\n当前连击数: " + comboCount;
    }
    
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // 增加连击计数
        comboCount++;
        
        // 计算当前眩晕概率
        float stunChance = BASE_STUN_CHANCE + (comboCount * COMBO_STUN_BONUS);
        
        // 尝试眩晕
        if (Random.Float() < stunChance) {
            Buff.prolong(defender, Stunned.class, STUN_DURATION);
            GLog.p("连击打晕了敌人！");
            
            // 重置连击计数
            comboCount = 0;
        }
        
        return damage;
    }
    
    public void resetCombo() {
        // 重置连击计数
        comboCount = 0;
    }
}
