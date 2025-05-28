package com.coladungeon.items.weapon.melee.vambrace;

import com.coladungeon.items.weapon.melee.MeleeWeapon;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.actors.Char;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Random;

public class Vambrace extends MeleeWeapon {
    
    // 基础属性
    private static final float BASE_BLOCK_RATE = 0.7f;      // 基础格挡率：70%
    private static final float ARMOR_PENETRATION = 0.35f;   // 破甲百分比：35%
    private static final float COUNTER_DMG_MULTI = 1.4f;    // 反击伤害倍率：1.4倍
    
    {
        image = ItemSpriteSheet.GAUNTLETS;  // 使用已有的护手图标
        
        // 设置攻击延迟（低=高攻速）
        DLY = 0.5f;
        
        tier = 1;
        
        // 设置需求力量和精确度
        ACC = 1.2f;
        RCH = 1;
    }

    
    public Vambrace() {
        super();
        image = ItemSpriteSheet.GAUNTLETS;  // 使用已有的护手图标
        
        // 设置攻击延迟（低=高攻速）
        DLY = 0.5f;
        
        tier = 1;
        
        // 设置需求力量和精确度
        ACC = 1.2f;
        RCH = 1;
    }
    
    @Override
    public String desc() {
        return "这副精巧的金属护手不仅能保护使用者的前臂，同时还配备了可折叠的尖刺机关。"
             + "其精密设计能够帮助使用者轻松格挡敌人攻击，并在格挡成功时立即反击。"
             + "\n\n格挡成功时，反击将无视敌人部分护甲。"
             + "\n\n当前格挡率: " + Math.round((BASE_BLOCK_RATE + level() * 0.025f) * 100) + "%"
             + "\n当前破甲率: " + Math.round((ARMOR_PENETRATION + level() * 0.05f) * 100) + "%";
    }
    
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // 尝试格挡
        if (Random.Float() < BASE_BLOCK_RATE + level() * 0.025f) {
            // 格挡成功，计算反击伤害
            int counterDamage = Math.round(damageRoll(defender) * COUNTER_DMG_MULTI);
            
            // 计算破甲
            float armorPen = ARMOR_PENETRATION + level() * 0.05f;
            
            // 应用破甲伤害
            // attacker.damage(counterDamage, defender);
            defender.damage(counterDamage,attacker);
            // 显示格挡效果
            GLog.p("格挡成功！");
            
            return 0;  // 格挡掉所有伤害
        }
        
        return damage;  // 未格挡，正常受伤
    }
}