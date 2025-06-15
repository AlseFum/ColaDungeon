package com.coladungeon.mod.ArknightsMod.operator;

import java.util.ArrayList;
import java.util.Collections;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.blobs.Blob;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Paralysis;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.mod.ArknightsMod.NPC.Dummy;
import com.coladungeon.mod.ArknightsMod.Operator;
import com.coladungeon.mod.ArknightsMod.items.weapon.ExecutorWeapon;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteManager;
import com.watabou.utils.Random;

public class TexasTheOmertosa extends Operator {

    {
        spriteClass = Dummy.DummySprite.class;

        HP = HT = 1000;
        defenseSkill = 30;

        alignment = Alignment.ALLY;
        cost = 19;
    }

    @Override
    public String name() {
        return "缄默德克萨斯";
    }

    @Override
    public String description() {
        return "企鹅物流的精英干员，代号\"缄默德克萨斯\"。\n\n" +
               "擅长使用双剑进行战斗，能够释放强大的范围攻击。\n" +
               "通过剑雨技能对敌人造成范围伤害和控制效果。\n\n" +
               "特性：\n" +
               "- 攻击时对周围敌人造成法术伤害\n" +
               "- 可以释放剑雨技能造成范围伤害和麻痹效果\n" +
               "- 使用剑雨后失去双持效果";
    }

    @Override
    public int attackSkill(Char target) {
        return 35;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(20, 40);
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(5, 15);
    }

    public static class TexasWeapon extends ExecutorWeapon {
        {
            image = ItemSpriteManager.registerTexture("cola/redknife.png", 32).label("redknife").getByName("redknife");
        }

        public static final String AC_RAIN = "RAIN";
        public boolean dualwielding=true;
        @Override
        public ArrayList<String> actions(Hero hero) {
            ArrayList<String> actions = super.actions(hero);
            actions.add(AC_RAIN);
            return actions;
        }

        @Override
        public String actionName(String action, Hero hero) {
            if (action.equals(AC_RAIN)) {
                return "剑雨";
            }
            return super.actionName(action, hero);
        }

        @Override
        public void execute(Hero hero, String action) {
            super.execute(hero, action);
            if (action.equals(AC_RAIN)) {
                GameScene.selectCell(new CellSelector.Listener() {
                    @Override
                    public void onSelect(Integer cell) {
                        if (cell != null) {
                            // 在目标位置生成3x3的blob
                            for (int i = -1; i <= 1; i++) {
                                for (int j = -1; j <= 1; j++) {
                                    int pos = cell + i + j * Dungeon.level.width();
                                    if (Dungeon.level.insideMap(pos)) {
                                        GameScene.add(Blob.seed(pos, 2, RainBlob.class));
                                    }
                                }
                            }
                            // 随机选择至多3个敌人造成伤害和麻痹
                            ArrayList<Char> targets = new ArrayList<>();
                            for (Char ch : Dungeon.level.mobs) {
                                if (ch.alignment == Char.Alignment.ENEMY && 
                                    Dungeon.level.distance(cell, ch.pos) <= 1) {
                                    targets.add(ch);
                                }
                            }
                            Collections.shuffle(targets);
                            int count = Math.min(3, targets.size());
                            for (int i = 0; i < count; i++) {
                                Char target = targets.get(i);
                                int damage = (int)(hero.damageRoll() * 2.0f);
                                target.damage(damage, hero);
                                Buff.affect(target, Paralysis.class, 2f);
                            }
                            dualwielding = false;
                            hero.spendAndNext(1f);
                        }
                    }

                    @Override
                    public String prompt() {
                        return "选择剑雨目标位置";
                    }
                });
            }
        }

        @Override
        public String name() {
            return "蓝莓与黑巧";
        }

        @Override
        public int SE(Char owner, Char enemy, int damage) {
            // 对周围目标造成300%攻击力的法术伤害
            int spellDamage = (int)(owner.damageRoll() * 3.0f);
            for (Char ch : Dungeon.level.mobs) {
                if (ch != enemy && ch.alignment == Char.Alignment.ENEMY && Dungeon.level.distance(owner.pos, ch.pos) <= 2) {
                    ch.damage(spellDamage, owner);
                }
            }
            return damage;
        }

        @Override
        public String desc() {
            String desc = "对周围2格范围内的敌人造成300%攻击力的法术伤害。\n\n";
            desc += "特殊技能：\n";
            desc += "- 剑雨：在目标位置生成3x3的剑雨区域，随机对其中至多3个敌人造成200%伤害并麻痹2回合。使用后失去双持效果。";
            return desc;
        }
    }

    public static class RainBlob extends Blob {
        {
            alwaysVisible = true;
        }

        @Override
        protected void evolve() {
            int[] cur = this.cur;
            int[] off = this.off;
            int volume = this.volume;
            
            for (int i = 0; i < Dungeon.level.length(); i++) {
                if (cur[i] > 0) {
                    off[i] = cur[i] - 1;
                    volume += off[i];
                } else {
                    off[i] = 0;
                }
            }
            
            this.volume = volume;
        }
    }
}
