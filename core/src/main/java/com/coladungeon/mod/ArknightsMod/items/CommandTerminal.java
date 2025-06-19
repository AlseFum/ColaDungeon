package com.coladungeon.mod.ArknightsMod.items;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Item;
import com.coladungeon.items.bags.Bag;
import com.coladungeon.mod.ArknightsMod.Operator;
import com.coladungeon.mod.ArknightsMod.operator.Phantom;
import com.coladungeon.mod.ArknightsMod.operator.TexasTheOmertosa;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteManager;
import com.watabou.utils.DeviceCompat;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class CommandTerminal extends Item {

    static {
        ItemSpriteManager.registerTexture("cola/command_terminal.png", 32)
                .label("command_terminal");
    }

    {
        image = ItemSpriteManager.ByName("command_terminal");
        defaultAction = AC_CHARGE;
        unique = true;
    }

    private static final String AC_CHARGE = "CHARGE";
    private static final String AC_DEPLOY = "DEPLOY";
    private static final String AC_CRAFT  = "CRAFT";
    private static final String COST = "cost";

    public int cost = 0;
    private static final int MAX_COST = 99;
    private static final int CHARGE_TURNS = 10; // 每10回合回复1点

    public static class TerminalCharger extends Buff {

        public int turns = 0;

        @Override
        public boolean act() {
            if (target instanceof Hero hero) {
                CommandTerminal terminal = null;
                for (Item item : hero.belongings) {
                    if (item instanceof CommandTerminal termi) {
                        terminal = termi;
                        break;
                    }
                }

                if (terminal != null && terminal.cost < MAX_COST) {
                    turns++;
                    if (turns >= CHARGE_TURNS) {
                        turns = 0;
                        terminal.cost++;
                        Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
                        updateQuickslot();
                    }
                }
            }
            spend(1f);
            return true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put("turns", turns);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            turns = bundle.getInt("turns");
        }
    }

    @Override
    public String name() {
        return "罗德岛指挥终端";
    }

    @Override
    public String desc() {
        String desc = "指挥终端，可以通过充能来获得cost点数，用于部署干员。\n\n";
        desc += "当前cost：" + cost + "/" + MAX_COST + "\n\n";
        desc += "充能：增加cost点数\n";
        desc += "部署：消耗cost点数部署干员\n\n";
        desc += "合成：消耗cost点数和其他材料来合成物资\n\n";
        return desc;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if(DeviceCompat.isDebug()){
actions.add(AC_CHARGE);
        }
        
        actions.add(AC_DEPLOY);
        actions.add(AC_CRAFT);
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
    public boolean collect(Bag container) {
        if (super.collect(container)) {
            if (container.owner != null) {
                Buff.affect(container.owner, TerminalCharger.class);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (Dungeon.hero != null) {
            Buff.detach(Dungeon.hero, TerminalCharger.class);
        }
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_CHARGE)) {
            if (cost < MAX_COST) {
                cost++;
                Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
                updateQuickslot();
            } else {
                GLog.i("cost is full");
            }
        } else if (action.equals(AC_DEPLOY)) {
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
        } else if (action.equals(AC_CRAFT)) {
            GLog.i("Not implemented yet");
        }
    }

    private void deployOperator(Hero hero, Operator operator, int requiredCost) {
        if (cost >= requiredCost) {
            cost -= requiredCost;
            operator.pos = hero.pos;
            operator.arrange = Operator.ARRANGE_CHASE;
            Dungeon.level.occupyCell(operator);
            GameScene.add(operator);
            updateQuickslot();
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
