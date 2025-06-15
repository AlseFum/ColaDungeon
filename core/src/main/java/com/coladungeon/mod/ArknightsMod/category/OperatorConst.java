package com.coladungeon.mod.ArknightsMod.category;

public class OperatorConst {
    public static enum Rarity {
        //SX means X-stars. S1 means 1-star.
        //SExtra means Extra rarity. don't get it.
        S1(0),
        S2(0),
        S3(0),
        S4(2),
        S5(3),
        S6(6),
        SExtra(0);
        public int hope_cost;

        Rarity(int hopcost) {
            this.hope_cost = hopcost;
        }
    }

    public static enum Role {
        Vanguard,
        Supporter,
        Caster,
        Defender,
        Guard,
        Medic,
        Sniper,
        Specialist,
        None;
    }

    public static enum Branch {
        Core(Role.Caster),
        Splash(Role.Caster),
        Blast(Role.Caster),
        Chain(Role.Caster),
        MechAccord(Role.Caster),
        Phalanx(Role.Caster),
        Mystic(Role.Caster),
        None(Role.None),
        Pioneer(Role.Vanguard),

        NPC,
        Geek;
        public final Role r;

        Branch() {
            this.r = Role.None;
        }

        Branch(Role r) {
            this.r = r;
        }
    }
}
