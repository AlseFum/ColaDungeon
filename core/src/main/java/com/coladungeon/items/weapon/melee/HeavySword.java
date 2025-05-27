/*
 * Cola Dungeon
 */

package com.coladungeon.items.weapon.melee;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Vertigo;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.messages.Messages;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Random;

public class HeavySword extends MeleeWeapon {

    private static final float STUN_CHANCE = 0.25f; // 25% chance to stun
    private static final float ARMOR_BREAK_CHANCE = 0.3f; // 30% chance to break armor
    private static final int ARMOR_BREAK_AMOUNT = 5; // Amount of armor reduction

    {
        image = ItemSpriteSheet.GREATSWORD; // Temporarily using greatsword sprite
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 0.8f; // Lower pitch for heavier sound

        tier = 4;
        DLY = 1.5f; // 50% slower than normal
        
        // Bonus reach due to its size
        RCH = 2;
    }

    @Override
    public int STRReq(int lvl) {
        return super.STRReq(lvl) + 1; // Requires +1 strength compared to normal weapons
    }

    @Override
    public int min(int lvl) {
        return tier + 3 + lvl; // Higher base damage
    }

    @Override
    public int max(int lvl) {
        return 5 * (tier + 2) + lvl * (tier + 1); // Higher max damage
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // Chance to stun the target
        if (Random.Float() < STUN_CHANCE) {
            Buff.affect(defender, Vertigo.class, 2f);
            defender.sprite.showStatus(0xFF0000, "眩晕");
        }

        // Chance to deal bonus damage by bypassing some armor
        if (Random.Float() < ARMOR_BREAK_CHANCE) {
            // Calculate additional damage from armor penetration
            int bonusDmg = Math.min(ARMOR_BREAK_AMOUNT, defender.drRoll());
            if (bonusDmg > 0) {
                damage += bonusDmg;
                defender.sprite.showStatus(0xCCCCCC, "破甲 +" + bonusDmg);
            }
        }

        return super.proc(attacker, defender, damage);
    }

    @Override
    public String name() {
        return "重剑";
    }

    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder();
        desc.append("一把巨大而沉重的剑，挥舞起来需要更多的力量，但每一击都能造成毁灭性的伤害。\n\n");
        
        desc.append("_特殊效果:_\n");
        desc.append("- 有25%几率使敌人眩晕\n");
        desc.append("- 有30%几率破坏敌人护甲\n");
        desc.append("- 攻击范围增加到2格\n\n");
        
        desc.append("_属性:_\n");
        desc.append("- 攻击速度减慢50%\n");
        desc.append("- 比同级武器需要更高力量\n");
        desc.append("- 基础伤害：").append(min()).append("-").append(max()).append("\n");
        desc.append("- 力量需求：").append(STRReq()).append("\n");
        
        return desc.toString();
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        // Heavy sword ability: Crushing Blow
        // This could be implemented later when needed
    }
} 