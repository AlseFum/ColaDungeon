package com.coladungeon.mod.ArknightsMod.operator;

import java.util.ArrayList;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Paralysis;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.mod.ArknightsMod.Operator;
import com.coladungeon.mod.ArknightsMod.category.OperatorConst.Branch;
import com.coladungeon.mod.ArknightsMod.category.OperatorConst.Rarity;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.ExecutorWeapon;
import com.coladungeon.sprites.ItemSpriteManager;
import com.coladungeon.sprites.MobSprite;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Phantom extends Operator {

    {

        spriteClass = PhantomSprite.class;

        HP = HT = 100;
        defenseSkill = 30;

        alignment = Alignment.ALLY;

        // 设置干员特有属性
        branch = Branch.Core;
        rarity = Rarity.S6;
        cost = 19;
    }

    @Override
    public String name() {
        return "Phantom";
    }

    @Override
    public String description() {
        return "傀影。";
    }

    @Override
    public int attackSkill(Char target) {
        return 35;
    }

    @Override
    public int damageRoll() {
        int damage = Random.NormalIntRange(20, 40);
        yemutuxi buff = buff(yemutuxi.class);
        if (buff != null) {
            damage = (int) (damage * (1 + 0.2f * buff.getStacks()));
        }
        return damage;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(5, 15);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        yemutuxi buff = buff(yemutuxi.class);
        if (buff != null && buff.stacks > 0) {
            buff.stacks--;
            if (buff.stacks <= 0) {
                buff.detach();
            }
        }
        return super.attackProc(enemy, damage);
    }
    
    public static class PhantomWeapon extends ExecutorWeapon {
        static {
            ItemSpriteManager.registerTexture("cola/phantomknife.png", 32).label("phantomknife");
        }
        {
            image = ItemSpriteManager.ByName("phantomknife");
            defaultAction=AC_WAVE;
        }

        public static final String AC_WAVE = "WAVE";

        @Override
        public ArrayList<String> actions(Hero hero) {
            ArrayList<String> actions = super.actions(hero);
            actions.add(AC_WAVE);
            return actions;
        }

        @Override
        public String actionName(String action, Hero hero) {
            if (action.equals(AC_WAVE)) {
                return "吟唱";
            }
            return super.actionName(action, hero);
        }

        @Override
        public void execute(Hero hero, String action) {
            super.execute(hero, action);
            if (action.equals(AC_WAVE)) {
                yemutuxi buff = hero.buff(yemutuxi.class);
                if (buff != null && buff.stacks > 0) {
                    int stacks = Math.min(buff.stacks,20);
                    GLog.i("PhantomWeapon", "吟唱：" + stacks);
                    // 范围随层数增加，每层增加1格，最小2格
                    int range = Math.max(2, 2 + stacks - 1);
                    // 先收集所有目标
                    ArrayList<Char> targets = new ArrayList<>();
                    for (Char ch : Dungeon.level.mobs) {
                        if (ch.alignment == Char.Alignment.ENEMY && 
                            Dungeon.level.distance(hero.pos, ch.pos) <= range) {
                            targets.add(ch);
                        }
                    }
                    
                    // 然后处理每个目标
                    for (Char ch : targets) {
                        int damage = (int)(hero.damageRoll() * (1 + 0.2f * stacks));
                        ch.damage(damage, hero);
                        Buff.affect(ch, Paralysis.class, 2f);
                        
                        // 计算推开方向
                        int dx = ch.pos % Dungeon.level.width() - hero.pos % Dungeon.level.width();
                        int dy = ch.pos / Dungeon.level.width() - hero.pos / Dungeon.level.width();
                        
                        // 推开敌人
                        if (dx != 0 || dy != 0) {
                            int newPos = ch.pos;
                            if (Math.abs(dx) > Math.abs(dy)) {
                                newPos = ch.pos + (dx > 0 ? 1 : -1);
                            } else {
                                newPos = ch.pos + (dy > 0 ? Dungeon.level.width() : -Dungeon.level.width());
                            }
                            
                            // 确保新位置是有效的
                            if (Dungeon.level.passable[newPos] && !Dungeon.level.solid[newPos]) {
                                ch.move(newPos);
                            }
                        }
                    }
                    // 清空层数
                    buff.stacks -= stacks;
                    if(buff.stacks<=0){
                        buff.detach();
                    }
                    hero.spendAndNext(1f);
                } else {
                    GLog.w("没有夜幕突袭层数！");
                }
            }
        }

        @Override
        public String name() {
            return "演出双匕";
        }
        public int maxStacks(){
            return 8+tier+Dungeon.hero.STR()-STRReq();
        };
        @Override
        public int SE(Char owner, Char enemy, int damage) {
            yemutuxi buff = Buff.affect(owner, yemutuxi.class);
            buff.stacks += 3;
            boolean debug=true;
            if(buff.stacks>maxStacks()&&!debug){
                buff.stacks = maxStacks();
            }
            updateQuickslot();
            return damage;
        }

        @Override
        public int damageRoll(Char owner) {
            int damage = super.damageRoll(owner);
            yemutuxi buff = owner.buff(yemutuxi.class);
            if (buff != null) {
                damage = (int) (damage * (1 + 0.2f * buff.getStacks()));
                buff.stacks -=1;
                updateQuickslot();
            }
            return damage;
        }

        @Override
        public String desc() {
            String desc = "伏击时获得夜幕突袭。\n\n";
            yemutuxi buff = Dungeon.hero.buff(yemutuxi.class);
            int stacks = buff != null ? buff.getStacks() : 0;
            desc += "当前夜幕突袭层数：" + stacks + "\n\n";
            desc += "特殊技能：\n";
            desc += "- 波动：消耗所有夜幕突袭层数，对周围敌人造成伤害并麻痹2回合。\n";
            desc += "  范围随层数增加（每层+1格，最小2格），伤害随层数提升（每层+20%）。";
            return desc;
        }
        @Override
        public String status(){
            yemutuxi buff = Dungeon.hero.buff(yemutuxi.class);
            int stacks = buff != null ? buff.getStacks() : 0;
            return  ""+stacks;
        }
    }

    public static class yemutuxi extends Buff {

        public int stacks = 0;
        {
            announced=true;
        }
        @Override
        public boolean act() {
            spend(TICK);
            return true;
        }

        public void addStack() {
            stacks++;
        }

        public int getStacks() {
            return stacks;
        }

        @Override
        public String toString() {
            return "nightambush " + stacks;
        }

        @Override
        public String desc() {
            return "每层增加20%攻击力";
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put("stacks", stacks);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            stacks = bundle.getInt("stacks");
        }
    }
    public static class PhantomSprite extends MobSprite{
        public PhantomSprite() {
		super();

		texture( "cola/phantom.png" );

		TextureFilm frames = new TextureFilm( texture, 32, 32 );
        scale.set(0.5f);
		idle = new Animation( 5, true );
		idle.frames( frames, 0, 1, 2  );

		run = new Animation( 15, true );
		run.frames( frames, 3,4,5 );

		attack = new Animation( 12, false );
		attack.frames( frames,3,4,5);

		die = new Animation( 12, false );
		die.frames( frames, 6,7 );

		play( idle );
	}

	@Override
	public int blood() {
		return 0xFFFFEA80;
	}
    }
}
