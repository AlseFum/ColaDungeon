package com.coladungeon.mod.ArknightsMod.themes;

import com.coladungeon.levels.themes.ThemePack;

public class DissociativeRecombination extends ThemePack {
    public DissociativeRecombination(){
    super(LungmenLevel.class,LungmenLevel.class,
            (depth, branch) ->
                    (branch == 0 && depth > 20 && depth <= 25) ? (short) 2 : (short) 0);
    }
}
