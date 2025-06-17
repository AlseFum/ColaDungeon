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

package com.coladungeon.items.stones;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Invisibility;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.Speck;
import com.coladungeon.items.scrolls.ScrollOfTeleportation;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.scenes.InterlevelScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class StoneOfDungeonTravel extends Runestone {
    
    {
        image = ItemSpriteSheet.STONE_SHOCK;
        defaultAction = AC_APPLY;
    }
    
    public static final String AC_APPLY = "APPLY";
    
    private int lastFloor = -1;
    private boolean activated = false;
    
    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        
        if (action.equals(AC_APPLY)) {
            activate(hero);
        }
    }
    
    private void activate(Hero hero) {
        if (!Dungeon.interfloorTeleportAllowed()) {
            GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele"));
            return;
        }
        
        activated = true;
        
        GameScene.show(new WndOptions(
                "Dungeon Travel Stone",
                "Where would you like to travel?",
                "One Floor Up",
                "One Floor Down",
                "Five Floors Up", 
                "Five Floors Down"
        ) {
            @Override
            protected void onSelect(int index) {
                if (index < 0) return;
                
                lastFloor = Dungeon.depth;
                int targetDepth = Dungeon.depth;
                
                switch (index) {
                    case 0: // 上升一层
                        targetDepth = Math.max(1, Dungeon.depth - 1);
                        break;
                    case 1: // 下降一层
                        targetDepth = Math.min(40, Dungeon.depth + 1);
                        break;
                    case 2: // 上升五层
                        targetDepth = Math.max(1, Dungeon.depth - 5);
                        break;
                    case 3: // 下降五层
                        targetDepth = Math.min(40, Dungeon.depth + 5);
                        break;
                }
                
                if (targetDepth != Dungeon.depth) {
                    Buff.affect(hero, Invisibility.class, 2f);
                    InterlevelScene.mode = InterlevelScene.Mode.RETURN;
                    InterlevelScene.returnDepth = targetDepth;
                    InterlevelScene.returnBranch = Dungeon.branch;
                    InterlevelScene.returnPos = -1;
                    Game.switchScene(InterlevelScene.class);
                    
                    // 使用后消耗
                    detach(hero.belongings.backpack);
                    
                    CellEmitter.get(hero.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
                    Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
                }
            }
        });
    }
    
    @Override
    protected void activate(int cell) {
        if (!activated) {
            activate(Dungeon.hero);
        }
    }
    
    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }
    
    private static final String LAST_FLOOR = "last_floor";
    private static final String ACTIVATED = "activated";
    
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LAST_FLOOR, lastFloor);
        bundle.put(ACTIVATED, activated);
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        lastFloor = bundle.getInt(LAST_FLOOR);
        activated = bundle.getBoolean(ACTIVATED);
    }
    
    @Override
    public int value() {
        return 50 * quantity;
    }
    
    @Override
    public String name() {
        return Messages.get(this, "name");
    }
} 