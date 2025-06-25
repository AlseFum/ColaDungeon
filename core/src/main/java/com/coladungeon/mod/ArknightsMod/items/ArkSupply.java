package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.Item;
import com.coladungeon.items.supply.Supply;
import com.coladungeon.mod.ArknightsMod.items.build.ArknightsMelee;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.*;
import com.coladungeon.mod.ArknightsMod.items.build.caster.*;
import com.coladungeon.mod.ArknightsMod.items.build.defender.*;
import com.coladungeon.mod.ArknightsMod.items.build.guard.*;
import com.coladungeon.mod.ArknightsMod.items.build.support.*;
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.*;
import com.coladungeon.mod.ArknightsMod.operator.Phantom.PhantomWeapon;
import com.coladungeon.mod.ArknightsMod.operator.TexasTheOmertosa.TexasWeapon;
import com.coladungeon.sprites.ItemSpriteManager;

public class ArkSupply extends Supply {
    {
        image = ItemSpriteManager.ByName("arksupply");
    }

    public ArkSupply() {
        super();
        this.put_in(GuardSupply.class)
            .put_in(DefenderSupply.class)
            .put_in(VanguardSupply.class)
            .put_in(CasterSupply.class)
            .put_in(SupportSupply.class)
            .put_in(SpecialistSupply.class)
            .put_in(OperatorSupply.class)
            .put_in(UtilitySupply.class)
            .name("罗德岛补给包")
            .desc("罗德岛的综合补给包，包含了所有职业的武器和装备。");
    }

    public static class GuardSupply extends Supply {
        {
            image = ItemSpriteManager.ByName("guard_chips");
        }
        
        public GuardSupply() {
            super();
            this.put_in(CenturionWeapon.class)
                .put_in(DreadnoughtWeapon.class)
                .put_in(ArtsFighterWeapon.class)
                .put_in(CrusherWeapon.class)
                .put_in(EarthshakerWeapon.class)
                .put_in(FighterWeapon.class)
                .put_in(InstructorWeapon.class)
                .put_in(LiberatorWeapon.class)
                .put_in(LordWeapon.class)
                .put_in(ReaperWeapon.class)
                .put_in(SolobladeWeapon.class)
                .put_in(SwordmasterWeapon.class)
                .name("守卫武器补给包")
                .desc("一个专门装满了守卫职业武器的补给包，包含了各种不同风格的守卫武器。");
        }
    }

    public static class DefenderSupply extends Supply {
        {
            image = ItemSpriteManager.ByName("defender_chips");
        }
        
        public DefenderSupply() {
            super();
            this.put_in(IronGuardArmor.class)
                .put_in(GuardianArmor.class)
                .put_in(UnbreakableArmor.class)
                .put_in(MagicGuardArmor.class)
                .put_in(DecisiveArmor.class)
                .put_in(FortressArmor.class)
                .put_in(SentinelArmor.class)
                .put_in(OriginGuardArmor.class)
                .name("重装装甲补给包")
                .desc("一个专门装满了重装职业装甲的补给包，包含了各种不同风格的重装装甲。");
        }
    }

    public static class VanguardSupply extends Supply {
        {
            image = ItemSpriteManager.ByName("vanguard_chips");
        }
        
        public VanguardSupply() {
            super();
            this.put_in(AgentWeapon.class)
                .put_in(ChargerWeapon.class)
                .put_in(FlagBearerWeapon.class)
                .put_in(PioneerWeapon.class)
                .put_in(TacticianWeapon.class)
                .name("先锋武器补给包")
                .desc("一个专门装满了先锋职业武器的补给包，包含了各种不同风格的先锋武器。");
        }
    }

    public static class CasterSupply extends Supply {
        {
            image = ItemSpriteManager.ByName("caster_chips");
        }
        
        public CasterSupply() {
            super();
            this.put_in(CasterWeapon.class)
                .put_in(MechAccordWeapon.class)
                .name("术师武器补给包")
                .desc("""
                    一个专门装满了术师职业武器的补给包，包含了各种不同风格的术师武器。
                    
                    包含的武器类型：
                    • 术师武器 - 基础术师武器
                    • 机械协奏武器 - 机械术师武器

                    每个武器都有其独特的法术机制和特殊效果，
                    为术师干员提供多样化的法术攻击选择。
                    
                    武器特点：
                    • 法术伤害：强大的法术攻击力
                    • 元素效果：火、冰、雷等元素攻击
                    • 范围攻击：大范围的法术攻击
                    • 充能系统：法术充能和释放机制
                    • 机械联动：与机械单位的协同作战
                    """);
        }
    }

    public static class SupportSupply extends Supply {
        {
            image = ItemSpriteManager.ByName("support_chips");
        }
        
        public SupportSupply() {
            super();
            this.put_in(StagnatorWeapon.class)
                .put_in(WeakenWeapon.class)
                .put_in(BardArtifact.class)
                .put_in(SummonerArtifact.class)
                .put_in(CraftsmanWeapon.class)
                .put_in(WitchWeapon.class)
                .put_in(WitchArtifact.class)
                .name("辅助物品补给包")
                .desc("一个专门装满了辅助职业武器和神器的补给包，包含了各种不同风格的辅助物品。");
        }
    }

    public static class SpecialistSupply extends Supply {
        {
            image = ItemSpriteManager.ByName("specialist_chips");
        }
        
        public SpecialistSupply() {
            super();
            this.put_in(AlchemistWeapon.class)
                .put_in(DollkeeperWeapon.class)
                .put_in(GeekWeapon.class)
                .put_in(HookWeapon.class)
                .put_in(MerchantWeapon.class)
                .put_in(PusherWeapon.class)
                .put_in(SkyRangerWeapon.class)
                .put_in(TrappistWeapon.TrappistArtifact.class)
                .put_in(TrappistWeapon.ExplosiveTrapItem.class)
                .name("特种武器补给包")
                .desc("一个专门装满了特种职业武器的补给包，包含了各种不同风格的特种武器。");
        }
    }

    public static class OperatorSupply extends Supply {
        {
            image = ItemSpriteManager.ByName("arksupply");
        }
        
        public OperatorSupply() {
            super();
            this.put_in(PhantomWeapon.class)
                .put_in(TexasWeapon.class)
                .name("干员武器补给包")
                .desc("一个专门装满了干员专属武器的补给包，包含了各种干员的专属武器。");
        }
    }

    public static class UtilitySupply extends Supply {
        {
            image = ItemSpriteManager.ByName("arksupply");
        }
        
        public UtilitySupply() {
            super();
            this.put_in(CommandTerminal.class)
                .put_in(RefinedCloak.class)
                .put_in(ArknightsMelee.class)
                .name("通用物品补给包")
                .desc("罗德岛指挥终端 - 充能获得cost点数，用于部署干员\n罗德岛精制潜行斗篷 - 充能获得隐身效果的神器");
        }
    }
}