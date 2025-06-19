package com.coladungeon.mod.ArknightsMod.items.defender;

import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.ShieldBuff;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.armor.Armor;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;

public class IronGuardArmor extends Armor {
    
    {
        image = ItemSpriteSheet.ARMOR_PLATE; // 使用板甲图标作为基础
        tier = 4;
    }
    
    public IronGuardArmor() {
        super(4);
    }
    
    @Override
    public int DRMax(int lvl) {
        return 4 + lvl * 2; // 基础防御力较高
    }
    
    @Override
    public int DRMin(int lvl) {
        return 2 + lvl; // 最小防御力
    }
    
    @Override
    public int STRReq(int lvl) {
        return 16; // 需要较高的力量
    }
    
    @Override
    public String name() {
        return "铁卫装甲";
    }
    
    @Override
    public String desc() {
        return "坚固的铁卫装甲，为前线战士提供可靠的防护。这是最基础的重装装甲，适合新手重装干员使用。";
    }
    
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // 铁卫装甲没有特殊效果，只是基础防护
        return super.proc(attacker, defender, damage);
    }
} 