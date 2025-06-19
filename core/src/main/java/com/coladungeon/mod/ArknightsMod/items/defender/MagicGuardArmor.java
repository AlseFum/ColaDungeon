package com.coladungeon.mod.ArknightsMod.items.defender;

import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.armor.Armor;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Random;

public class MagicGuardArmor extends Armor {
    
    {
        image = ItemSpriteSheet.ARMOR_PLATE;
        tier = 5;
    }
    
    public MagicGuardArmor() {
        super(5);
    }
    
    @Override
    public int DRMax(int lvl) {
        return 5 + lvl * 2;
    }
    
    @Override
    public int DRMin(int lvl) {
        return 3 + lvl;
    }
    
    @Override
    public int STRReq(int lvl) {
        return 18;
    }
    
    @Override
    public String name() {
        return "驭法铁卫装甲";
    }
    
    @Override
    public String desc() {
        return "驭法铁卫装甲蕴含着魔法能量，当穿戴者受到攻击时会反弹魔法伤害给攻击者。这种装甲适合那些能够操控魔法的重装战士。";
    }
    
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // 受击时有40%概率反弹魔法伤害
        if (defender instanceof Hero && damage > 0) {
            if (Random.Float() < 0.4f) {
                int reflectDamage = Math.max(1, damage / 2);
                attacker.damage(reflectDamage, defender);
                GLog.p("驭法铁卫装甲反弹了魔法伤害！");
            }
        }
        return super.proc(attacker, defender, damage);
    }
} 