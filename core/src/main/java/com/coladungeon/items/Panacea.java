package com.coladungeon.items;

import java.util.ArrayList;

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
import com.coladungeon.items.potions.brews.AquaBrew;
import com.coladungeon.items.potions.brews.BlizzardBrew;
import com.coladungeon.items.potions.brews.CausticBrew;
import com.coladungeon.items.potions.brews.InfernalBrew;
import com.coladungeon.items.potions.brews.ShockingBrew;
import com.coladungeon.items.potions.brews.UnstableBrew;
import com.coladungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.coladungeon.items.potions.elixirs.ElixirOfArcaneArmor;
import com.coladungeon.items.potions.elixirs.ElixirOfDragonsBlood;
import com.coladungeon.items.potions.elixirs.ElixirOfFeatherFall;
import com.coladungeon.items.potions.elixirs.ElixirOfHoneyedHealing;
import com.coladungeon.items.potions.elixirs.ElixirOfIcyTouch;
import com.coladungeon.items.potions.elixirs.ElixirOfMight;
import com.coladungeon.items.potions.elixirs.ElixirOfToxicEssence;
import com.coladungeon.items.potions.exotic.PotionOfCleansing;
import com.coladungeon.items.potions.exotic.PotionOfCorrosiveGas;
import com.coladungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.coladungeon.items.potions.exotic.PotionOfDragonsBreath;
import com.coladungeon.items.potions.exotic.PotionOfEarthenArmor;
import com.coladungeon.items.potions.exotic.PotionOfMagicalSight;
import com.coladungeon.items.potions.exotic.PotionOfMastery;
import com.coladungeon.items.potions.exotic.PotionOfShielding;
import com.coladungeon.items.potions.exotic.PotionOfShroudingFog;
import com.coladungeon.items.potions.exotic.PotionOfSnapFreeze;
import com.coladungeon.items.potions.exotic.PotionOfStamina;
import com.coladungeon.items.potions.exotic.PotionOfStormClouds;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.ui.Icons;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndTabbedIconGrid;
import com.watabou.noosa.Image;
import com.coladungeon.Assets;
import com.watabou.utils.Reflection;

public class Panacea extends Item {

    private Class<? extends Potion> selectedPotion;
    public static final String AC_SELECT = "SELECT";
    public static final String AC_DRINK = "DRINK";
    public static final String AC_GENERATE = "GENERATE";

    {
        image = ItemSpriteSheet.POTION_HOLDER;
        defaultAction = AC_DRINK;
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_SELECT)) {
            hero.sprite.operate(hero.pos);

            // 使用WndTabbedIconGrid构建器创建分标签页窗口
            WndTabbedIconGrid.Builder builder = new WndTabbedIconGrid.Builder()
                    .setTitle("选择药剂")
                    .setColumns(4);
            
            // 添加标准药剂标签页
            builder.addTab("标准药剂", Icons.get(Icons.POTION_BANDOLIER));
            
            // 添加标准药剂到第一个标签页（索引0）
            builder.addItemToTab(0,
                    createPotionIcon(PotionOfStrength.class),
                    "力量药剂：增加力量",
                    () -> selectedPotion = PotionOfStrength.class
            );
            builder.addItemToTab(0,
                    createPotionIcon(PotionOfHealing.class),
                    "治疗药剂：恢复生命值",
                    () -> selectedPotion = PotionOfHealing.class
            );
            builder.addItemToTab(0,
                    createPotionIcon(PotionOfMindVision.class),
                    "心灵视觉药剂：显示周围生物",
                    () -> selectedPotion = PotionOfMindVision.class
            );
            builder.addItemToTab(0,
                    createPotionIcon(PotionOfFrost.class),
                    "冰冻药剂：冻结目标",
                    () -> selectedPotion = PotionOfFrost.class
            );
            builder.addItemToTab(0,
                    createPotionIcon(PotionOfLiquidFlame.class),
                    "液态火焰药剂：燃烧目标",
                    () -> selectedPotion = PotionOfLiquidFlame.class
            );
            builder.addItemToTab(0,
                    createPotionIcon(PotionOfToxicGas.class),
                    "毒气药剂：使目标中毒",
                    () -> selectedPotion = PotionOfToxicGas.class
            );
            builder.addItemToTab(0,
                    createPotionIcon(PotionOfHaste.class),
                    "加速药剂：提高移动速度",
                    () -> selectedPotion = PotionOfHaste.class
            );
            builder.addItemToTab(0,
                    createPotionIcon(PotionOfInvisibility.class),
                    "隐身药剂：暂时隐身",
                    () -> selectedPotion = PotionOfInvisibility.class
            );
            builder.addItemToTab(0,
                    createPotionIcon(PotionOfLevitation.class),
                    "漂浮药剂：可以漂浮",
                    () -> selectedPotion = PotionOfLevitation.class
            );
            builder.addItemToTab(0,
                    createPotionIcon(PotionOfParalyticGas.class),
                    "麻痹药剂：使目标麻痹",
                    () -> selectedPotion = PotionOfParalyticGas.class
            );
            builder.addItemToTab(0,
                    createPotionIcon(PotionOfPurity.class),
                    "净化药剂：清除负面效果",
                    () -> selectedPotion = PotionOfPurity.class
            );
            builder.addItemToTab(0,
                    createPotionIcon(PotionOfExperience.class),
                    "经验药剂：获得经验值",
                    () -> selectedPotion = PotionOfExperience.class
            );
            
            // 添加异域药剂标签页
            builder.addTab("异域药剂", Icons.get(Icons.ALCHEMY));
            
            // 添加异域药剂到第二个标签页（索引1）
            builder.addItemToTab(1,
                    createPotionIcon(PotionOfMastery.class),
                    "精通药剂：提升技能等级",
                    () -> selectedPotion = PotionOfMastery.class
            );
            builder.addItemToTab(1,
                    createPotionIcon(PotionOfShielding.class),
                    "护盾药剂：获得护盾",
                    () -> selectedPotion = PotionOfShielding.class
            );
            builder.addItemToTab(1,
                    createPotionIcon(PotionOfMagicalSight.class),
                    "魔法视觉药剂：看穿魔法",
                    () -> selectedPotion = PotionOfMagicalSight.class
            );
            builder.addItemToTab(1,
                    createPotionIcon(PotionOfSnapFreeze.class),
                    "急冻药剂：瞬间冻结",
                    () -> selectedPotion = PotionOfSnapFreeze.class
            );
            builder.addItemToTab(1,
                    createPotionIcon(PotionOfDragonsBreath.class),
                    "龙息药剂：喷吐火焰",
                    () -> selectedPotion = PotionOfDragonsBreath.class
            );
            builder.addItemToTab(1,
                    createPotionIcon(PotionOfCorrosiveGas.class),
                    "腐蚀毒气药剂：强力腐蚀",
                    () -> selectedPotion = PotionOfCorrosiveGas.class
            );
            builder.addItemToTab(1,
                    createPotionIcon(PotionOfStamina.class),
                    "耐力药剂：持久加速",
                    () -> selectedPotion = PotionOfStamina.class
            );
            builder.addItemToTab(1,
                    createPotionIcon(PotionOfShroudingFog.class),
                    "迷雾药剂：范围隐身",
                    () -> selectedPotion = PotionOfShroudingFog.class
            );
            builder.addItemToTab(1,
                    createPotionIcon(PotionOfStormClouds.class),
                    "风暴云药剂：召唤雷暴",
                    () -> selectedPotion = PotionOfStormClouds.class
            );
            builder.addItemToTab(1,
                    createPotionIcon(PotionOfEarthenArmor.class),
                    "大地护甲药剂：石肤防护",
                    () -> selectedPotion = PotionOfEarthenArmor.class
            );
            builder.addItemToTab(1,
                    createPotionIcon(PotionOfCleansing.class),
                    "净化药剂：深层清洁",
                    () -> selectedPotion = PotionOfCleansing.class
            );
            builder.addItemToTab(1,
                    createPotionIcon(PotionOfDivineInspiration.class),
                    "神圣启示药剂：获得天赋点",
                    () -> selectedPotion = PotionOfDivineInspiration.class
            );
            
            // 添加酿造药剂标签页
            builder.addTab("酿造药剂", Icons.get(Icons.WAND_HOLSTER));
            
            // 添加酿造药剂到第三个标签页（索引2）
            builder.addItemToTab(2,
                    createPotionIcon(InfernalBrew.class),
                    "地狱酿剂：燃烧地面",
                    () -> selectedPotion = InfernalBrew.class
            );
            builder.addItemToTab(2,
                    createPotionIcon(BlizzardBrew.class),
                    "暴雪酿剂：冰冻区域",
                    () -> selectedPotion = BlizzardBrew.class
            );
            builder.addItemToTab(2,
                    createPotionIcon(ShockingBrew.class),
                    "电击酿剂：雷电攻击",
                    () -> selectedPotion = ShockingBrew.class
            );
            builder.addItemToTab(2,
                    createPotionIcon(CausticBrew.class),
                    "腐蚀酿剂：酸性伤害",
                    () -> selectedPotion = CausticBrew.class
            );
            builder.addItemToTab(2,
                    createPotionIcon(AquaBrew.class),
                    "水之酿剂：水系效果",
                    () -> selectedPotion = AquaBrew.class
            );
            builder.addItemToTab(2,
                    createPotionIcon(UnstableBrew.class),
                    "不稳定酿剂：随机效果",
                    () -> selectedPotion = UnstableBrew.class
            );
            
            // 添加药剂精华标签页
            builder.addTab("药剂精华", Icons.get(Icons.SEED_POUCH));
            
            // 添加药剂精华到第四个标签页（索引3）
            builder.addItemToTab(3,
                    createPotionIcon(ElixirOfHoneyedHealing.class),
                    "蜂蜜治疗精华：强力治疗",
                    () -> selectedPotion = ElixirOfHoneyedHealing.class
            );
            builder.addItemToTab(3,
                    createPotionIcon(ElixirOfAquaticRejuvenation.class),
                    "水之复苏精华：长期再生",
                    () -> selectedPotion = ElixirOfAquaticRejuvenation.class
            );
            builder.addItemToTab(3,
                    createPotionIcon(ElixirOfMight.class),
                    "力量精华：永久力量",
                    () -> selectedPotion = ElixirOfMight.class
            );
            builder.addItemToTab(3,
                    createPotionIcon(ElixirOfDragonsBlood.class),
                    "龙血精华：火焰附魔",
                    () -> selectedPotion = ElixirOfDragonsBlood.class
            );
            builder.addItemToTab(3,
                    createPotionIcon(ElixirOfToxicEssence.class),
                    "毒素精华：毒性附魔",
                    () -> selectedPotion = ElixirOfToxicEssence.class
            );
            builder.addItemToTab(3,
                    createPotionIcon(ElixirOfIcyTouch.class),
                    "冰霜精华：冰冻附魔",
                    () -> selectedPotion = ElixirOfIcyTouch.class
            );
            builder.addItemToTab(3,
                    createPotionIcon(ElixirOfArcaneArmor.class),
                    "奥术护甲精华：魔法防护",
                    () -> selectedPotion = ElixirOfArcaneArmor.class
            );
            builder.addItemToTab(3,
                    createPotionIcon(ElixirOfFeatherFall.class),
                    "羽落精华：缓慢下降",
                    () -> selectedPotion = ElixirOfFeatherFall.class
            );

            // 显示窗口
            GameScene.show(builder.build());
        } else if (action.equals(AC_DRINK)) {
            if (selectedPotion == null) {
                GLog.w("请先选择要饮用的药剂！");
            } else {
                Potion potion = Reflection.newInstance(selectedPotion);
                potion.curUser = hero;
                potion.apply(hero);
            }
        } else if (action.equals(AC_GENERATE)) {
            if (selectedPotion == null) {
                GLog.w("请先选择要生成的药剂！");
            } else {
                Potion potion = Reflection.newInstance(selectedPotion);
                potion.identify();
                if (potion.collect(hero.belongings.backpack)) {
                    GLog.p("生成了 " + potion.name() + "！");
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
        actions.add(AC_DRINK);
        actions.add(AC_GENERATE);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_SELECT)) {
            return "选择药剂";
        } else if (action.equals(AC_DRINK)) {
            return "饮用药剂";
        } else if (action.equals(AC_GENERATE)) {
            return "生成药剂";
        }
        return super.actionName(action, hero);
    }

    @Override
    public String name() {
        return "药剂选择器";
    }

    @Override
    public String desc() {
        return "这是一个可以让你选择使用各种药剂的物品。";
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
    
    // 创建药剂图标的辅助方法
    private static Image createPotionIcon(Class<? extends Potion> potionClass) {
        try {
            Potion potion = Reflection.newInstance(potionClass);
            if (potion.icon != -1) {
                return new Image(Assets.Sprites.ITEM_ICONS, ItemSpriteSheet.Icons.film.get(potion.icon));
            } else {
                // 如果没有icon，使用sprite作为fallback
                return new ItemSprite(potion.image);
            }
        } catch (Exception e) {
            // 发生异常时使用默认图标
            return new ItemSprite(ItemSpriteSheet.POTION_HOLDER);
        }
    }
}
