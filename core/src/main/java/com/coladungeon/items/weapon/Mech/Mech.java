package com.coladungeon.items.weapon.Mech;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.FloatingText;
import com.coladungeon.effects.Speck;
import com.coladungeon.items.weapon.Weapon;
import com.coladungeon.items.wands.DamageWand;
import com.coladungeon.sprites.CharSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.ui.BuffIndicator;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Mech extends Weapon {

    private int tier;

    {
        tier = 4;
        image = ItemSpriteSheet.WEAPON_HOLDER;

        DLY = 1.2f; //0.8x speed
        ACC = 1.2f; //20% more accurate
    }

    @Override
    public String name() {
        return "机械魔导器";
    }

    @Override
    public String desc() {
        return "这是一把由精密机械和魔法晶体构成的高级武器。它能够在攻击时释放多重魔法打击，并积累能量来释放腐蚀性的奥术能量。\n\n" +
               "每次攻击会造成3次独立的魔法伤害，每次命中都会积累能量。当能量充满时，会对视野内最多3个敌人施加奥术腐蚀效果。";
    }

    private int charge = 0;
    private static final int MAX_CHARGE = 100;
    private static final int HITS_PER_ATTACK = 3;
    private static final int MAX_AFFECTED_ENEMIES = 3;

    @Override
    public int min(int lvl) {
        return tier + lvl;
    }

    @Override
    public int STRReq(int lvl) {
        return (8 + tier * 2) - (lvl / 2);
    }

    @Override
    public int max(int lvl) {
        return Math.round(2f * (tier + 1)) + lvl * Math.round(0.5f * (tier + 1));
    }

    @Override
    public int damageRoll(Char owner) {
        int baseDamage = super.damageRoll(owner);
        // Each hit does 1/4 to 1/2 of base damage
        return Random.NormalIntRange(baseDamage/4, baseDamage/2);
    }

    @Override
    public float delayFactor(Char owner) {
        // Slightly increased delay to account for multiple hits
        return super.delayFactor(owner) * 1.2f;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // Calculate individual hit damages
        int[] damages = new int[HITS_PER_ATTACK];
        damages[0] = damage;  // First hit
        int totalDamage = damage;
        
        // Calculate additional hits
        for (int i = 1; i < HITS_PER_ATTACK; i++) {
            damages[i] = damageRoll(attacker);
            totalDamage += damages[i];
        }
        
        // Show all damage numbers simultaneously with different positions
        for (int i = 0; i < HITS_PER_ATTACK; i++) {
            FloatingText.show(
                defender.sprite.x + (i-1) * 8,
                defender.sprite.y - i * 4,
                String.valueOf(damages[i]),
                0x4488CC
            );
        }
        
        // Apply total damage as magical damage
        defender.damage(totalDamage, DamageWand.class);
        
        // Show multiple magical hit effects in a spread pattern
        for (int i = 0; i < HITS_PER_ATTACK; i++) {
            defender.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 2);
            defender.sprite.emitter().x -= 4;  // Shift each burst left
        }
        defender.sprite.emitter().x += 4 * HITS_PER_ATTACK;  // Reset position
        
        // Handle charge for all hits at once
        if (attacker instanceof Hero) {
            charge += 10 * HITS_PER_ATTACK;
            if (charge >= MAX_CHARGE) {
                activateCharge((Hero)attacker);
            }
        }
        
        return 0;
    }

    private void activateCharge(Hero hero) {
        charge = 0;
        
        // Count current affected enemies
        int affectedCount = 0;
        for (Char ch : Actor.chars()) {
            if (ch.buff(ArcaneCorrosion.class) != null) {
                affectedCount++;
            }
        }
        
        // Only apply new buffs if we haven't reached the limit
        if (affectedCount < MAX_AFFECTED_ENEMIES) {
            // Apply to nearest enemies first
            for (Char ch : Actor.chars()) {
                if (ch.alignment == Char.Alignment.ENEMY 
                    && Dungeon.level.heroFOV[ch.pos]
                    && ch.buff(ArcaneCorrosion.class) == null) {
                    
                    Buff.affect(ch, ArcaneCorrosion.class).set(5 + hero.lvl/2);
                    affectedCount++;
                    
                    if (affectedCount >= MAX_AFFECTED_ENEMIES) {
                        break;
                    }
                }
            }
        }

        hero.sprite.centerEmitter().burst(Speck.factory(Speck.STEAM), 8);
        GLog.p("你的机械魔导器释放出奥术能量！");
    }

    @Override
    public String status() {
        return charge + "/" + MAX_CHARGE;
    }

    private static final String CHARGE = "charge";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CHARGE, charge);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        charge = bundle.getInt(CHARGE);
    }

    public static class ArcaneCorrosion extends Buff {
        private int duration;
        private static final int TICK = 1;

        {
            type = buffType.NEGATIVE;
            announced = true;
        }

        public void set(int duration) {
            this.duration = duration;
        }

        @Override
        public boolean act() {
            if (duration > 0) {
                int damage = Random.NormalIntRange(1, 3 + Dungeon.depth/2);
                target.damage(damage, this);
                target.sprite.emitter().burst(Speck.factory(Speck.STEAM), 2);
                
                duration--;
                spend(TICK);
            } else {
                detach();
            }
            return true;
        }

        @Override
        public int icon() {
            return BuffIndicator.CORRUPT;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.6f, 0.2f, 0.8f);
        }

        @Override
        public String toString() {
            return "奥术腐蚀";
        }

        @Override
        public String desc() {
            return "受到持续的魔法伤害，剩余" + duration + "回合。";
        }

        private static final String DURATION = "duration";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DURATION, duration);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            duration = bundle.getInt(DURATION);
        }
    }
} 