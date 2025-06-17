package com.coladungeon.mod;

import java.util.ArrayList;

import com.coladungeon.mod.ArknightsMod.ArknightsMod;
import com.coladungeon.mod.ColaMisc.ColaMisc;

public class Index {

    public static void init() {
        load(new ArknightsMod());
        load(new ColaMisc());
        }

    public static class Mod {

        public String author;
        public String namespace;

        public void setup() {
        }
    }
    //Below is the code for the mod loader
    public static ArrayList<Mod> mods = new ArrayList<>();

    public static void setup() {
        for (Mod mod : mods) {
            try {
                mod.setup();
            } catch (Exception e) {
                System.out.println("[MOD] Error in mod "+mod.namespace+"@"+mod.author+" :\n\t"+e);

            }
            
        }
    }

    public static void load(Mod mod) {
        mods.add(mod);
    }

    @FunctionalInterface
    public interface SetupFn {
        void setup();
    }

    public static void load(SetupFn setup) {
        load(new Mod() {
            @Override
            public void setup() {
                setup.setup();
            }
        });
    }

}
