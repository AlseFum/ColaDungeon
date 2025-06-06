package com.coladungeon.items.supply;
import com.coladungeon.items.Item;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.windows.WndIconGrid;
import com.coladungeon.sprites.ItemSprite;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.Image;
import java.util.ArrayList;

public class testitem extends Item{
    
    
    @Override
    public void execute(Hero hero,String action){
        if(action.equals(AC_TEST)){
            hero.spend(1);
            hero.sprite.operate(hero.pos);
            
            // 使用Builder模式创建图标网格窗口
            WndIconGrid.Builder builder = new WndIconGrid.Builder()
                .setTitle("选择药水")
                .setColumns(4);

            // 添加所有药水图标，每个图标都有自己的点击处理逻辑
            builder.addItem(
                new ItemSprite(ItemSpriteSheet.Icons.POTION_STRENGTH),
                "力量药水：增加力量",
                () -> {
                    GLog.i("确认使用力量药水");
                }
            ).addItem(
                new ItemSprite(ItemSpriteSheet.Icons.POTION_HEALING),
                "治疗药水：恢复生命值",
                () -> {
                    GLog.i("确认使用治疗药水");
                }
            ).addItem(
                new ItemSprite(ItemSpriteSheet.Icons.POTION_MINDVIS),
                "心灵视觉药水：显示周围生物",
                () -> {
                    GLog.i("确认使用心灵视觉药水");
                }
            ).addItem(
                new ItemSprite(ItemSpriteSheet.Icons.POTION_FROST),
                "冰冻药水：冻结目标",
                () -> {
                    GLog.i("确认使用冰冻药水");
                }
            ).addItem(
                new ItemSprite(ItemSpriteSheet.Icons.POTION_LIQFLAME),
                "液态火焰药水：燃烧目标",
                () -> {
                    GLog.i("确认使用液态火焰药水");
                }
            ).addItem(
                new ItemSprite(ItemSpriteSheet.Icons.POTION_TOXICGAS),
                "毒气药水：使目标中毒",
                () -> {
                    GLog.i("确认使用毒气药水");
                }
            ).addItem(
                new ItemSprite(ItemSpriteSheet.Icons.POTION_HASTE),
                "加速药水：提高移动速度",
                () -> {
                    GLog.i("确认使用加速药水");
                }
            ).addItem(
                new ItemSprite(ItemSpriteSheet.Icons.POTION_INVIS),
                "隐身药水：暂时隐身",
                () -> {
                    GLog.i("确认使用隐身药水");
                }
            ).addItem(
                new ItemSprite(ItemSpriteSheet.Icons.POTION_LEVITATE),
                "漂浮药水：可以漂浮",
                () -> {
                    GLog.i("确认使用漂浮药水");
                }
            ).addItem(
                new ItemSprite(ItemSpriteSheet.Icons.POTION_PARAGAS),
                "麻痹药水：使目标麻痹",
                () -> {
                    GLog.i("确认使用麻痹药水");
                }
            ).addItem(
                new ItemSprite(ItemSpriteSheet.Icons.POTION_PURITY),
                "净化药水：清除负面效果",
                () -> {
                    GLog.i("确认使用净化药水");
                }
            ).addItem(
                new ItemSprite(ItemSpriteSheet.Icons.POTION_EXP),
                "经验药水：获得经验值",
                () -> {
                    GLog.i("确认使用经验药水");
                }
            );

            // 显示窗口
            GameScene.show(builder.build());
        }
    }
    @Override
    public ArrayList<String> actions(Hero hero){
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_TEST);
        return actions;
    }
    @Override
    public String actionName(String action,Hero hero){
        if(action.equals(AC_TEST)){
            return "测试";
        }
        return super.actionName(action, hero);
    }
    public static final String AC_TEST = "TEST";
    public testitem(){
        super();
        image = ItemSpriteSheet.SOMETHING;
        stackable = true;
        defaultAction = AC_TEST;
    }
    @Override
    public String name(){
        return "测试物品";
    }
    @Override
    public String desc(){
        return "现在还不知道有什么用。";
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
        return 1;
    }
}