package com.coladungeon.sprites;

import com.coladungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class DroneSprite extends MobSprite {

    public DroneSprite() {
        super();

        // 使用Warrior的贴图作为基础，支持动态key覆盖
        TextureFilm frames = textureWithFallback("mod:drone", Assets.Sprites.WARRIOR, 12, 15);

        // 设置各种状态的动画
        idle = new Animation(2, true);
        idle.frames(frames, 0, 0, 0, 1);

        run = new Animation(10, true);
        run.frames(frames, 2, 3, 4, 5, 6, 7);

        attack = new Animation(15, false);
        attack.frames(frames, 8, 9, 10, 11, 12);

        die = new Animation(10, false);
        die.frames(frames, 13, 14, 15, 16, 17);

        play(idle);
    }
} 