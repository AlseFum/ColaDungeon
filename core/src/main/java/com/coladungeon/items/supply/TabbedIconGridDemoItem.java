package com.coladungeon.items.supply;

import java.util.ArrayList;

import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Item;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.scenes.PixelScene;
import com.coladungeon.sprites.ItemSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.ui.Icons;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndTabbedIconGrid;

public class TabbedIconGridDemoItem extends Item {
    
    {
        image = ItemSpriteSheet.SCROLL_HOLDER;
        stackable = false;
        unique = true;
    }
    
    @Override
    public String name() {
        return "TabbedIconGrid演示";
    }
    
    @Override
    public String desc() {
        return "使用后显示WndTabbedIconGrid窗口的演示，包含多个带图标的tab、IconGrid网格、可选的slider和确认按钮。图标tab节省空间，界面更整洁。";
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
    
    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add("使用");
        return actions;
    }
    
    @Override
    public void execute(Hero hero, String action) {
        if (action.equals("使用")) {
            showDemo();
        } else {
            super.execute(hero, action);
        }
    }
    
    private void showDemo() {
        // 创建窗口构建器 - 使用3列便于测试布局
        WndTabbedIconGrid.Builder builder = new WndTabbedIconGrid.Builder()
            .setTitle("装备选择器演示")
            .setColumns(3); // 改为3列便于测试
        
        // 添加第一个tab - 武器选择（简化内容）
        builder.addTab("武器", Icons.get(Icons.TARGET));
        builder.addItemToTab(0, new ItemSprite(ItemSpriteSheet.SWORD), "长剑 - 基础武器", () -> {
            GLog.i("选择了长剑");
        });
        builder.addItemToTab(0, new ItemSprite(ItemSpriteSheet.MAGES_STAFF), "法杖 - 魔法武器", () -> {
            GLog.i("选择了法杖");
        });
        builder.addItemToTab(0, new ItemSprite(ItemSpriteSheet.DAGGER), "匕首 - 敏捷武器", () -> {
            GLog.i("选择了匕首");
        });
        builder.addItemToTab(0, new ItemSprite(ItemSpriteSheet.MACE), "钉锤 - 重型武器", () -> {
            GLog.i("选择了钉锤");
        });
        builder.addItemToTab(0, new ItemSprite(ItemSpriteSheet.SPIRIT_BOW), "精神弓 - 远程武器", () -> {
            GLog.i("选择了精神弓");
        });
        builder.addItemToTab(0, new ItemSprite(ItemSpriteSheet.CROSSBOW), "弩 - 机械武器", () -> {
            GLog.i("选择了弩");
        });
        
        // 添加第二个tab - 防具选择
        builder.addTab("防具", Icons.get(Icons.CHECKED));
        builder.addItemToTab(1, new ItemSprite(ItemSpriteSheet.ARMOR_CLOTH), "布甲 - 轻型防具", () -> {
            GLog.i("选择了布甲");
        });
        builder.addItemToTab(1, new ItemSprite(ItemSpriteSheet.ARMOR_LEATHER), "皮甲 - 中型防具", () -> {
            GLog.i("选择了皮甲");
        });
        builder.addItemToTab(1, new ItemSprite(ItemSpriteSheet.ARMOR_MAIL), "锁甲 - 重型防具", () -> {
            GLog.i("选择了锁甲");
        });
        builder.addItemToTab(1, new ItemSprite(ItemSpriteSheet.GREATSHIELD), "巨盾 - 防护装备", () -> {
            GLog.i("选择了巨盾");
        });
        
        // 添加第三个tab - 饰品（带slider）
        builder.addTab("饰品", Icons.get(Icons.BACKPACK_LRG));
        builder.addItemToTab(2, new ItemSprite(ItemSpriteSheet.RING_GARNET), "石榴石戒指", () -> {
            GLog.i("选择了石榴石戒指");
        });
        builder.addItemToTab(2, new ItemSprite(ItemSpriteSheet.RING_RUBY), "红宝石戒指", () -> {
            GLog.i("选择了红宝石戒指");
        });
        builder.addItemToTab(2, new ItemSprite(ItemSpriteSheet.RING_TOPAZ), "黄玉戒指", () -> {
            GLog.i("选择了黄玉戒指");
        });
        
        // 为第三个tab添加slider
        builder.setTabSlider(2, true);
        builder.setTabSliderRange(2, 0, 10);
        builder.setTabSliderValue(2, 5);
        builder.setTabSliderLabel(2, "强化等级");
        builder.setTabSliderChangeCallback(2, () -> {
            GLog.i("强化等级调整为: +" + builder.build().getSliderValue(2));
        });
        
        // 构建并显示窗口
        WndTabbedIconGrid window = builder.build();
        GameScene.show(window);
    }
} 