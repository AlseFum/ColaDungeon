package com.coladungeon.items.weapon.melee.assassin;

import com.coladungeon.Assets;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.messages.Messages;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.actors.Char;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Invisibility;
import com.coladungeon.utils.GLog;

public class PhantomClaw extends Assassinator {

    {
        image = ItemSpriteSheet.ASSASSINS_BLADE; // Placeholder image
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 0.8f;
        tier = 5;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

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

    @Override
    public String desc() {
        return "A weapon of tier 5, crafted for master assassins. It delivers unparalleled lethality and precision.";
    }
    @Override
    public void special_effect(Char attacker, Char defender, int damage) {
        //给敌人一个buff，回合内敌人视野内其他敌人如果没看到此敌人，会受到敌人的影子攻击，并附加上这个buff
        //敌人死亡则buff计数返回武器计数，这个计数越强，
        //阴影buff蔓延性越强
        super.special_effect(attacker, defender, damage);
        
        // Update defender's field of view
        if (defender.fieldOfView == null || defender.fieldOfView.length != Dungeon.level.length()) {
            defender.fieldOfView = new boolean[Dungeon.level.length()];
            Dungeon.level.updateFieldOfView(defender, defender.fieldOfView);
        }
        
        // Count characters not in defender's field of view
        int invisibleCount = 0;
        for (Char ch : Dungeon.level.mobs) {
            if (ch != defender && !defender.fieldOfView[ch.pos]) {
                invisibleCount++;
            }
        }
        
        if (invisibleCount > 0) {
            // Apply invisibility to attacker
            Buff.affect(attacker, Invisibility.class, invisibleCount * 3f);
            
            // Deal damage to characters not in defender's field of view
            for (Char ch : Dungeon.level.mobs) {
                if (ch != defender && !defender.fieldOfView[ch.pos]) {
                    int shadowDamage = Math.round(damage * 0.4f);
                    ch.damage(shadowDamage, attacker);
                    GLog.n("Shadow attack!");
                }
            }
        } else {
            GLog.n("No shadows to attack!");
        }
    }
} 