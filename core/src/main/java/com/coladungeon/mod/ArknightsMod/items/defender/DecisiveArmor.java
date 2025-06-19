package com.coladungeon.mod.ArknightsMod.items.defender;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Momentum;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.armor.Armor;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Random;

public class DecisiveArmor extends Armor {
    
    {
        image = ItemSpriteSheet.ARMOR_PLATE;
        tier = 6;
    }
    
    public DecisiveArmor() {
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
        return "决战者装甲";
    }
    
    @Override
    public String desc() {
        return "决战者装甲在周围有敌人时会快速充能，提供额外的攻击速度和移动速度。这种装甲适合那些在危险环境中作战的战士。";
    }
    
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (defender instanceof Hero) {
            // 检查周围是否有敌人
            boolean hasNearbyEnemies = false;
            for (int i = 0; i < Dungeon.level.length(); i++) {
                Char ch = Actor.findChar(i);
                if (ch != null && ch != defender && ch.alignment == Char.Alignment.ENEMY) {
                    int distance = Dungeon.level.distance(defender.pos, ch.pos);
                    if (distance <= 3) { // 3格范围内有敌人
                        hasNearbyEnemies = true;
                        break;
                    }
                }
            }
            
            // 如果有附近敌人，给予充能效果
            if (hasNearbyEnemies) {
                Buff.affect(defender, Momentum.class);
                if (Random.Float() < 0.1f) { // 10%概率显示提示
                    GLog.p("决战者装甲正在充能！");
                }
            }
        }
        return super.proc(attacker, defender, damage);
    }
} 