package com.coladungeon.mod.ArknightsMod.items.defender;

import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Cripple;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Item;
import com.coladungeon.items.armor.Armor;
import com.coladungeon.items.weapon.Weapon;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Random;

public class FortressArmor extends Armor {
    
    {
        image = ItemSpriteSheet.ARMOR_PLATE;
        tier = 7;
    }
    
    public FortressArmor() {
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
        return "要塞装甲";
    }
    
    @Override
    public String desc() {
        return "重型要塞装甲提供极高的防护，但需要配套的武器才能正常使用。如果没有合适的武器，攻击时会产生后坐力造成额外伤害。";
    }
    
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (defender instanceof Hero) {
            Hero hero = (Hero) defender;
            Item weapon = hero.belongings.weapon;
            
            // 检查是否有配套武器（这里假设重型武器是配套的）
            boolean hasCompatibleWeapon = false;
            if (weapon instanceof Weapon) {
                Weapon w = (Weapon) weapon;
                // 假设重型武器（等级 >= 4）是配套的
                if (w.level() >= 4) {
                    hasCompatibleWeapon = true;
                }
            }
            
            // 如果没有配套武器，攻击时产生后坐力
            if (!hasCompatibleWeapon && attacker == defender) {
                if (Random.Float() < 0.3f) { // 30%概率产生后坐力
                    int recoilDamage = Math.max(1, damage / 4);
                    defender.damage(recoilDamage, defender);
                    Buff.affect(defender, Cripple.class, 2f);
                    GLog.w("要塞装甲的后坐力造成了额外伤害！");
                }
            }
        }
        return super.proc(attacker, defender, damage);
    }
} 