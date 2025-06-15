package com.coladungeon.items;

import java.util.ArrayList;

import com.coladungeon.actors.hero.Hero;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.windows.WndBag;
import com.coladungeon.Dungeon;
import com.coladungeon.windows.WndOptions;
import com.coladungeon.ui.InventoryPane;


public class ItemRemover extends Item {

    {
        image = ItemSpriteSheet.TORCH;
        stackable = false;
        unique = true;
        defaultAction=AC_REMOVE;
    }

    public static final String AC_REMOVE = "REMOVE";
    public static final String AC_CLEAR = "CLEAR";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_REMOVE);
        actions.add(AC_CLEAR);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_REMOVE)) {
            GameScene.selectItem(new WndBag.ItemSelector() {
                @Override
                public String textPrompt() {
                    return "Select an item to remove(click elsewhere to cancel)";
                }

                @Override
                public boolean itemSelectable(Item item) {
                    return true;
                }

                @Override
                public void onSelect(Item item) {
                    if (item == null) {
                        return;
                    }
                    if (item.quantity > 5) {
                        GameScene.show(WndOptions.make()
                        .title("删除物品")
                        .message("要删除多少？")
                        .option("1份",(Object o)->{
                            item.quantity = item.quantity - 1;
                        })
                        .option("5分之一("+(item.quantity-(int)item.quantity*4/5)+")",(Object o)->{
                            item.quantity = (int)item.quantity*4/5;
                        })
                        .option("全部",(Object o)->{
                            item.quantity=1;
                            item.detach(hero.belongings.backpack);
                            InventoryPane.refresh();
                        })
                        .build());
                    } else {
                        item.detach(hero.belongings.backpack);
                        InventoryPane.refresh();
                    }

                    // GameScene.selectItem(this);
                }
            });
        } else if (action.equals(AC_CLEAR)) {
            GameScene.selectCell("选择清除的格子", (Integer pos) -> {
                if (pos == -1) {
                    return;
                }

                Heap heap = Dungeon.level.heaps.get(pos);
                if (heap != null) {
                    heap.destroy();
                }
            });
        }
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_REMOVE)) {
            return "删除物品";
        }
        if (action.equals(AC_CLEAR)) {
            return "清除格子";
        }
        return super.actionName(action, hero);
    }

    @Override
    public String name() {
        return "Item Remover";
    }

    @Override
    public String desc() {
        return "to delete item more easily";
    }
}
