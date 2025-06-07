/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.coladungeon;

import com.coladungeon.mod.Index;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.scenes.PixelScene;
import com.coladungeon.scenes.TitleScene;
import com.coladungeon.scenes.WelcomeScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PlatformSupport;

public class ColaDungeon extends Game {

    //rankings from v1.2.3 and older use a different score formula, so this reference is kept
    // public static final int older_scorer_version = 1;
    //savegames from versions older than v2.3.2 are no longer supported, and data from them is ignored
    // public static final int oldest_compatiable_version = 1;
    // public static final int v2_4_2 = 1;
    // public static final int v2_5_4 = 1;
    public static final int v_latest = 1;

    public ColaDungeon(PlatformSupport platform) {
        super(sceneClass == null ? WelcomeScene.class : sceneClass, platform);
		Index.init();
		Index.setup();
    }

    @Override
    public void create() {
        super.create();

        updateSystemUI();
        CDAction.loadBindings();

        Music.INSTANCE.enable(CDSettings.music());
        Music.INSTANCE.volume(CDSettings.musicVol() * CDSettings.musicVol() / 100f);
        Sample.INSTANCE.enable(CDSettings.soundFx());
        Sample.INSTANCE.volume(CDSettings.SFXVol() * CDSettings.SFXVol() / 100f);

        Sample.INSTANCE.load(Assets.Sounds.all);

    }

    @Override
    public void finish() {
        if (!DeviceCompat.isiOS()) {
            super.finish();
        } else {
            //can't exit on iOS (Apple guidelines), so just go to title screen
            switchScene(TitleScene.class);
        }
    }

    public static void switchNoFade(Class<? extends PixelScene> c) {
        switchNoFade(c, null);
    }

    public static void switchNoFade(Class<? extends PixelScene> c, SceneChangeCallback callback) {
        PixelScene.noFade = true;
        switchScene(c, callback);
    }

    public static void seamlessResetScene(SceneChangeCallback callback) {
        if (scene() instanceof PixelScene _scene) {
            _scene.saveWindows();

            switchNoFade((Class<? extends PixelScene>) sceneClass, callback);
        } else {
            resetScene();
        }
    }

    public static void seamlessResetScene() {
        seamlessResetScene(null);
    }

    @Override
    protected void switchScene() {
        super.switchScene();
        if (scene instanceof PixelScene _scene) {
            _scene.restoreWindows();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (width == 0 || height == 0) {
            return;
        }

        if (scene instanceof PixelScene _scene
                && (height != Game.height || width != Game.width)) {
            PixelScene.noFade = true;
            _scene.saveWindows();
        }

        super.resize(width, height);

        updateDisplaySize();

    }

    @Override
    public void destroy() {
        super.destroy();
        GameScene.endActorThread();
    }

    public void updateDisplaySize() {
        platform.updateDisplaySize();
    }

    public static void updateSystemUI() {
        platform.updateSystemUI();
    }
}
