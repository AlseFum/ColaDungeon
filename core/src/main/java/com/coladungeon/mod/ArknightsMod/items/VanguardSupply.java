package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.AgentWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.ChargerWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.FlagBearerWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.PioneerWeapon;
import com.coladungeon.mod.ArknightsMod.items.build.vanguard.TacticianWeapon;
import com.coladungeon.sprites.ItemSpriteManager;

public class VanguardSupply extends Supply {
    {
        image = ItemSpriteManager.ByName("arksupply");
    }
    
    public VanguardSupply() {
        super();
        this.put_in(AgentWeapon.class)
            .put_in(ChargerWeapon.class)
            .put_in(FlagBearerWeapon.class)
            .put_in(PioneerWeapon.class)
            .put_in(TacticianWeapon.class)
            .name("先锋武器补给包")
            .desc("""
                一个专门装满了先锋职业武器的补给包，包含了各种不同风格的先锋武器。
                
                包含的武器类型：
                • 尖兵武器 - 充能恢复部署费用的武器
                • 冲锋手武器 - 杀敌回cost，充能连击的武器
                • 执旗手武器 - 充能cost，释放buff的武器
                • 情报官武器 - 长距离攻击，装载投掷武器的武器
                • 战术家武器 - 充能释放战术点的武器

                每个武器都有其独特的充能机制和特殊效果，
                为先锋干员提供多样化的战斗选择。
                
                武器特点：
                • 充能系统：通过不同方式获得充能
                • 部署费用：影响干员的部署和撤退
                • 战术支援：为友军提供各种增益效果
                • 灵活战斗：适应不同的战场环境
                """);
    }
} 