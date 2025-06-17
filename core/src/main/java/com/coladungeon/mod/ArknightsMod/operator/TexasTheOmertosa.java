package com.coladungeon.mod.ArknightsMod.operator;

import java.util.ArrayList;
import java.util.Collections;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.blobs.Blob;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Paralysis;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.BlobEmitter;
import com.coladungeon.effects.particles.BlastParticle;
import com.coladungeon.effects.particles.SparkParticle;
import com.coladungeon.mod.ArknightsMod.NPC.Dummy;
import com.coladungeon.mod.ArknightsMod.Operator;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.ExecutorWeapon;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteManager;
import com.coladungeon.tiles.DungeonTilemap;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
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
        return "企鹅物流的精英干员，代号\"缄默德克萨斯\"。\n\n"
                + "擅长使用双剑进行战斗，能够释放强大的范围攻击。\n"
                + "通过剑雨技能对敌人造成范围伤害和控制效果。\n\n"
                + "特性：\n"
                + "- 攻击时对周围敌人造成法术伤害\n"
                + "- 可以释放剑雨技能造成范围伤害和麻痹效果\n"
                + "- 使用剑雨后失去双持效果";
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

        static {
            ItemSpriteManager.registerTexture("cola/omertosaweapon.png", 64).label("omertosa");
        }

        {
            image = ItemSpriteManager.ByName("omertosa");
        }

        public static final String AC_RAIN = "RAIN";
        public static final String AC_AMBUSH = "AMBUSH";
        public boolean dualwielding = true;

        private void createAmbushEffect(Char owner, Char target) {
            // 在角色周围八格创建剑气效果
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue; // 跳过中心位置
                    int pos = owner.pos + i + j * Dungeon.level.width();
                    if (Dungeon.level.insideMap(pos)) {
                        Emitter swordEmitter = new Emitter();
                        swordEmitter.pos(DungeonTilemap.tileCenterToWorld(pos));
                        swordEmitter.burst(new Emitter.Factory() {
                            @Override
                            public void emit(Emitter emitter, int index, float x, float y) {
                                SwordParticle p = (SwordParticle)emitter.recycle(SwordParticle.class);
                                p.reset(x, y);
                            }

                            @Override
                            public boolean lightMode() {
                                return true;
                            }
                        }, 5);
                        owner.sprite.parent.add(swordEmitter);
                    }
                }
            }

            // 在目标位置创建更大的效果
            Emitter targetEmitter = new Emitter();
            targetEmitter.pos(target.sprite.center());
            targetEmitter.burst(new Emitter.Factory() {
                @Override
                public void emit(Emitter emitter, int index, float x, float y) {
                    TargetParticle p = (TargetParticle)emitter.recycle(TargetParticle.class);
                    p.reset(x, y);
                }

                @Override
                public boolean lightMode() {
                    return true;
                }
            }, 20);
            target.sprite.parent.add(targetEmitter);

            // 添加爆炸效果
            Emitter blastEmitter = new Emitter();
            blastEmitter.pos(target.sprite.center());
            blastEmitter.burst(BlastParticle.FACTORY, 8);
            blastEmitter.burst(SparkParticle.FACTORY, 15);
            target.sprite.parent.add(blastEmitter);
        }

        private void createRainEffect(Char target) {
            // 创建剑雨效果
            Emitter rainEmitter = new Emitter();
            rainEmitter.pos(target.sprite.center().x, target.sprite.y - 20);
            rainEmitter.burst(new Emitter.Factory() {
                @Override
                public void emit(Emitter emitter, int index, float x, float y) {
                    RainSwordParticle p = (RainSwordParticle)emitter.recycle(RainSwordParticle.class);
                    p.reset(x, y);
                }

                @Override
                public boolean lightMode() {
                    return true;
                }
            }, 3);
            target.sprite.parent.add(rainEmitter);

            // 添加命中效果
            Emitter hitEmitter = new Emitter();
            hitEmitter.pos(target.sprite.center());
            hitEmitter.burst(BlastParticle.FACTORY, 4);
            hitEmitter.burst(SparkParticle.FACTORY, 8);
            target.sprite.parent.add(hitEmitter);
        }

        public static class SwordParticle extends PixelParticle {
            public SwordParticle() {
                super();
                color(0x88CCFF);
                lifespan = 0.6f;
                size(3);
                speed.set(Random.Float(-10, 10), Random.Float(-10, 10));
            }

            public void reset(float x, float y) {
                revive();
                this.x = x;
                this.y = y;
                left = lifespan;
                speed.set(Random.Float(-10, 10), Random.Float(-10, 10));
            }

            @Override
            public void update() {
                super.update();
                am = left / lifespan;
                size(3 * (1 - left / lifespan));
            }
        }

        public static class TargetParticle extends PixelParticle {
            public TargetParticle() {
                super();
                color(0x88CCFF);
                lifespan = 0.8f;
                size(6);
                speed.set(Random.Float(-15, 15), Random.Float(-15, 15));
            }

            public void reset(float x, float y) {
                revive();
                this.x = x;
                this.y = y;
                left = lifespan;
                speed.set(Random.Float(-15, 15), Random.Float(-15, 15));
            }

            @Override
            public void update() {
                super.update();
                am = left / lifespan;
                size(6 * (1 - left / lifespan));
            }
        }

        public static class RainSwordParticle extends PixelParticle {
            public RainSwordParticle() {
                super();
                color(0x88CCFF);
                lifespan = 0.5f;
                size(2);
                speed.set(0, 60);
            }

            public void reset(float x, float y) {
                revive();
                this.x = x;
                this.y = y;
                left = lifespan;
                speed.set(0, 60);
            }

            @Override
            public void update() {
                super.update();
                am = left / lifespan;
                size(2 + 4 * (1 - left / lifespan));
            }
        }

        @Override
        public ArrayList<String> actions(Hero hero) {
            ArrayList<String> actions = super.actions(hero);
            actions.add(AC_RAIN);
            actions.add(AC_AMBUSH);
            return actions;
        }

        @Override
        public String actionName(String action, Hero hero) {
            if (action.equals(AC_RAIN)) {
                return "剑雨";
            } else if (action.equals(AC_AMBUSH)) {
                return "伏击";
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
                                if (ch.alignment == Char.Alignment.ENEMY
                                        && Dungeon.level.distance(cell, ch.pos) <= 1) {
                                    targets.add(ch);
                                }
                            }
                            Collections.shuffle(targets);
                            int count = Math.min(3, targets.size());
                            for (int i = 0; i < count; i++) {
                                Char target = targets.get(i);
                                int damage = (int) (hero.damageRoll() * 2.0f);
                                target.damage(damage, hero);
                                Buff.affect(target, Paralysis.class, 2f);
                                createRainEffect(target);
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
            } else if (action.equals(AC_AMBUSH)) {
                // 立即触发伏击效果
                ArrayList<Char> targets = new ArrayList<>();
                for (Char ch : Dungeon.level.mobs) {
                    if (ch.alignment == Char.Alignment.ENEMY && Dungeon.level.distance(hero.pos, ch.pos) <= 2) {
                        targets.add(ch);
                    }
                }
                for (Char target : targets) {
                    int spellDamage = (int) (hero.damageRoll() * 3.0f);
                    target.damage(spellDamage, hero);
                    createAmbushEffect(hero, target);
                }
                hero.spendAndNext(1f);
            }
        }

        @Override
        public String name() {
            return "蓝莓与黑巧";
        }

        @Override
        public int SE(Char owner, Char enemy, int damage) {
            // 对周围目标造成300%攻击力的法术伤害
            int spellDamage = (int) (owner.damageRoll() * 3.0f);
            ArrayList<Char> targets = new ArrayList<>();
            for (Char ch : Dungeon.level.mobs) {
                if (ch != enemy && ch.alignment == Char.Alignment.ENEMY && Dungeon.level.distance(owner.pos, ch.pos) <= 2) {
                    targets.add(ch);
                }
            }
            for (Char target : targets) {
                target.damage(spellDamage, owner);
                createAmbushEffect(owner, target);
            }
            return damage;
        }

        @Override
        public String desc() {
            String desc = "对周围2格范围内的敌人造成300%攻击力的法术伤害。\n\n";
            desc += "特殊技能：\n";
            desc += "- 剑雨：在目标位置生成3x3的剑雨区域，随机对其中至多3个敌人造成200%伤害并麻痹2回合。使用后失去双持效果。\n";
            desc += "- 伏击：立即对周围2格范围内的所有敌人造成300%攻击力的法术伤害。";
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

        @Override
        public void use(BlobEmitter emitter) {
            super.use(emitter);
            emitter.pour(RainParticle.FACTORY, 0.2f);
        }

        public static class RainParticle extends PixelParticle {
            public static final Emitter.Factory FACTORY = new Emitter.Factory() {
                @Override
                public void emit(Emitter emitter, int index, float x, float y) {
                    RainParticle p = (RainParticle)emitter.recycle(RainParticle.class);
                    p.reset(x, y);
                }

                @Override
                public boolean lightMode() {
                    return true;
                }
            };

            public RainParticle() {
                super();
                color(0x88CCFF);
                lifespan = 0.8f;
                size(3);
                speed.set(0, 30);
            }

            public void reset(float x, float y) {
                revive();
                this.x = x;
                this.y = y - 20; // 从上方开始下落
                left = lifespan;
                speed.set(0, 30);
            }

            @Override
            public void update() {
                super.update();
                am = left / lifespan;
                size(3 * (1 - left / lifespan));
            }
        }
    }
}
