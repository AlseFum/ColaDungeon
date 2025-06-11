package com.coladungeon.mod.ArknightsMod.Headhunt;

import java.util.ArrayList;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Item;
import com.coladungeon.messages.Messages;
import com.coladungeon.mod.ArknightsMod.Operator;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.watabou.utils.PathFinder;

public class OperatorSummon extends Item {

    public static final String AC_SUMMON = "SUMMON";

    {
        image = ItemSpriteSheet.SCROLL_ISAZ;
        stackable = true;
        defaultAction = AC_SUMMON;
    }

    @Override
    public String name() {
        return "干员召唤券";
    }

    @Override
    public String desc() {
        return "使用后可以召唤一名罗德岛干员协助战斗。干员会跟随你并攻击敌人。";
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return 50;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_SUMMON);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_SUMMON)) {
            summonOperator(hero);
        }
    }

    private void summonOperator(Hero hero) {
        // 在玩家周围找一个合适的位置
        int pos = -1;
        for (int i : PathFinder.NEIGHBOURS8) {
            int cell = hero.pos + i;
            if (Dungeon.level.passable[cell] && Dungeon.level.findMob(cell) == null) {
                pos = cell;
                break;
            }
        }

        if (pos == -1) {
            GLog.w(Messages.get(this, "no_space"));
            return;
        }

        // 创建干员
        Operator operator = new Operator();
        operator.pos = pos;
        GameScene.add(operator);

        // 消耗物品
        detach(hero.belongings.backpack);
        GLog.i(Messages.get(this, "summon"));
    }
} 