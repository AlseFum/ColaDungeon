package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;
import com.coladungeon.items.wands.WandOfBlastWave;
import com.coladungeon.items.wands.WandOfCorrosion;
import com.coladungeon.items.wands.WandOfCorruption;
import com.coladungeon.items.wands.WandOfDisintegration;
import com.coladungeon.items.wands.WandOfFireblast;
import com.coladungeon.items.wands.WandOfFrost;
import com.coladungeon.items.wands.WandOfLightning;
import com.coladungeon.items.wands.WandOfLivingEarth;
import com.coladungeon.items.wands.WandOfMagicMissile;
import com.coladungeon.items.wands.WandOfPrismaticLight;
import com.coladungeon.items.wands.WandOfRegrowth;
import com.coladungeon.items.wands.WandOfTransfusion;
import com.coladungeon.items.wands.WandOfWarding;
import com.coladungeon.mod.ArknightsMod.category.OperatorConst;
import com.coladungeon.mod.ArknightsMod.items.build.ArknightsMelee;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.AlchemistWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.DollkeeperWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.GeekWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.HookWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.MerchantWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.PusherWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.SkyRangerWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.Specialist.TrappistWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.caster.CasterWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.caster.MechAccordWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.defender.DecisiveArmor;
import com.coladungeon.mod.ArknightsMod.items.build.defender.FortressArmor;
import com.coladungeon.mod.ArknightsMod.items.build.defender.GuardianArmor;
import com.coladungeon.mod.ArknightsMod.items.build.defender.IronGuardArmor;
import com.coladungeon.mod.ArknightsMod.items.build.defender.MagicGuardArmor;
import com.coladungeon.mod.ArknightsMod.items.build.defender.OriginGuardArmor;
import com.coladungeon.mod.ArknightsMod.items.build.defender.SentinelArmor;
import com.coladungeon.mod.ArknightsMod.items.build.defender.UnbreakableArmor;
import com.coladungeon.mod.ArknightsMod.items.build.guard.ArtsFighterWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.CenturionWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.CrusherWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.DreadnoughtWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.EarthshakerWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.FighterWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.InstructorWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.LiberatorWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.LordWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.ReaperWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.SolobladeWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.guard.SwordmasterWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.support.BardArtifact;
import com.coladungeon.mod.ArknightsMod.items.build.support.CraftsmanWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.support.StagnatorWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.support.SummonerArtifact;
import com.coladungeon.mod.ArknightsMod.items.build.support.WeakenWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.support.WitchArtifact;
import com.coladungeon.mod.ArknightsMod.items.build.support.WitchWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.AgentWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.ChargerWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.FlagBearerWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.PioneerWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.TacticianWeapon;
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
            .put_in(SniperSupply.class)
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
                .name("近卫武器补给包")
                .desc("一个专门装满了近卫职业武器的补给包，包含了各种不同风格的守卫武器。");
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
            
            // 术师武器
            this.put_in(CasterWeapon.class)
                .put_in(MechAccordWeapon.class)
                
                // 所有法杖
                .put_in(WandOfMagicMissile.class)
                .put_in(WandOfFireblast.class)
                .put_in(WandOfFrost.class)
                .put_in(WandOfLightning.class)
                .put_in(WandOfDisintegration.class)
                .put_in(WandOfCorrosion.class)
                .put_in(WandOfCorruption.class)
                .put_in(WandOfBlastWave.class)
                .put_in(WandOfLivingEarth.class)
                .put_in(WandOfPrismaticLight.class)
                .put_in(WandOfRegrowth.class)
                .put_in(WandOfTransfusion.class)
                .put_in(WandOfWarding.class)
                
                // 术师模块
                .put_in(CoreCasterModule.class)
                .put_in(SplashCasterModule.class)
                .put_in(ChainCasterModule.class)
                .put_in(MechAccordCasterModule.class)
                .put_in(BlastCasterModule.class)
                .put_in(MysticCasterModule.class)
                
                .name("术师武器法杖补给包")
                .desc("""
                    一个专门装满了术师职业武器和法杖的补给包，包含了各种不同风格的术师装备。
                    
                    包含的装备类型：
                    
                    术师武器：
                    • 便携制式施术单元 - 基础术师武器，可装载法杖和模块
                    • 机械协奏武器 - 机械术师武器
                    
                    法杖（共13种）：
                    • 魔法导弹法杖 - 基础魔法攻击
                    • 火焰法杖 - 火焰范围攻击
                    • 冰霜法杖 - 冰冻减速效果
                    • 闪电法杖 - 链式闪电攻击
                    • 分解法杖 - 穿透攻击
                    • 腐蚀法杖 - 毒性伤害
                    • 腐化法杖 - 敌人转化
                    • 冲击波法杖 - 击退效果
                    • 活体大地法杖 - 召唤石像
                    • 棱镜光法杖 - 眩光攻击
                    • 再生法杖 - 植物召唤
                    • 输血法杖 - 生命转移
                    • 守护法杖 - 守护召唤
                    
                    术师模块（6种分支）：
                    • 核心术师模块 - 增强基础法术伤害
                    • 扩散术师模块 - 添加范围伤害效果
                    • 链术师模块 - 法术跳跃传播
                    • 驭械术师模块 - 施法后获得充能增益
                    • 轰击术师模块 - 添加直线穿透伤害
                    • 秘术师模块 - 施法时恢复生命值
                    
                    特点：
                    • 模块化设计：术师武器可装载法杖和多个模块
                    • 组合效果：不同法杖与模块搭配产生独特效果
                    • 充能系统：法杖提供充能，武器消耗充能施法
                    • 多样化攻击：从单体到范围，从直接伤害到特殊效果
                    """);
        }
    }
    
    // 术师模块类定义
    public static class CoreCasterModule extends CasterWeapon.CasterModule {
        public CoreCasterModule() {
            super(OperatorConst.Branch.Core);
        }
    }
    
    public static class SplashCasterModule extends CasterWeapon.CasterModule {
        public SplashCasterModule() {
            super(OperatorConst.Branch.Splash);
        }
    }
    
    public static class ChainCasterModule extends CasterWeapon.CasterModule {
        public ChainCasterModule() {
            super(OperatorConst.Branch.Chain);
        }
    }
    
    public static class MechAccordCasterModule extends CasterWeapon.CasterModule {
        public MechAccordCasterModule() {
            super(OperatorConst.Branch.MechAccord);
        }
    }
    
    public static class BlastCasterModule extends CasterWeapon.CasterModule {
        public BlastCasterModule() {
            super(OperatorConst.Branch.Blast);
        }
    }
    
    public static class MysticCasterModule extends CasterWeapon.CasterModule {
        public MysticCasterModule() {
            super(OperatorConst.Branch.Mystic);
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