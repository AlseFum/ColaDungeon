package com.coladungeon.items;

public class DivineAnkh extends Ankh {

    @Override
    public String name(){
        return "Divine Ankh";
    }
    @Override
    public String desc(){
        return "A divine ankh that can be used to revive infinite times.";
    }
    @Override
    public boolean isBlessed() {
        return true;
    }
    
    
}
