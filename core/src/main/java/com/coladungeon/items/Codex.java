package com.coladungeon.items;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.scrolls.InventoryScroll;
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
import com.coladungeon.items.scrolls.exotic.ScrollOfAntiMagic;
import com.coladungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.coladungeon.items.scrolls.exotic.ScrollOfDivination;
import com.coladungeon.items.scrolls.exotic.ScrollOfDread;
import com.coladungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.coladungeon.items.scrolls.exotic.ScrollOfForesight;
import com.coladungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.coladungeon.items.scrolls.exotic.ScrollOfMysticalEnergy;
import com.coladungeon.items.scrolls.exotic.ScrollOfPassage;
import com.coladungeon.items.scrolls.exotic.ScrollOfPrismaticImage;
import com.coladungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.coladungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.ui.Icons;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndTabbedIconGrid;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

public class Codex extends Item {

    private Class<? extends Scroll> selectedScroll;
    public static final String AC_SELECT = "SELECT";
    public static final String AC_READ = "READ";
    public static final String AC_GENERATE = "GENERATE";

    {
        image = ItemSpriteSheet.SCROLL_HOLDER;
        defaultAction = AC_READ;
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_SELECT)) {
            hero.sprite.operate(hero.pos);

            // 使用WndTabbedIconGrid构建器创建分标签页窗口
            WndTabbedIconGrid.Builder builder = new WndTabbedIconGrid.Builder()
                    .setTitle("选择卷轴")
                    .setColumns(4);
            
            // 添加Regular Scrolls标签页
            builder.addTab("标准卷轴", Icons.get(Icons.SCROLL_COLOR));
            
            // 添加标准卷轴到第一个标签页（索引0）
            builder.addItemToTab(0, 
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_IDENTIFY)),
                    "鉴定卷轴：鉴定物品",
                    () -> selectedScroll = ScrollOfIdentify.class
            );
            builder.addItemToTab(0,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_UPGRADE)),
                    "升级卷轴：升级物品",
                    () -> selectedScroll = ScrollOfUpgrade.class
            );
            builder.addItemToTab(0,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_TELEPORT)),
                    "传送卷轴：传送到随机位置",
                    () -> selectedScroll = ScrollOfTeleportation.class
            );
            builder.addItemToTab(0,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_RAGE)),
                    "狂暴卷轴：激怒周围敌人",
                    () -> selectedScroll = ScrollOfRage.class
            );
            builder.addItemToTab(0,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_RECHARGE)),
                    "充能卷轴：为法杖充能",
                    () -> selectedScroll = ScrollOfRecharging.class
            );
            builder.addItemToTab(0,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_TRANSMUTE)),
                    "嬗变卷轴：改变物品",
                    () -> selectedScroll = ScrollOfTransmutation.class
            );
            builder.addItemToTab(0,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_MIRRORIMG)),
                    "镜像卷轴：创造分身",
                    () -> selectedScroll = ScrollOfMirrorImage.class
            );
            builder.addItemToTab(0,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_MAGICMAP)),
                    "魔法地图卷轴：显示地图",
                    () -> selectedScroll = ScrollOfMagicMapping.class
            );
            builder.addItemToTab(0,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_LULLABY)),
                    "催眠卷轴：催眠敌人",
                    () -> selectedScroll = ScrollOfLullaby.class
            );
            builder.addItemToTab(0,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_REMCURSE)),
                    "移除诅咒卷轴：移除诅咒",
                    () -> selectedScroll = ScrollOfRemoveCurse.class
            );
            builder.addItemToTab(0,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_RETRIB)),
                    "惩戒卷轴：对敌人造成伤害",
                    () -> selectedScroll = ScrollOfRetribution.class
            );
            builder.addItemToTab(0,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_TERROR)),
                    "恐惧卷轴：使敌人逃跑",
                    () -> selectedScroll = ScrollOfTerror.class
            );
            
            // 添加Exotic Scrolls标签页
            builder.addTab("增强卷轴", Icons.get(Icons.SCROLL_COLOR));
            
            // 添加异域卷轴到第二个标签页（索引1）
            builder.addItemToTab(1,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_DIVINATE)),
                    "占卜卷轴：探知物品信息",
                    () -> selectedScroll = ScrollOfDivination.class
            );
            builder.addItemToTab(1,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_ENCHANT)),
                    "附魔卷轴：为装备附魔",
                    () -> selectedScroll = ScrollOfEnchantment.class
            );
            builder.addItemToTab(1,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_PASSAGE)),
                    "通道卷轴：创造传送门",
                    () -> selectedScroll = ScrollOfPassage.class
            );
            builder.addItemToTab(1,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_CHALLENGE)),
                    "挑战卷轴：激怒并强化敌人",
                    () -> selectedScroll = ScrollOfChallenge.class
            );
            builder.addItemToTab(1,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_MYSTENRG)),
                    "神秘能量卷轴：强力充能",
                    () -> selectedScroll = ScrollOfMysticalEnergy.class
            );
            builder.addItemToTab(1,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_METAMORPH)),
                    "变形卷轴：改变职业特性",
                    () -> selectedScroll = ScrollOfMetamorphosis.class
            );
            builder.addItemToTab(1,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_PRISIMG)),
                    "棱镜镜像卷轴：创造彩色分身",
                    () -> selectedScroll = ScrollOfPrismaticImage.class
            );
            builder.addItemToTab(1,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_FORESIGHT)),
                    "预知卷轴：显示详细地图",
                    () -> selectedScroll = ScrollOfForesight.class
            );
            builder.addItemToTab(1,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_SIREN)),
                    "海妖之歌卷轴：魅惑敌人",
                    () -> selectedScroll = ScrollOfSirensSong.class
            );
            builder.addItemToTab(1,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_ANTIMAGIC)),
                    "反魔法卷轴：移除魔法效果",
                    () -> selectedScroll = ScrollOfAntiMagic.class
            );
            builder.addItemToTab(1,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_PSIBLAST)),
                    "心灵爆炸卷轴：精神伤害",
                    () -> selectedScroll = ScrollOfPsionicBlast.class
            );
            builder.addItemToTab(1,
                    new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_DREAD)),
                    "恐怖卷轴：造成持续恐惧",
                    () -> selectedScroll = ScrollOfDread.class
            );

            // 显示窗口
            GameScene.show(builder.build());
        } else if (action.equals(AC_READ)) {
            if (selectedScroll == null) {
                GLog.w("请先选择要使用的卷轴！");
            } else {
                Scroll scroll = Reflection.newInstance(selectedScroll);
                scroll.curUser = hero;
                if (scroll instanceof ScrollOfIdentify) {
                    // 鉴定卷轴特殊处理
                    if (scroll instanceof InventoryScroll) {
                        ((InventoryScroll)scroll).doRead();
                    } else {
                        scroll.doRead();
                    }
                } else if (scroll instanceof ScrollOfUpgrade) {
                    // 升级卷轴特殊处理
                    if (scroll instanceof InventoryScroll) {
                        ((InventoryScroll)scroll).doRead();
                    } else {
                        scroll.doRead();
                    }
                } else if (scroll instanceof ScrollOfTransmutation) {
                    // 嬗变卷轴特殊处理
                    if (scroll instanceof InventoryScroll) {
                        ((InventoryScroll)scroll).doRead();
                    } else {
                        scroll.doRead();
                    }
                } else if (scroll instanceof InventoryScroll) {
                    ((InventoryScroll)scroll).doRead();
                } else {
                    scroll.doRead();
                }
            }
        } else if (action.equals(AC_GENERATE)) {
            if (selectedScroll == null) {
                GLog.w("请先选择要生成的卷轴！");
            } else {
                Scroll scroll = Reflection.newInstance(selectedScroll);
                scroll.identify();
                if (scroll.collect(hero.belongings.backpack)) {
                    GLog.p("生成了 " + scroll.name() + "！");
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
        actions.add(AC_READ);
        actions.add(AC_GENERATE);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_SELECT)) {
            return "选择卷轴";
        } else if (action.equals(AC_READ)) {
            return "阅读卷轴";
        } else if (action.equals(AC_GENERATE)) {
            return "生成卷轴";
        }
        return super.actionName(action, hero);
    }

    public Codex() {
        super();
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
