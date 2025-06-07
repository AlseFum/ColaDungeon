package com.coladungeon.mod;
import com.coladungeon.actors.hero.HeroClassSheet;
public class mymy extends Index.Mod {
    @Override
    public void setup(){
        HeroClassSheet.registerStandardClass("test")
        .title("bur")
        .desc("bur")
        .shortDesc("bur")
        .register();
    }
}