package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.Assets;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.weapon.melee.MeleeWeapon;
import com.coladungeon.sprites.ItemSpriteSheet;

public class RhodesStandardSword extends MeleeWeapon {

    {
        image = ItemSpriteSheet.SWORD;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1f;
    }

    @Override
    public int min(int lvl) {
        return 4 +  // 基础伤害
                lvl * 2; // 每级提升
    }

    @Override
    public int max(int lvl) {
        return 12 +  // 基础伤害
                lvl * 3; // 每级提升
    }

    @Override
    public int STRReq(int lvl) {
        return 10; // 力量需求
    }

    @Override
    public int damageRoll(Char owner) {
        int damage = super.damageRoll(owner);
        
        // 如果使用者是英雄，增加额外伤害
        if (owner instanceof Hero) {
            int exStr = ((Hero)owner).STR() - STRReq();
            if (exStr > 0) {
                damage += Hero.heroDamageIntRange(0, exStr);
            }
        }
        
        return damage;
    }
}
