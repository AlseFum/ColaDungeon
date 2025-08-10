package com.coladungeon.ui;

import com.coladungeon.CDSettings;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.scenes.PixelScene;
import com.coladungeon.windows.WndConsole;
import com.watabou.noosa.Image;

public class ConsoleButton extends IconButton {

    public ConsoleButton(){
        super();
        Image ic = Icons.get(Icons.INFO);
        ic.scale.set(PixelScene.align(0.8f));
        icon(ic);
    }

    @Override
    protected void onClick() {
        if (CDSettings.devConsole()){
            GameScene.show(new WndConsole());
        }
    }
}


