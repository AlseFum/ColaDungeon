package com.coladungeon.items;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.scrolls.Scroll;
import com.coladungeon.items.scrolls.ScrollOfIdentify;
import com.coladungeon.items.scrolls.ScrollOfLullaby;
import com.coladungeon.items.scrolls.ScrollOfMagicMapping;
import com.coladungeon.items.scrolls.ScrollOfMirrorImage;
import com.coladungeon.items.scrolls.ScrollOfRage;
import com.coladungeon.items.scrolls.ScrollOfRecharging;
import com.coladungeon.items.scrolls.ScrollOfRemoveCurse;
import com.coladungeon.items.scrolls.ScrollOfRetribution;
import com.coladungeon.items.scrolls.ScrollOfTeleportation;
import com.coladungeon.items.scrolls.ScrollOfTerror;
import com.coladungeon.items.scrolls.ScrollOfTransmutation;
import com.coladungeon.items.scrolls.ScrollOfUpgrade;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndIconGrid;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

public class Codex extends Item {

    private Class<? extends Scroll> selectedScroll;
    public static final String AC_SELECT = "SELECT";
    public static final String AC_READ = "READ";
    {
        image = ItemSpriteSheet.SCROLL_HOLDER;
        defaultAction = AC_SELECT;
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_SELECT)) {
            hero.sprite.operate(hero.pos);

            // 使用Builder模式创建图标网格窗口
            WndIconGrid.Builder builder = new WndIconGrid.Builder()
                    .setTitle("选择卷轴")
                    .setColumns(4);
            // 添加所有卷轴图标，每个图标都有自己的点击处理逻辑
            builder.addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_IDENTIFY)),
                    "鉴定卷轴：鉴定物品",
                    () -> {
                        selectedScroll = ScrollOfIdentify.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedScroll).name());
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_UPGRADE)),
                    "升级卷轴：升级物品",
                    () -> {
                        selectedScroll = ScrollOfUpgrade.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedScroll).name());
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_TELEPORT)),
                    "传送卷轴：传送到随机位置",
                    () -> {
                        selectedScroll = ScrollOfTeleportation.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedScroll).name());
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_RAGE)),
                    "狂暴卷轴：激怒周围敌人",
                    () -> {
                        selectedScroll = ScrollOfRage.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedScroll).name());
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_RECHARGE)),
                    "充能卷轴：为法杖充能",
                    () -> {
                        selectedScroll = ScrollOfRecharging.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedScroll).name());
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_TRANSMUTE)),
                    "变形卷轴：改变物品",
                    () -> {
                        selectedScroll = ScrollOfTransmutation.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedScroll).name());
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_MIRRORIMG)),
                    "镜像卷轴：创造分身",
                    () -> {
                        selectedScroll = ScrollOfMirrorImage.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedScroll).name());
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_MAGICMAP)),
                    "魔法地图卷轴：显示地图",
                    () -> {
                        selectedScroll = ScrollOfMagicMapping.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedScroll).name());
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_LULLABY)),
                    "催眠卷轴：催眠敌人",
                    () -> {
                        selectedScroll = ScrollOfLullaby.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedScroll).name());
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_REMCURSE)),
                    "移除诅咒卷轴：移除诅咒",
                    () -> {
                        selectedScroll = ScrollOfRemoveCurse.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedScroll).name());
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_RETRIB)),
                    "惩戒卷轴：对敌人造成伤害",
                    () -> {
                        selectedScroll = ScrollOfRetribution.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedScroll).name());
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_TERROR)),
                    "恐惧卷轴：使敌人逃跑",
                    () -> {
                        selectedScroll = ScrollOfTerror.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedScroll).name());
                    }
            );

            // 显示窗口
            GameScene.show(builder.build());
        } else if (action.equals(AC_READ)) {
            if (selectedScroll == null) {
                GLog.w("请先选择要使用的卷轴！");
            } else {
                Scroll scroll = Reflection.newInstance(selectedScroll);
                scroll.curUser = hero;
                if (scroll instanceof ScrollOfUpgrade) {
                    ((ScrollOfUpgrade)scroll).reShowSelector(false);
                } else {
                    scroll.doRead();
                }
            }
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_SELECT);
        actions.add(AC_READ);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_SELECT)) {
            return "选择卷轴";
        } else if (action.equals(AC_READ)) {
            return "阅读卷轴";
        }
        return super.actionName(action, hero);
    }

    public Codex() {
        super();
        
        stackable = true;
        defaultAction = AC_SELECT;
    }

    @Override
    public String name() {
        return "卷轴选择器";
    }

    @Override
    public String desc() {
        return "这是一个可以让你选择使用各种卷轴的物品。";
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
        return 1000;
    }
}
