package com.coladungeon.items.weapon.melee.assassin;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.blobs.CorrosiveGas;
import com.coladungeon.actors.blobs.ToxicGas;
import com.coladungeon.actors.buffs.AllyBuff;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Burning;
import com.coladungeon.actors.buffs.Invisibility;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.actors.mobs.npcs.NPC;
import com.coladungeon.items.rings.RingOfAccuracy;
import com.coladungeon.items.rings.RingOfEvasion;
import com.coladungeon.items.scrolls.ScrollOfTeleportation;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.CharSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.sprites.MirrorSprite;
import com.coladungeon.ui.BuffIndicator;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class MirrorEdge extends Assassinator {
//实现这么个效果，special effect时会充能，
//然后召唤一个镜像攻击敌人
//镜像被攻击时直接扣除充能（有最大扣除值）
//然后消失
//充能越大，special effect所需阈值越低
//
    {
        image = ItemSpriteSheet.DAGGER; // Placeholder image
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 1.0f;
        tier = 3;
    }

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
        Dagger.sneakAbility(hero, target, 4, 2 + buffedLvl(), this);
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
        return "Mirror Edge";
    }

    @Override
    public String desc() {
        return "A weapon of tier 3, designed for skilled assassins. It offers a balance of power and precision.";
    }

    private float charge = 0;
    private static final float MAX_CHARGE = 10f;
    private static final float CHARGE_PER_HIT = 2f;
    private static final float MIN_CHARGE_TO_SPAWN = 3f;

    // @Override
    // public void special_effect(Char attacker, Char defender, int damage) {
    //     // 增加充能
    //     charge = Math.min(charge + CHARGE_PER_HIT, MAX_CHARGE);
        
    //     // 只有当充能达到阈值时才召唤镜像
    //     if (charge >= MIN_CHARGE_TO_SPAWN) {
    //         float chargeToUse = charge;
    //         charge = 0;  // 重置充能
            
    //         // 召唤镜像
    //         MirrorReflection mirror = new MirrorReflection();
    //         mirror.setCharge(chargeToUse);  // 将充能传递给镜像
    //         mirror.duplicate((Hero) attacker);
    //         mirror.enemy = defender;  // 设置目标
            
    //         // 在敌人周围找位置放置镜像
    //         ArrayList<Integer> respawnPoints = new ArrayList<>();
    //         for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
    //             int p = defender.pos + PathFinder.NEIGHBOURS9[i];
    //             if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
    //                 respawnPoints.add(p);
    //             }
    //         }
            
    //         if (!respawnPoints.isEmpty()) {
    //             int index = Random.index(respawnPoints);
    //             GameScene.add(mirror);
    //             ScrollOfTeleportation.appear(mirror, respawnPoints.get(index));
    //             GLog.p("Mirror appears! (Charge: " + (int)chargeToUse + ")");
    //         }
    //     }
    // }

    // //returns the number of images spawned
    // public static int spawnImages(Hero hero, Char enemy, int pos, int nImages) {

    //     ArrayList<Integer> respawnPoints = new ArrayList<>();

    //     for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
    //         int p = pos + PathFinder.NEIGHBOURS9[i];
    //         if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
    //             respawnPoints.add(p);
    //         }
    //     }

    //     int spawned = 0;
    //     while (nImages > 0 && !respawnPoints.isEmpty()) {
    //         int index = Random.index(respawnPoints);

    //         MirrorReflection mob = new MirrorReflection();
    //         mob.duplicate(hero);
    //         GameScene.add(mob);
    //         ScrollOfTeleportation.appear(mob, respawnPoints.get(index));

    //         respawnPoints.remove(index);
    //         nImages--;
    //         spawned++;
    //     }

    //     return spawned;
    // }

    // Static method to determine if a character can see a specific position
    public static boolean canSee(Char character, int pos) {
        // Check if the position is within the character's field of view
        return character.fieldOfView != null && pos >= 0 && pos < character.fieldOfView.length && character.fieldOfView[pos];
    }

    // public static class MirrorReflection extends NPC {

    //     private float charge;      // 当前充能值
    //     private static final float MAX_CHARGE = 10f;  // 最大充能值
    //     private static final float CHARGE_LOSS_CAP = 5f;  // 最大扣除值

    //     {
    //         spriteClass = MirrorSprite.class;

    //         HP = HT = 1;
    //         defenseSkill = 1;

    //         alignment = Alignment.ALLY;
    //         state = HUNTING;

    //         //before other mobs
    //         actPriority = MOB_PRIO + 1;
    //     }

    //     private Hero hero;
    //     private int heroID;
    //     public int armTier;
    //     public Char enemy;

    //     @Override
    //     protected boolean act() {
    //         if (hero == null) {
    //             hero = (Hero) Actor.findById(heroID);
    //             if (hero == null) {
    //                 die(null);
    //                 sprite.killAndErase();
    //                 return true;
    //             }
    //         }

    //         if (enemy != null) {
    //             moveToBlindSpot();
    //             if (enemy instanceof Mob) {
    //                 ((Mob)enemy).aggro(this);
    //             }
    //         }

    //         return super.act();
    //     }

    //     @Override
    //     public int defenseProc(Char enemy, int damage) {
    //         // 受到伤害时扣除充能
    //         float chargeLoss = Math.min(damage, CHARGE_LOSS_CAP);
    //         charge -= chargeLoss;
            
    //         // 如果充能耗尽，镜像消失
    //         if (charge <= 0) {
    //             die(null);
    //             sprite.killAndErase();
    //         }
            
    //         return super.defenseProc(enemy, damage);
    //     }

    //     public void setCharge(float charge) {
    //         this.charge = Math.min(charge, MAX_CHARGE);
    //     }

    //     public float getCharge() {
    //         return charge;
    //     }

    //     private void moveToBlindSpot() {
    //         if (enemy == null) {
    //             return;
    //         }

    //         // List to store potential blind spot positions
    //         ArrayList<Integer> blindSpots = new ArrayList<>();

    //         // Check surrounding positions for potential blind spots
    //         for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
    //             int potentialPos = pos + PathFinder.NEIGHBOURS8[i];

    //             // Check if the position is passable and not in the enemy's line of sight
    //             if (Dungeon.level.passable[potentialPos] && !MirrorEdge.canSee(enemy, potentialPos)) {
    //                 blindSpots.add(potentialPos);
    //             }
    //         }

    //         // If there are blind spots available, move to one of them
    //         if (!blindSpots.isEmpty()) {
    //             int targetPos = Random.element(blindSpots);
    //             move(targetPos);
    //         }
    //     }

    //     private static final String CHARGE = "charge";

    //     @Override
    //     public void storeInBundle(Bundle bundle) {
    //         super.storeInBundle(bundle);
    //         bundle.put(CHARGE, charge);
    //     }

    //     @Override
    //     public void restoreFromBundle(Bundle bundle) {
    //         super.restoreFromBundle(bundle);
    //         charge = bundle.getFloat(CHARGE);
    //     }

    //     public void duplicate(Hero hero) {
    //         this.hero = hero;
    //         heroID = this.hero.id();
    //         Buff.affect(this, MirrorInvis.class, Short.MAX_VALUE);
    //     }

    //     @Override
    //     public int damageRoll() {
    //         int damage;
    //         if (hero.belongings.weapon() != null) {
    //             damage = hero.belongings.weapon().damageRoll(this);
    //         } else {
    //             damage = hero.damageRoll(); //handles ring of force
    //         }
    //         return (damage + 1) / 2; //half hero damage, rounded up
    //     }

    //     @Override
    //     public int attackSkill(Char target) {
    //         //same base attack skill as hero, benefits from accuracy ring and weapon
    //         int attackSkill = 9 + hero.lvl;
    //         attackSkill *= RingOfAccuracy.accuracyMultiplier(hero);
    //         if (hero.belongings.attackingWeapon() != null) {
    //             attackSkill *= hero.belongings.attackingWeapon().accuracyFactor(this, target);
    //         }
    //         return attackSkill;
    //     }

    //     @Override
    //     public int defenseSkill(Char enemy) {
    //         if (hero != null) {
    //             int baseEvasion = 4 + hero.lvl;
    //             int heroEvasion = (int) ((4 + hero.lvl) * RingOfEvasion.evasionMultiplier(hero));

    //             //if the hero has more/less evasion, 50% of it is applied
    //             //includes ring of evasion boost
    //             return super.defenseSkill(enemy) * (baseEvasion + heroEvasion) / 2;
    //         } else {
    //             return 0;
    //         }
    //     }

    //     @Override
    //     public float attackDelay() {
    //         return hero.attackDelay(); //handles ring of furor
    //     }

    //     @Override
    //     protected boolean canAttack(Char enemy) {
    //         return super.canAttack(enemy) || (hero.belongings.weapon() != null && hero.belongings.weapon().canReach(this, enemy.pos));
    //     }

    //     @Override
    //     public int drRoll() {
    //         int dr = super.drRoll();
    //         if (hero != null && hero.belongings.weapon() != null) {
    //             return dr + Random.NormalIntRange(0, hero.belongings.weapon().defenseFactor(this) / 2);
    //         } else {
    //             return dr;
    //         }
    //     }

    //     @Override
    //     public int attackProc(Char enemy, int damage) {
    //         damage = super.attackProc(enemy, damage);

    //         MirrorInvis buff = buff(MirrorInvis.class);
    //         if (buff != null) {
    //             buff.detach();
    //         }

    //         if (enemy instanceof Mob) {
    //             ((Mob) enemy).aggro(this);
    //         }
    //         if (hero.belongings.weapon() != null) {
    //             damage = hero.belongings.weapon().proc(this, enemy, damage);
    //             if (enemy!=null && !enemy.isAlive() && enemy == Dungeon.hero) {
    //                 Dungeon.fail(this);
    //                 GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", name())));
    //             }
    //             return damage;
    //         } else {
    //             return damage;
    //         }
    //     }

    //     @Override
    //     public CharSprite sprite() {
    //         CharSprite s = super.sprite();

    //         hero = (Hero) Actor.findById(heroID);
    //         if (hero != null) {
    //             armTier = hero.tier();
    //         } else {
    //             armTier = 1;
    //         }
    //         ((MirrorSprite) s).updateArmor(armTier);
    //         return s;
    //     }

    //     {
    //         immunities.add(ToxicGas.class);
    //         immunities.add(CorrosiveGas.class);
    //         immunities.add(Burning.class);
    //         immunities.add(AllyBuff.class);
    //     }

    //     public static class MirrorInvis extends Invisibility {

    //         {
    //             announced = false;
    //         }

    //         @Override
    //         public int icon() {
    //             return BuffIndicator.NONE;
    //         }
    //     }

    //     public void setEnemy(Char enemy) {
    //         this.enemy = enemy;
    //     }

    // }
}
