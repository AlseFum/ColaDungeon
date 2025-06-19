package com.coladungeon.mod.ArknightsMod.items.defender;

import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.MindVision;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.armor.Armor;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Random;

public class SentinelArmor extends Armor {
    
    {
        image = ItemSpriteSheet.ARMOR_PLATE;
        tier = 6;
    }
    
    public SentinelArmor() {
        super(6);
    }
    
    @Override
    public int DRMax(int lvl) {
        return 6 + lvl * 2;
    }
    
    @Override
    public int DRMin(int lvl) {
        return 4 + lvl;
    }
    
    @Override
    public int STRReq(int lvl) {
        return 20;
    }
    
    @Override
    public String name() {
        return "哨戒铁卫装甲";
    }
    
    @Override
    public String desc() {
        return "哨戒铁卫装甲增强了穿戴者的视野和攻击距离，同时提供额外的感知能力。这种装甲适合那些需要远距离作战的哨兵。";
    }
    
    @Override
    public boolean doEquip(Hero hero) {
        if (super.doEquip(hero)) {
            // 装备时给予视野增强效果
            Buff.affect(hero, MindVision.class, 999f);
            GLog.p("哨戒铁卫装甲增强了你的视野！");
            return true;
        }
        return false;
    }
    
    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            // 卸下时移除视野增强效果
            Buff.detach(hero, MindVision.class);
            GLog.w("哨戒铁卫装甲的视野增强效果消失了！");
            return true;
        }
        return false;
    }
    
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // 攻击时有20%概率造成额外伤害（模拟远距离攻击）
        if (defender instanceof Hero && attacker != defender) {
            if (Random.Float() < 0.2f) {
                int extraDamage = Math.max(1, damage / 3);
                attacker.damage(extraDamage, defender);
                GLog.p("哨戒铁卫装甲的远距离攻击造成了额外伤害！");
            }
        }
        return super.proc(attacker, defender, damage);
    }
} 