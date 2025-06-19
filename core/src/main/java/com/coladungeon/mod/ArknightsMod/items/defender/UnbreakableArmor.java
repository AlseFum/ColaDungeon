package com.coladungeon.mod.ArknightsMod.items.defender;

import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Healing;
import com.coladungeon.actors.buffs.MagicImmune;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.armor.Armor;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Random;

public class UnbreakableArmor extends Armor {
    
    {
        image = ItemSpriteSheet.ARMOR_PLATE;
        tier = 6;
    }
    
    public UnbreakableArmor() {
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
        return "不屈者装甲";
    }
    
    @Override
    public String desc() {
        return "不屈者装甲会阻止外部治疗，但会通过战斗来恢复生命值。当穿戴者攻击敌人时，有概率恢复生命值。这种装甲适合那些依靠战斗来维持生命的战士。";
    }
    
    @Override
    public boolean doEquip(Hero hero) {
        if (super.doEquip(hero)) {
            // 装备时给予禁疗效果
            Buff.affect(hero, MagicImmune.class, 999f);
            GLog.w("不屈者装甲阻止了外部治疗！");
            return true;
        }
        return false;
    }
    
    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            // 卸下时移除禁疗效果
            Buff.detach(hero, MagicImmune.class);
            GLog.p("不屈者装甲的禁疗效果消失了！");
            return true;
        }
        return false;
    }
    
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // 当穿戴者攻击时，有25%概率恢复生命值
        if (defender instanceof Hero && attacker != defender) {
            if (Random.Float() < 0.25f) {
                int healAmount = Math.max(1, damage / 3);
                Buff.affect(defender, Healing.class).setHeal(healAmount, 0.25f, 0);
                GLog.p("不屈者装甲通过战斗恢复了生命值！");
            }
        }
        return super.proc(attacker, defender, damage);
    }
} 