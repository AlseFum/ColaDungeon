package com.coladungeon.mod.ArknightsMod.items.weapon;

import com.coladungeon.Assets;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.weapon.melee.MeleeWeapon;
import com.coladungeon.sprites.ItemSpriteManager;

public class RhodesStandardSword extends MeleeWeapon {
    static{
        ItemSpriteManager.registerTexture("cola/RhodesStandardSword.png",32)
            .label("RhodesStandardSword");
    }
    {
        image = ItemSpriteManager.ByName("RhodesStandardSword");
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
                lvl * 6; // 每级提升
    }
    @Override
    public String name(){
        return "罗德岛标准剑";
    }
    @Override
    public String desc(){
        return "罗德岛标准剑,供一般人使用。";
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
                damage += Hero.heroDamageIntRange(0, exStr)*2;
            }
        }
        
        return damage;
    }
}
