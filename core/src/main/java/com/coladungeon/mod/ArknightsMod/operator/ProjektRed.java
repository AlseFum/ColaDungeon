package com.coladungeon.mod.ArknightsMod.operator;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.mobs.npcs.NPC;
import com.coladungeon.mod.ArknightsMod.DummySprite;
import com.watabou.utils.Bundle;
import com.coladungeon.items.weapon.Weapon;
import com.watabou.utils.Random;

public class ProjektRed extends Operator {

    {
        spriteClass = DummySprite.class;
        
        HP = HT = 1000;
        defenseSkill = 30;
        
        alignment = Alignment.ALLY;
        
        // 设置干员特有属性
        branch = Branch.Core;
        rarity = Rarity.S6;
        cost = 19;
    }

    @Override
    public String name() {
        return "Projekt Red";
    }
    
    @Override
    public String description() {
        return "罗德岛精英干员，代号\"红\"。擅长隐秘行动和快速突袭。";
    }
    
    @Override
    public String info() {
        String desc = super.info();
        
        desc += "\n\n干员信息：";
        desc += "\n- 分支：" + branch.name();
        desc += "\n- 稀有度：" + rarity.name();
        desc += "\n- 部署费用：" + cost;
        
        return desc;
    }
    
    @Override
    public int attackSkill(Char target) {
        return 35;
    }
    
    @Override
    public int damageRoll() {
        return Random.NormalIntRange(20, 40);
    }
    
    @Override
    public int drRoll() {
        return Random.NormalIntRange(5, 15);
    }
}
