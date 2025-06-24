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
        // 术师分支
        Core(Role.Caster, "中坚术师"),
        Splash(Role.Caster, "扩散术师"),
        Blast(Role.Caster, "轰击术师"),
        Chain(Role.Caster, "链术师"),
        MechAccord(Role.Caster, "驭械术师"),
        Phalanx(Role.Caster, "阵法术师"),
        Mystic(Role.Caster, "秘术师"),
        Primal(Role.Caster, "本源术师"),
        Splashcaster(Role.Caster, "塑灵术师"),
        
        // 先锋分支
        Pioneer(Role.Vanguard, "尖兵"),
        
        // 其他
        None(Role.None, "无"),
        NPC(Role.None, "NPC"),
        Geek(Role.None, "怪杰");
        public final Role r;
        public final String name;

        Branch() {
            this.r = Role.None;
            this.name = "未知";
        }

        Branch(Role r) {
            this.r = r;
            this.name = "未知";
        }
        
        Branch(Role r, String name) {
            this.r = r;
            this.name = name;
        }
    }
}
