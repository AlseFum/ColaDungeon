/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.coladungeon.items.food;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Hunger;
import com.coladungeon.actors.buffs.Healing;
import com.coladungeon.actors.buffs.Barkskin;
import com.coladungeon.actors.buffs.Bless;
import com.coladungeon.actors.buffs.Haste;
import com.coladungeon.actors.buffs.MindVision;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.hero.Talent;
import com.coladungeon.messages.Messages;
import com.coladungeon.utils.GLog;

public class GoldenApple extends Food {

    {
        image = 114516;
        energy = Hunger.STARVING; 

        bones = true;
    }

    @Override
    protected float eatingTime(){
        if (Dungeon.hero.hasTalent(Talent.IRON_STOMACH)
                || Dungeon.hero.hasTalent(Talent.ENERGIZING_MEAL)
                || Dungeon.hero.hasTalent(Talent.MYSTICAL_MEAL)
                || Dungeon.hero.hasTalent(Talent.INVIGORATING_MEAL)
                || Dungeon.hero.hasTalent(Talent.FOCUSED_MEAL)
                || Dungeon.hero.hasTalent(Talent.ENLIGHTENING_MEAL)){
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        
        // 添加治疗效果：立即回复部分生命值并持续一段时间
        Buff.affect(hero, Healing.class).setHeal(hero.HT / 1, 0.5f, 0);
        
        // 添加荆棘皮效果：提高防御力
        Buff.affect(hero, Barkskin.class).set(5 + hero.lvl/2, 5);
        
        // 添加祝福效果：提高命中率
        Buff.affect(hero, Bless.class, 30f);
        
        // 添加急速效果：增加移动和攻击速度
        Buff.affect(hero, Haste.class, 20f);
        
        // 添加心灵视觉：可以看到敌人位置
        Buff.affect(hero, MindVision.class, 15f);
        
        // 显示消息
        GLog.p("金苹果的神奇能量在你的体内流动！");
    }

    @Override
    public int value() {
        return 5 * quantity;
    }
    
    @Override
    public String info() {
        String info = super.info();
        info += "\n\n" + Messages.get(this, "effect_desc");
        return info;
    }
}
 