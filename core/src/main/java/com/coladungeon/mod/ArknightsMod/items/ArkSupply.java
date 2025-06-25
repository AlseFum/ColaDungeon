package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;
import com.coladungeon.sprites.ItemSpriteManager;

public class ArkSupply extends Supply {
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
            .desc("""
                罗德岛的综合补给包，包含了所有职业的武器和装备。
                
                包含的补给包类型：
                • 守卫武器补给包 - 包含所有守卫职业武器
                • 重装装甲补给包 - 包含所有重装职业装甲
                • 先锋武器补给包 - 包含所有先锋职业武器
                • 狙击武器补给包 - 包含所有狙击职业武器
                • 术师武器补给包 - 包含所有术师职业武器
                • 辅助物品补给包 - 包含所有辅助职业武器和神器
                • 特种武器补给包 - 包含所有特种职业武器
                • 干员武器补给包 - 包含所有干员专属武器
                • 通用物品补给包 - 包含功能性物品和神器

                这是一个综合性的补给包，为玩家提供所有职业的装备选择。
                适合想要体验不同职业和玩法的玩家使用。
                
                补给包特点：
                • 全面覆盖：包含所有职业的装备
                • 分类清晰：按职业分类的补给包
                • 便于选择：可以根据需要选择特定职业
                • 丰富多样：提供大量的装备选择
                """)
            .image(ItemSpriteManager.ByName("arksupply"));
    }
}