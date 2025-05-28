package com.coladungeon.items.weapon.melee.assassin;

import com.coladungeon.Assets;
import com.coladungeon.sprites.ItemSpriteSheet;

public class AssassinDagger extends AssassinWeapon {

    {
        image = ItemSpriteSheet.DAGGER;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 1.1f;

        tier = 1;  // 1级武器
        
        // 设置伏击伤害比例为70%
        SURPRISE_DMG_FACTOR = 0.7f;
        // 每次连击增加12%伤害
        COMBO_DMG_BONUS = 0.12f;
    }

    @Override
    public int max(int lvl) {
        return 4*(tier+1) +    // 基础伤害8
                lvl*(tier+1);   // 每级+2伤害
    }

    @Override
    public String desc() {
        return "这把轻巧的匕首适合伏击敌人。\n"
             + "成功伏击时会积累连击能量，每次连击都会增强下一次伏击的威力。";
    }
} 