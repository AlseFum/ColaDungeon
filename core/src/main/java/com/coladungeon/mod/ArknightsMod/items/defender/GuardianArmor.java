package com.coladungeon.mod.ArknightsMod.items.defender;

import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Healing;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.armor.Armor;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Random;

public class GuardianArmor extends Armor {
    
    {
        image = ItemSpriteSheet.ARMOR_PLATE;
        tier = 5;
    }
    
    public GuardianArmor() {
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
        return "守护者装甲";
    }
    
    @Override
    public String desc() {
        return "神圣的守护者装甲，当穿戴者受到攻击时会自动恢复生命值。这种装甲蕴含着治愈的力量，让守护者能够持续保护队友。";
    }
    
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // 受击时有30%概率恢复生命值
        if (defender instanceof Hero && damage > 0) {
            if (Random.Float() < 0.3f) {
                int healAmount = Math.max(1, damage / 4); // 恢复伤害的1/4
                Buff.affect(defender, Healing.class).setHeal(healAmount, 0.25f, 0);
                GLog.p("守护者装甲为你恢复了生命值！");
            }
        }
        return super.proc(attacker, defender, damage);
    }
} 