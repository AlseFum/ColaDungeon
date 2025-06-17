package com.coladungeon.mod.ArknightsMod.operator;

import com.coladungeon.actors.Char;
import com.coladungeon.mod.ArknightsMod.NPC.Dummy;
import com.coladungeon.mod.ArknightsMod.Operator;
import com.coladungeon.mod.ArknightsMod.category.OperatorConst;
import com.watabou.utils.Random;
import com.coladungeon.actors.buffs.Paralysis;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.ExecutorWeapon;
import com.coladungeon.sprites.ItemSpriteManager;

public class ProjektRed extends Operator {

    {

        spriteClass = Dummy.DummySprite.class;

        HP = HT = 1000;
        defenseSkill = 30;

        alignment = Alignment.ALLY;

        // 设置干员特有属性
        branch = OperatorConst.Branch.Core;
        rarity = OperatorConst.Rarity.S6;
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

    public static class RedKnife extends ExecutorWeapon {


        {
            image = ItemSpriteManager.registerTexture("cola/redknife.png",32).label("redknife").getByName("redknife");
        }
        @Override
        public String name(){
            return "红的小刀";
        }

        @Override
        public String info(){
            return "红有一把小刀。她有一把小刀。\n\n"+super.info();
        }
        @Override
        public int SE(Char owner, Char enemy, int damage) {
            Buff.affect(enemy, Paralysis.class);
            return damage;
        }
    }
}
