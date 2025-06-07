package com.coladungeon.items;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.potions.Potion;
import com.coladungeon.items.potions.PotionOfExperience;
import com.coladungeon.items.potions.PotionOfFrost;
import com.coladungeon.items.potions.PotionOfHaste;
import com.coladungeon.items.potions.PotionOfHealing;
import com.coladungeon.items.potions.PotionOfInvisibility;
import com.coladungeon.items.potions.PotionOfLevitation;
import com.coladungeon.items.potions.PotionOfLiquidFlame;
import com.coladungeon.items.potions.PotionOfMindVision;
import com.coladungeon.items.potions.PotionOfParalyticGas;
import com.coladungeon.items.potions.PotionOfPurity;
import com.coladungeon.items.potions.PotionOfStrength;
import com.coladungeon.items.potions.PotionOfToxicGas;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndIconGrid;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

public class Panacea extends Item {

    private Class<? extends Potion> selectedPotion;
    public static final String AC_TEST = "TEST";
    public static final String AC_DRINK = "DRINK";
    public static final String AC_THROW = Item.AC_THROW;
    {
        image = ItemSpriteSheet.POTION_HOLDER;
        defaultAction = AC_TEST;
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_TEST)) {
            hero.sprite.operate(hero.pos);

            // 使用Builder模式创建图标网格窗口
            WndIconGrid.Builder builder = new WndIconGrid.Builder()
                    .setTitle("选择药水")
                    .setColumns(4);
            // 添加所有药水图标，每个图标都有自己的点击处理逻辑
            builder.addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_STRENGTH)),
                    "力量药水：增加力量",
                    () -> {
                        selectedPotion = PotionOfStrength.class;
                        GLog.i("已选择力量药水");
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_HEALING)),
                    "治疗药水：恢复生命值",
                    () -> {
                        selectedPotion = PotionOfHealing.class;
                        GLog.i("已选择治疗药水");
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_MINDVIS)),
                    "心灵视觉药水：显示周围生物",
                    () -> {
                        selectedPotion = PotionOfMindVision.class;
                        GLog.i("已选择心灵视觉药水");
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_FROST)),
                    "冰冻药水：冻结目标",
                    () -> {
                        selectedPotion = PotionOfFrost.class;
                        GLog.i("已选择冰冻药水");
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_LIQFLAME)),
                    "液态火焰药水：燃烧目标",
                    () -> {
                        selectedPotion = PotionOfLiquidFlame.class;
                        GLog.i("已选择液态火焰药水");
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_TOXICGAS)),
                    "毒气药水：使目标中毒",
                    () -> {
                        selectedPotion = PotionOfToxicGas.class;
                        GLog.i("已选择毒气药水");
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_HASTE)),
                    "加速药水：提高移动速度",
                    () -> {
                        selectedPotion = PotionOfHaste.class;
                        GLog.i("已选择加速药水");
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_INVIS)),
                    "隐身药水：暂时隐身",
                    () -> {
                        selectedPotion = PotionOfInvisibility.class;
                        GLog.i("已选择隐身药水");
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_LEVITATE)),
                    "漂浮药水：可以漂浮",
                    () -> {
                        selectedPotion = PotionOfLevitation.class;
                        GLog.i("已选择漂浮药水");
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_PARAGAS)),
                    "麻痹药水：使目标麻痹",
                    () -> {
                        selectedPotion = PotionOfParalyticGas.class;
                        GLog.i("已选择麻痹药水");
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_PURITY)),
                    "净化药水：清除负面效果",
                    () -> {
                        selectedPotion = PotionOfPurity.class;
                        GLog.i("已选择净化药水");
                    }
            ).addItem(
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_EXP)),
                    "经验药水：获得经验值",
                    () -> {
                        selectedPotion = PotionOfExperience.class;
                        GLog.i("已选择经验药水");
                    }
            );

            // 显示窗口
            GameScene.show(builder.build());
        } else if (action.equals(AC_DRINK)) {
            if (selectedPotion != null) {
                Potion potion = Reflection.newInstance(selectedPotion);
                potion.apply(hero);
                defaultAction = AC_DRINK;
                GLog.i("喝下了" + potion.name());
            } else {
                GLog.w("还没有选择药水！");
            }
        } else if (action.equals(AC_THROW)) {
            if (selectedPotion != null) {
                Potion potion = Reflection.newInstance(selectedPotion);
                potion.identify();
                potion.setCurrent(hero);
                defaultAction = AC_THROW;
                GameScene.selectCell(new CellSelector.Listener() {
                    @Override
                    public void onSelect(Integer target) {
                        if (target != null) {
                            potion.cast(hero, target);
                        }
                    }

                    @Override
                    public String prompt() {
                        return Messages.get(Item.class, "prompt");
                    }
                });
            } else {
                GLog.w("还没有选择药水！");
            }
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_TEST);
        actions.add(AC_DRINK);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_TEST)) {
            return "选择药水";
        } else if (action.equals(AC_DRINK)) {
            return "喝下药水";
        } else if (action.equals(AC_THROW)) {
            return "扔出药水";
        }
        return super.actionName(action, hero);
    }

    public Panacea() {
        super();
        image = ItemSpriteSheet.SOMETHING;
        stackable = true;
        defaultAction = AC_TEST;
    }

    @Override
    public String name() {
        return "药水选择器";
    }

    @Override
    public String desc() {
        return "这是一个可以让你选择使用各种药水的物品。";
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
