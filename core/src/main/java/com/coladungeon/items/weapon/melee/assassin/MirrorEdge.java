package com.coladungeon.items.weapon.melee.assassin;

import com.coladungeon.Assets;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.messages.Messages;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.actors.Char;

public class MirrorEdge extends Assassinator {

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
    @Override
    public void special_effect(Char attacker, Char defender, int damage) {
        //创建一个镜像，镜像会在攻击的同时尝试走到敌人视野之外，
        //玩家可以随时无损耗与镜像切换位置
    }
} 