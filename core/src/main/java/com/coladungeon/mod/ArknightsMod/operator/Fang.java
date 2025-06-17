package com.coladungeon.mod.ArknightsMod.operator;

import com.coladungeon.items.weapon.Weapon;
import com.coladungeon.mod.ArknightsMod.Operator;
import com.coladungeon.mod.ArknightsMod.category.OperatorConst;

public class Fang extends Operator {
    {
        rarity= OperatorConst.Rarity.S1;
        branch= OperatorConst.Branch.Pioneer;
        cost=13;
    }
    public Fang() {
        
    }
    public static class Spear extends Weapon{
        {
            RCH=2;
        }
        @Override
        public int STRReq(int lvl) {
            return 10;
        }
        @Override
        public int max(int lvl) {
            return 10;
        }
        @Override
        public int min(int lvl) {
            return 5;
        }
    }
}