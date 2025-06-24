package com.coladungeon.items;

import java.util.ArrayList;

import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Item;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.ui.Icons;
import com.watabou.noosa.Image;
import com.coladungeon.Assets;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.Dungeon;

import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndIconGrid;
import com.coladungeon.items.stones.StoneOfAugmentation;
import com.coladungeon.items.stones.StoneOfEnchantment;
import com.coladungeon.items.stones.StoneOfBlast;
import com.coladungeon.items.stones.StoneOfDisarming;
import com.coladungeon.items.stones.StoneOfFear;
import com.coladungeon.items.stones.StoneOfFlock;
import com.coladungeon.items.stones.StoneOfIntuition;
import com.coladungeon.items.stones.StoneOfDetectMagic;
import com.coladungeon.items.stones.StoneOfShock;
import com.coladungeon.items.stones.StoneOfGeneration;
import com.coladungeon.items.stones.StoneOfDungeonTravel;
import com.coladungeon.items.stones.StoneOfDeepSleep;
import com.coladungeon.items.stones.StoneOfClairvoyance;
import com.coladungeon.items.stones.StoneOfBlink;
import com.coladungeon.items.stones.StoneOfAggression;
import com.coladungeon.items.stones.Runestone;
import com.coladungeon.items.stones.InventoryStone;
import com.watabou.utils.Reflection;

public class RedStone extends Item {

    private Class<? extends Runestone> selectedStone;
    public static final String AC_SELECT = "SELECT";
    public static final String AC_USE = "USE";
    public static final String AC_THROW = "THROW";
    public static final String AC_GENERATE = "GENERATE";
    
    {
        image = ItemSpriteSheet.STONE_HOLDER;
        defaultAction = AC_SELECT;
        stackable = true;
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_SELECT)) {
            hero.sprite.operate(hero.pos);

            // 使用Builder模式创建图标网格窗口
            WndIconGrid.Builder builder = new WndIconGrid.Builder()
                    .setTitle("选择符文石")
                    .setColumns(5);
            // 添加所有符文石图标，每个图标都有自己的点击处理逻辑
            builder.addItem(
                    new ItemSprite(ItemSpriteSheet.STONE_AUGMENTATION),
                    "增强符文石：增强武器和护甲",
                    () -> {
                        selectedStone = StoneOfAugmentation.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedStone).name());
                    }
            ).addItem(
                    new ItemSprite(ItemSpriteSheet.STONE_ENCHANT),
                    "附魔符文石：为武器和护甲附魔",
                    () -> {
                        selectedStone = StoneOfEnchantment.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedStone).name());
                    }
            ).addItem(
                    new ItemSprite(ItemSpriteSheet.STONE_BLAST),
                    "爆炸符文石：制造爆炸",
                    () -> {
                        selectedStone = StoneOfBlast.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedStone).name());
                    }
            ).addItem(
                    new ItemSprite(ItemSpriteSheet.STONE_DETECT),
                    "解除符文石：解除陷阱",
                    () -> {
                        selectedStone = StoneOfDisarming.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedStone).name());
                    }
            ).addItem(
                    new ItemSprite(ItemSpriteSheet.STONE_FEAR),
                    "恐惧符文石：使目标逃跑",
                    () -> {
                        selectedStone = StoneOfFear.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedStone).name());
                    }
            ).addItem(
                    new ItemSprite(ItemSpriteSheet.STONE_FLOCK),
                    "羊群符文石：召唤魔法羊",
                    () -> {
                        selectedStone = StoneOfFlock.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedStone).name());
                    }
            ).addItem(
                    new ItemSprite(ItemSpriteSheet.STONE_INTUITION),
                    "直觉符文石：猜测物品类型",
                    () -> {
                        selectedStone = StoneOfIntuition.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedStone).name());
                    }
            ).addItem(
                    new ItemSprite(ItemSpriteSheet.STONE_DETECT),
                    "魔法探测符文石：探测魔法物品",
                    () -> {
                        selectedStone = StoneOfDetectMagic.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedStone).name());
                    }
            ).addItem(
                    new ItemSprite(ItemSpriteSheet.STONE_SHOCK),
                    "震击符文石：造成范围伤害",
                    () -> {
                        selectedStone = StoneOfShock.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedStone).name());
                    }
            ).addItem(
                    new ItemSprite(ItemSpriteSheet.STONE_BLINK),
                    "生成符文石：生成随机物品",
                    () -> {
                        selectedStone = StoneOfGeneration.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedStone).name());
                    }
            ).addItem(
                    new ItemSprite(ItemSpriteSheet.STONE_SHOCK),
                    "传送符文石：传送到其他楼层",
                    () -> {
                        selectedStone = StoneOfDungeonTravel.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedStone).name());
                    }
            ).addItem(
                    new ItemSprite(ItemSpriteSheet.STONE_SLEEP),
                    "沉睡符文石：使目标陷入沉睡",
                    () -> {
                        selectedStone = StoneOfDeepSleep.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedStone).name());
                    }
            ).addItem(
                    new ItemSprite(ItemSpriteSheet.STONE_CLAIRVOYANCE),
                    "透视符文石：揭示周围区域",
                    () -> {
                        selectedStone = StoneOfClairvoyance.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedStone).name());
                    }
            ).addItem(
                    new ItemSprite(ItemSpriteSheet.STONE_BLINK),
                    "闪烁符文石：传送到目标位置",
                    () -> {
                        selectedStone = StoneOfBlink.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedStone).name());
                    }
            ).addItem(
                    new ItemSprite(ItemSpriteSheet.STONE_AGGRESSION),
                    "激怒符文石：使目标变得敌对",
                    () -> {
                        selectedStone = StoneOfAggression.class;
                        GLog.i("选择了" + Reflection.newInstance(selectedStone).name());
                    }
            );

            // 显示窗口
            GameScene.show(builder.build());
        } else if (action.equals(AC_USE)) {
            if (selectedStone == null) {
                GLog.w("请先选择要使用的符文石！");
            } else {
                Runestone stone = Reflection.newInstance(selectedStone);
                stone.curUser = hero;
                curUser = hero;
                curItem = stone;
                if (stone instanceof InventoryStone) {
                    // 对于背包符文石，使用directActivate方法
                    ((InventoryStone)stone).directActivate();
                } else {
                    // 对于需要选择目标的符文石，显示目标选择界面
                    GameScene.selectCell(new CellSelector.Listener() {
                        @Override
                        public void onSelect(Integer target) {
                            if (target != null) {
                                stone.onThrow(target);
                            }
                        }
                        @Override
                        public String prompt() {
                            return "选择使用目标位置";
                        }
                    });
                }
            }
        } else if (action.equals(AC_THROW)) {
            if (selectedStone == null) {
                GLog.w("请先选择要投掷的符文石！");
            } else {
                Runestone stone = Reflection.newInstance(selectedStone);
                stone.curUser = hero;
                curUser = hero;
                curItem = this;
                GameScene.selectCell(new CellSelector.Listener() {
                    @Override
                    public void onSelect(Integer target) {
                        if (target != null) {
                            stone.cast(hero, target);
                        }
                    }
                    @Override
                    public String prompt() {
                        return "选择投掷目标位置";
                    }
                });
            }
        } else if (action.equals(AC_GENERATE)) {
            if (selectedStone == null) {
                GLog.w("请先选择要生成的符文石！");
            } else {
                Runestone stone = Reflection.newInstance(selectedStone);
                stone.identify();
                if (stone.collect(hero.belongings.backpack)) {
                    GLog.p("生成了 " + stone.name() + "！");
                } else {
                    GLog.w("背包已满，无法生成物品！");
                }
            }
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_SELECT);
        actions.add(AC_USE);
        actions.add(AC_THROW);
        actions.add(AC_GENERATE);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_SELECT)) {
            return "选择符文石";
        } else if (action.equals(AC_USE)) {
            return "使用符文石";
        } else if (action.equals(AC_THROW)) {
            return "投掷符文石";
        } else if (action.equals(AC_GENERATE)) {
            return "生成符文石";
        }
        return super.actionName(action, hero);
    }

    @Override
    public String name() {
        String name = "符文石选择器";
        if (selectedStone != null) {
            name += " (" + Reflection.newInstance(selectedStone).name() + ")";
        }
        return name;
    }

    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder();
        desc.append("这是一个可以让你选择使用各种符文石的物品。");
        if (selectedStone != null) {
            desc.append("\n\n当前选择：").append(Reflection.newInstance(selectedStone).name());
            desc.append("\n").append(Reflection.newInstance(selectedStone).desc());
        } else {
            desc.append("\n\n请先选择一个符文石。");
        }
        return desc.toString();
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
