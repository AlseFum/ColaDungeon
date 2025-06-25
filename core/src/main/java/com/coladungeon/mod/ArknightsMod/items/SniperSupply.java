package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.supply.Supply;
import com.coladungeon.mod.ArknightsMod.items.build.sniper.Bow;
import com.coladungeon.mod.ArknightsMod.items.build.sniper.Crossbow;
import com.coladungeon.mod.ArknightsMod.items.build.sniper.PumpActionRifle;
import com.coladungeon.mod.ArknightsMod.items.build.sniper.SniperRifle;
import com.coladungeon.sprites.ItemSpriteManager;

public class SniperSupply extends Supply {
    {
        image = ItemSpriteManager.ByName("arksupply");
    }
    
    public SniperSupply() {
        super();
        this.put_in(Bow.class)
            .put_in(Crossbow.class)
            .put_in(SniperRifle.class)
            .put_in(PumpActionRifle.class)
            .name("狙击武器补给包")
            .desc("""
                一个专门装满了狙击职业武器的补给包，包含了各种不同风格的狙击武器。
                
                包含的武器类型：
                • 狙击弓 - 单发快速射击，受附魔效果最强
                • 狙击弩 - 快速装填，攻击速度最快
                • 狙击步枪 - 单次装填多次射击，平衡型武器
                • 泵动式狙击步枪 - 多次装填多次射击，伤害最高

                武器特性对比：
                • 弓：攻击速度快，附魔加成1.5倍，装填时间1.0秒
                • 弩：攻击速度最快，命中率最高，装填时间0.8秒
                • 步枪：射击效率高，每次装填5次射击，装填时间1.5秒
                • 泵动式：伤害最高，最多3次装填15次射击，装填时间2.0秒
                """);
    }
} 