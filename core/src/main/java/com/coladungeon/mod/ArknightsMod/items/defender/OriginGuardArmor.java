package com.coladungeon.mod.ArknightsMod.items.defender;

import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.armor.Armor;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Random;

public class OriginGuardArmor extends Armor {
    
    {
        image = ItemSpriteSheet.ARMOR_PLATE;
        tier = 7;
    }
    
    public OriginGuardArmor() {
        super(7);
    }
    
    @Override
    public int DRMax(int lvl) {
        return 7 + lvl * 2;
    }
    
    @Override
    public int DRMin(int lvl) {
        return 5 + lvl;
    }
    
    @Override
    public int STRReq(int lvl) {
        return 22;
    }
    
    @Override
    public String name() {
        return "本源铁卫装甲";
    }
    
    @Override
    public String desc() {
        return "本源铁卫装甲蕴含着原始的力量，装甲本身就能对敌人造成伤害。当穿戴者受到攻击时，装甲会自动反击敌人。";
    }
    
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // 受击时有50%概率装甲自动反击
        if (defender instanceof Hero && damage > 0) {
            if (Random.Float() < 0.5f) {
                int counterDamage = Math.max(2, damage / 2);
                attacker.damage(counterDamage, defender);
                GLog.p("本源铁卫装甲自动反击了敌人！");
            }
        }
        return super.proc(attacker, defender, damage);
    }
} 