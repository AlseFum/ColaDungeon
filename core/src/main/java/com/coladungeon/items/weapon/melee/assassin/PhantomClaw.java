package com.coladungeon.items.weapon.melee.assassin;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.blobs.Blob;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.BlobEmitter;
import com.coladungeon.effects.Speck;
import com.coladungeon.messages.Messages;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class PhantomClaw extends Assassinator {

    {
        image = ItemSpriteSheet.ASSASSINS_BLADE; // Placeholder image
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 0.8f;
        tier = 5;
    }
    public int charge = 0;
    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }
    @Override
    public boolean useTargeting() {
        return false;
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        Dagger.sneakAbility(hero, target, 5, 2 + buffedLvl(), this);
    }

    @Override
    public String abilityInfo() {
        if (levelKnown) {
            return Messages.get(this, "ability_desc", 2 + buffedLvl());
        } else {
            return Messages.get(this, "typical_ability_desc", 2);
        }
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(2 + level);
    }

    @Override
    public String name() {
        return "Phantom Claw";
    }
    // @Override
    // public String status() {
    //     return "~"+charge;
    // }
    @Override
    public String desc() {
        return "A weapon of tier 5, crafted for master assassins. It delivers unparalleled lethality and precision.";
    }

    // @Override
    // public void special_effect(Char attacker, Char defender, int damage) {
    //     super.special_effect(attacker, defender, damage);
    //     // 1. 在defender脚下生成PhantomBlob
    //     PhantomBlob blob = Blob.seed(defender.pos, 1, PhantomBlob.class);

    //     // 2. 给所有mob添加PhantomMark buff
    //     for (Char mob : Dungeon.level.mobs) {
    //         if (mob.buff(PhantomMark.class) == null) {
    //             Buff.affect(mob, PhantomMark.class)
    //             .setBlob(blob).damage= 12;
    //         }
    //     }
    // }

    // // ========== 内部类：PhantomBlob ==========
    // public static class PhantomBlob extends Blob {
    //     public int charge = 0;
    //     public int size = 1;
    //     public java.util.Set<Char> markedMobs = new java.util.HashSet<>();

    //     public PhantomBlob() {
    //         // 必须有无参构造函数供反射用
    //     }

    //     public void onMobDeath(Char mob) {
    //         charge++;
    //         size++;
    //         // 可以在这里增强伏击伤害或效果
    //     }

    //     @Override
    //     public void use(BlobEmitter emitter) {
    //         super.use(emitter);
    //         emitter.pour(Speck.factory(Speck.SMOKE), 3f);
    //     }

    //     @Override
    //     public String tileDesc() {
    //         return Messages.get(this, "desc");
    //     }
    // }

    // // ========== 内部类：PhantomMark ==========
    // public static class PhantomMark extends Buff {
    //     private PhantomBlob blob;
    //     private int tick = 0;
    //     public int damage=5;
    //     public PhantomMark setBlob(PhantomBlob blob) {
    //         this.blob = blob;
    //         blob.markedMobs.add(target);
    //         return this;
    //     }

    //     @Override
    //     public boolean act() {
    //         tick++;
    //         if (Random.Float() < 0.2f || tick % 5 == 0) { // 不定时伏击
    //             int _dam = damage+blob.size * 3;
    //             // Use the weapon as the source of damage
    //             PhantomClaw weapon = Dungeon.hero.belongings.weapon instanceof PhantomClaw ? 
    //                 (PhantomClaw)Dungeon.hero.belongings.weapon : null;
    //             if (weapon != null) {
    //                 target.damage(_dam, weapon);
    //             } else {
    //                 target.damage(_dam, Dungeon.hero);
    //             }
    //             com.coladungeon.utils.GLog.n("Phantom ambush!");
    //         }
    //         spend(TICK);
    //         return true;
    //     }

    //     @Override
    //     public void detach() {
    //         super.detach();
    //         if (!target.isAlive() && blob != null) {
    //             blob.onMobDeath(target);
    //         }
    //     }
    // }
}
