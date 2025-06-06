package com.coladungeon.items.supply;

import java.util.ArrayList;

import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Item;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.scenes.PixelScene;
import com.coladungeon.sprites.ItemSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.ui.Icons;
import com.watabou.noosa.Image;
import com.coladungeon.Assets;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.Dungeon;

import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndTabbed;
import com.coladungeon.windows.WndOptions;
import com.coladungeon.windows.WndInfoItem;
import com.coladungeon.windows.WndMessage;
import com.watabou.noosa.ui.Component;

public class testitem extends Item {
    public static final String AC_USE = "USE";
    
    {
        defaultAction = AC_USE;
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_USE)) {
            hero.sprite.operate(hero.pos);
            
            // 创建一个带标签的窗口
            WndTabbed tabbed = new WndTabbed() {
                private Component infoTab;
                private Component optionsTab;
                private Component itemTab;
                
                {
                    // 设置窗口大小
                    resize(120, 120);
                    
                    // 创建第一个标签页 - 基本信息
                    infoTab = new Component();
                    WndMessage info = new WndMessage("这是一个测试物品，用于展示如何使用 TabWindow。\n\n" +
                            "这个窗口展示了如何创建带标签的界面，每个标签页可以包含不同的内容。\n\n" +
                            "你可以点击不同的标签来查看不同的内容。");
                    infoTab.add(info);
                    infoTab.setRect(0, 0, width, height);
                    
                    // 创建第二个标签页 - 选项列表
                    optionsTab = new Component();
                    WndOptions options = new WndOptions("选项示例", "这是一个选项列表的示例：", 
                            "选项 1", "选项 2", "选项 3") {
                        @Override
                        protected void onSelect(int index) {
                            switch (index) {
                                case 0:
                                    GLog.i("你选择了选项 1");
                                    break;
                                case 1:
                                    GLog.i("你选择了选项 2");
                                    break;
                                case 2:
                                    GLog.i("你选择了选项 3");
                                    break;
                            }
                        }
                    };
                    optionsTab.add(options);
                    optionsTab.setRect(0, 0, width, height);
                    
                    // 创建第三个标签页 - 物品信息
                    itemTab = new Component();
                    WndInfoItem itemInfo = new WndInfoItem(testitem.this);
                    itemTab.add(itemInfo);
                    itemTab.setRect(0, 0, width, height);
                    
                    // 添加标签页
                    add(new LabeledTab("基本信息") {
                        @Override
                        protected void select(boolean value) {
                            super.select(value);
                            infoTab.visible = infoTab.active = value;
                        }
                    });
                    
                    add(new LabeledTab("选项列表") {
                        @Override
                        protected void select(boolean value) {
                            super.select(value);
                            optionsTab.visible = optionsTab.active = value;
                        }
                    });
                    
                    add(new LabeledTab("物品信息") {
                        @Override
                        protected void select(boolean value) {
                            super.select(value);
                            itemTab.visible = itemTab.active = value;
                        }
                    });
                    
                    // 添加内容
                    add(infoTab);
                    add(optionsTab);
                    add(itemTab);
                    
                    // 布局标签
                    layoutTabs();
                    
                    // 选择第一个标签
                    select(0);
                }
                
                @Override
                public void offset(int xOffset, int yOffset) {
                    super.offset(xOffset, yOffset);
                    infoTab.setRect(0, 0, width, height);
                    optionsTab.setRect(0, 0, width, height);
                    itemTab.setRect(0, 0, width, height);
                }
            };
            
            // 显示窗口
            GameScene.show(tabbed);
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_USE);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_USE)) {
            return "使用";
        }
        return super.actionName(action, hero);
    }

    @Override
    public String name() {
        return "TabWindow 示例";
    }

    @Override
    public String desc() {
        return "这是一个用于展示如何使用 TabWindow 的测试物品。";
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
        return 0;
    }
}
