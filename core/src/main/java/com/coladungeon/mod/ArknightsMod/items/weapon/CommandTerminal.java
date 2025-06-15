package com.coladungeon.mod.ArknightsMod.items.weapon;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Item;
import com.coladungeon.mod.ArknightsMod.Operator;
import com.coladungeon.mod.ArknightsMod.operator.Phantom;
import com.coladungeon.mod.ArknightsMod.operator.TexasTheOmertosa;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class CommandTerminal extends Item {

    {
        image = ItemSpriteSheet.ARTIFACT_HORN1;
        defaultAction = AC_CHARGE;
        unique = true;
    }

    private static final String AC_CHARGE = "CHARGE";
    private static final String AC_DEPLOY = "DEPLOY";
    private static final String COST = "cost";

    public int cost = 0;
    private static final int MAX_COST = 99;

    @Override
    public String name() {
        return "罗德岛指挥终端";
    }

    @Override
    public String desc() {
        String desc = "这是罗德岛博士专用的指挥终端，可以通过充能来获得cost点数，用于部署干员。\n\n";
        desc += "当前cost：" + cost + "/" + MAX_COST + "\n\n";
        desc += "使用说明：\n";
        desc += "充能：增加cost点数\n";
        desc += "部署：消耗cost点数部署干员\n\n";
        desc += "干员部署cost：\n";
        desc += "- 近卫干员：8-12 cost\n";
        desc += "- 狙击干员：7-10 cost\n";
        desc += "- 重装干员：10-15 cost\n";
        desc += "- 医疗干员：6-9 cost\n";
        desc += "- 术师干员：9-13 cost\n";
        desc += "- 辅助干员：5-8 cost";
        return desc;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_CHARGE);
        actions.add(AC_DEPLOY);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_CHARGE)) {
            return "充能";
        } else if (action.equals(AC_DEPLOY)) {
            return "部署";
        }
        return super.actionName(action, hero);
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_CHARGE)) {
            if (cost < MAX_COST) {
                cost++;
                Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
                GLog.p("充能完成！当前cost：" + cost + "/" + MAX_COST);
                updateQuickslot();
            } else {
                GLog.w("cost已满！");
            }
        } else if (action.equals(AC_DEPLOY)) {
            if (cost >= 8) { // 最低部署cost
                GameScene.show(new WndOptions(
                    "部署干员",
                    "选择要部署的干员：",
                    "傀影 (8 cost)",
                    "缄默德克萨斯 (10 cost)",
                    "取消") {
                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            deployOperator(hero, new Phantom(), 8);
                        } else if (index == 1) {
                            deployOperator(hero, new TexasTheOmertosa(), 10);
                        }
                    }
                });
            } else {
                GLog.w("cost不足！");
            }
        }
    }

    private void deployOperator(Hero hero, Operator operator, int requiredCost) {
        if (cost >= requiredCost) {
            cost -= requiredCost;
            operator.pos = hero.pos;
            operator.arrange = Operator.ARRANGE_CHASE;
            Dungeon.level.occupyCell(operator);
            GameScene.add(operator);
            GLog.p("部署成功！剩余cost：" + cost);
            updateQuickslot();
        } else {
            GLog.w("cost不足！");
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(COST, cost);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        cost = bundle.getInt(COST);
    }

    @Override
    public String status() {
        return Integer.toString(cost);
    }
}
