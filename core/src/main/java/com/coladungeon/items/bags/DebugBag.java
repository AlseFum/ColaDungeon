package com.coladungeon.items.bags;

import com.coladungeon.items.Item;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.items.DivineAnkh;
import com.coladungeon.items.RedStone;
import com.coladungeon.items.Codex;
import com.coladungeon.items.Panacea;

import com.coladungeon.items.ItemRemover;
import com.coladungeon.items.supply.Supply;
import com.coladungeon.items.supply.testitem;
public class DebugBag extends Bag {

    {
        image = ItemSpriteSheet.BACKPACK;
        unique = true;
    }

    @Override
    public boolean canHold(Item item) {
        return item instanceof DivineAnkh
            || item instanceof RedStone
            || item instanceof Codex
            || item instanceof Panacea
            || item instanceof Supply
            || item instanceof ItemRemover
            || item instanceof testitem
            ; 
    }

    @Override
    public int capacity() {
        return 30; // 弹药和枪械的容量限制
    }

    @Override
    public String name() {
        return "调试包";
    }

    @Override
    public String desc() {
        return "";
    }
} 