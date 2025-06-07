package com.coladungeon.mod;

import java.util.ArrayList;

public class Index {

    public static void init() {
        load(new mymy());
    }

    public static class Mod {

        public String author;
        public String namespace;

        public void setup() {
        }
    }
    public static ArrayList<Mod> mods = new ArrayList<>();

    public static void setup() {
        for (Mod mod : mods) {
            mod.setup();
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
