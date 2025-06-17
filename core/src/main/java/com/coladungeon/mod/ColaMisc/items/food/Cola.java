package com.coladungeon.mod.ColaMisc.items.food;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.actors.buffs.Hunger;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.food.Food;
import com.coladungeon.sprites.ItemSpriteManager;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Cola extends Food {
    static {
        ItemSpriteManager.registerTexture("cola/cola.png",64).label("cola");
    }
    {
        image = ItemSpriteManager.ByName("cola");
        energy = Hunger.HUNGRY/4f;
        defaultAction = AC_ADD;
    }

    private static final String AC_ADD = "ADD";
    private static final String AC_DRINK = "DRINK";

    // 效果类型
    public enum EffectType {
        SPEED_UP("速度提升", 1.5f),
        ATTACK_UP("攻击力提升", 1.3f),
        DEFENSE_UP("防御力提升", 1.2f),
        HEAL("生命恢复", 10f),
        REGENERATION("生命再生", 5f),
        STRENGTH("力量提升", 1.4f);

        public final String name;
        public final float value;

        EffectType(String name, float value) {
            this.name = name;
            this.value = value;
        }
    }

    // 当前可乐中的效果列表
    private ArrayList<EffectType> effects = new ArrayList<>();
    private static final int MAX_EFFECTS = 3;

    @Override
    public String name() {
        return "魔法可乐";
    }

    @Override
    public String desc() {
        return "一罐神奇的可乐，可以通过添加材料来获得不同的效果。\n\n" +
               "当前效果：\n" +
               "- 速度提升：提升移动速度\n" +
               "- 攻击力提升：增加伤害\n" +
               "- 防御力提升：减少受到的伤害\n" +
               "- 生命恢复：立即恢复生命值\n" +
               "- 生命再生：持续恢复生命值\n" +
               "- 力量提升：增加攻击力\n\n" +
               "最多可以添加3种效果，饮用后会消耗所有效果。";
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ADD);
        actions.add(AC_DRINK);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_ADD)) {
            if (effects.size() < MAX_EFFECTS) {
                // 随机添加一个效果
                EffectType newEffect = Random.element(EffectType.values());
                effects.add(newEffect);
                GLog.p("你向可乐中添加了材料，获得了" + newEffect.name + "效果！");
                updateQuickslot();
            } else {
                GLog.w("可乐已经添加了太多材料，无法继续添加！");
            }
        } else if (action.equals(AC_DRINK)) {
            // 恢复饥饿值
            hero.buff(Hunger.class).satisfy(energy);
            
            // 应用所有效果
            for (EffectType effect : effects) {
                switch (effect) {
                    case SPEED_UP:
                        // TODO: 实现速度提升效果
                        break;
                    case ATTACK_UP:
                        // TODO: 实现攻击力提升效果
                        break;
                    case DEFENSE_UP:
                        // TODO: 实现防御力提升效果
                        break;
                    case HEAL:
                        hero.HP = Math.min(hero.HP + (int)effect.value, hero.HT);
                        break;
                    case REGENERATION:
                        // TODO: 实现生命再生效果
                        break;
                    case STRENGTH:
                        // TODO: 实现力量提升效果
                        break;
                }
            }
            
            // 清空效果列表
            effects.clear();
            
            // 播放音效
            Sample.INSTANCE.play(Assets.Sounds.DRINK);
            
            // 显示消息
            GLog.p("你喝下了可乐，感觉精神焕发！");
        }
    }
} 