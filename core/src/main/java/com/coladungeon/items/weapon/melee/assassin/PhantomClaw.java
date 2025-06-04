package com.coladungeon.items.weapon.melee.assassin;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Invisibility;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.messages.Messages;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;

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
    public String status() {
        return "~"+charge;
    }
    @Override
    public String desc() {
        return "A weapon of tier 5, crafted for master assassins. It delivers unparalleled lethality and precision.";
    }

    @Override
    public void special_effect(Char attacker, Char defender, int damage) {
        super.special_effect(attacker, defender, damage);
        //未来应实现有幻影进行攻击的视觉效果
        // Update defender's field of view
        if (defender.fieldOfView == null || defender.fieldOfView.length != Dungeon.level.length()) {
            defender.fieldOfView = new boolean[Dungeon.level.length()];
            Dungeon.level.updateFieldOfView(defender, defender.fieldOfView);
        }
        
        // 创建一个临时列表来存储需要处理的怪物
        ArrayList<Char> targets = new ArrayList<>();
        
        // 先收集所有目标
        for (Char ch : Dungeon.level.mobs) {
            if (ch != defender && !defender.fieldOfView[ch.pos]) {
                targets.add(ch);
            }
        }
        
        // 然后处理收集到的目标
        if (!targets.isEmpty()) {
            // Apply invisibility to attacker
            Buff.affect(attacker, Invisibility.class, targets.size() * 3f);
            
            // Deal damage to characters not in defender's field of view
            for (Char ch : targets) if(ch.isAlive()){
                int shadowDamage =(int) Math.round(damage *(charge*0.2+ 0.4f));
                ch.damage(shadowDamage, attacker);
                if(!ch.isAlive()){
                    charge++;
                }
                GLog.n("Shadow attack!");
            }else{
                charge++;
            }
        } else {
            GLog.n("No shadows to attack!");
        }
    }
}
