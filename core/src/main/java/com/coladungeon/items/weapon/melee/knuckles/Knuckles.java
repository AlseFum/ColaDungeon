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
    private static final int BASE_STUN_DURATION = 2;        // 基础眩晕持续回合数
    private static final float COMBO_STUN_BONUS = 0.05f;    // 每次连击增加的眩晕概率：5%
    private static final float MAX_STUN_CHANCE = 0.75f;     // 最大眩晕概率：75%
    
    private int comboCount = 0;                             // 连击计数器
    private int level = 0;                                  // 武器等级
    
    {
        initCommon();
    }

    private void initCommon() {
        image = ItemSpriteSheet.GLOVES;  // 使用手套图标作为指虎的临时图标
        
        // 设置攻击延迟（非常低=超高攻速）
        DLY = 0.25f;
        
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
             + "眩晕概率会受到敌人防御力和体型的影响。"
             + "\n\n基础眩晕概率: " + Math.round(BASE_STUN_CHANCE * 100) + "%"
             + "\n当前连击数: " + comboCount
             + "\n武器等级: " + level;
    }
    
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // 增加连击计数
        comboCount++;
        
        // 计算当前眩晕概率
        float stunChance = calculateStunChance(defender);
        
        // 尝试眩晕
        if (Random.Float() < stunChance) {
            int stunDuration = calculateStunDuration(defender);
            Buff.prolong(defender, Stunned.class, stunDuration);
            GLog.p("连击打晕了敌人！");
            
            // 重置连击计数
            comboCount = 0;
        }
        
        return damage;
    }
    
    private float calculateStunChance(Char defender) {
        // 基础概率
        float chance = BASE_STUN_CHANCE;
        
        // 连击加成
        chance += (comboCount * COMBO_STUN_BONUS);
        
        // 武器等级加成
        chance += (level * 0.05f);
        
        // 敌人防御力影响 (防御力越高,眩晕概率越低)
        float defFactor = Math.max(0.5f, 1f - (defender.defenseSkill(defender) * 0.01f));
        chance *= defFactor;
        
        // 敌人体型影响 (体型越大,眩晕概率越低)
        float sizeFactor = Math.max(0.5f, 1f - (defender.sprite.width() * 0.05f));
        chance *= sizeFactor;
        
        // 确保不超过最大概率
        return Math.min(chance, MAX_STUN_CHANCE);
    }
    
    private int calculateStunDuration(Char defender) {
        // 基础持续时间
        int duration = BASE_STUN_DURATION;
        
        // 武器等级加成
        duration += level;
        
        // 敌人防御力影响 (防御力越高,眩晕时间越短)
        float defFactor = Math.max(0.5f, 1f - (defender.defenseSkill(defender) * 0.01f));
        duration = Math.round(duration * defFactor);
        
        // 确保至少眩晕1回合
        return Math.max(1, duration);
    }
    
    public void resetCombo() {
        // 重置连击计数
        comboCount = 0;
    }
}
