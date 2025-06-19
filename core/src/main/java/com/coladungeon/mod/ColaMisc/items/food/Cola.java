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

    // 效果类型，包含对Hero的副作用lambda
    public enum EffectType {
        // 增加金币
        GOLD(hero -> {
            int gold = Random.IntRange(10, 50);
            com.coladungeon.Dungeon.gold += gold;
            GLog.p("你突然发现口袋里多了" + gold + "金币！");
        }),
        // 立即治疗少量生命
        HEAL(hero -> {
            int heal = Random.IntRange(5, 15);
            hero.HP = Math.min(hero.HT, hero.HP + heal);
            GLog.p("你感到一阵清凉，恢复了" + heal + "点生命值。");
        }),
        // 触发一次爆炸（对周围造成伤害）
        EXPLODE(hero -> {
            GLog.p("可乐在你肚子里爆炸了！你受到了一些伤害。");
            hero.damage(Random.IntRange(5, 20), null);
        }),
        // 传送到随机位置
        TELEPORT(hero -> {
            GLog.p("你突然出现在了另一个地方！");
            hero.pos = com.coladungeon.Dungeon.level.randomRespawnCell(hero);
        }),
        // 变身为巨人
        GIANT(hero -> {
            GLog.p("你突然变得巨大无比，感觉力量充盈！");
            hero.sprite.scale.set(2f, 2f); // 假设sprite有scale属性
        }),
        // 变身为小矮人
        TINY(hero -> {
            GLog.p("你突然变得小巧玲珑，行动更加灵活！");
            hero.sprite.scale.set(0.5f, 0.5f);
        }),
        // 全身发光
        GLOW(hero -> {
            GLog.p("你的身体开始发出耀眼的光芒，照亮了四周！");
            hero.sprite.add(com.coladungeon.sprites.CharSprite.State.ILLUMINATED);
        }),
        // 变色
        COLORFUL(hero -> {
            GLog.p("你的皮肤变成了彩虹色，十分滑稽！");
            hero.sprite.tint(0xFF00FF);
        }),
        // 发出奇怪的声音
        WEIRD_SOUND(hero -> {
            GLog.p("你忍不住发出一连串奇怪的叫声，吓到了周围的生物！");
            com.watabou.noosa.audio.Sample.INSTANCE.play(com.coladungeon.Assets.Sounds.CHARMS); // 假设有CHARMS音效
        });

        public interface Effect {
            void apply(Hero hero);
        }
        private final Effect effect;
        EffectType(Effect effect) {
            this.effect = effect;
        }
        public void apply(Hero hero) {
            effect.apply(hero);
        }
    }

    // 当前可乐中的效果列表
    private ArrayList<EffectType> effects = new ArrayList<>();
    private static final int MAX_EFFECTS = 3;

    @Override
    public String name() {
        return "可乐";
    }
    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ADD);
        actions.add(AC_DRINK);
        return actions;
    }
    @Override
    public String desc() {
        return "一罐神奇的可乐。";
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_ADD)) {
            return "添加材料";
        } else if (action.equals(AC_DRINK)) {
            return "喝下";
        }
        return super.actionName(action, hero);
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_ADD)) {
            if (effects.size() < MAX_EFFECTS) {
                // 随机添加一个效果
                EffectType newEffect = Random.element(EffectType.values());
                effects.add(newEffect);
                GLog.p("你向可乐中添加了材料");
                updateQuickslot();
            } else {
                GLog.w("可乐快要溢出来了");
            }
        } else if (action.equals(AC_DRINK)) {
            // 恢复饥饿值
            hero.buff(Hunger.class).satisfy(energy);
            
            // 应用所有效果
            for (EffectType effect : effects) {
                effect.apply(hero);
            }
            
            // 清空效果列表
            effects.clear();
            
            // 播放音效
            Sample.INSTANCE.play(Assets.Sounds.DRINK);
        }
    }
} 