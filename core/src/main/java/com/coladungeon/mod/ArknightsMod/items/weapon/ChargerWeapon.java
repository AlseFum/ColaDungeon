package com.coladungeon.mod.ArknightsMod.items.weapon;

import com.coladungeon.actors.Char;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.weapon.melee.MeleeWeapon;
import com.coladungeon.utils.EventBus;
import com.coladungeon.utils.EventBus.EventData;

public class ChargerWeapon extends MeleeWeapon {
    {
        tier = 1;
    }

    static {
        // 注册击杀事件监听器
        EventBus.register("PhysicalDamage:afterDefense:earlyKilled", (data) -> {
            EventData eventData = (EventData) data;
            Char attacker = eventData.get("attacker");
            Char defender = eventData.get("defender");
            
            // 检查攻击者是否持有 ChargerWeapon 或其子类
            if (attacker instanceof Hero) {
                Hero hero = (Hero) attacker;
                if (hero.belongings.weapon instanceof ChargerWeapon) {
                    ChargerWeapon weapon = (ChargerWeapon) hero.belongings.weapon;
                    weapon.onKill(attacker, defender);
                }
            }
            return null;
        });
    }

    @Override
    public int min(int lvl) {
        return 1 + tier + lvl;
    }

    @Override
    public int max(int lvl) {
        return 4 * (tier + 1) +    //8 base, down from 10
                lvl * (tier + 1);   //scaling unchanged
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        return super.proc(attacker, defender, damage);
    }

    // 击杀时触发的效果，子类可以重写这个方法
    public void onKill(Char attacker, Char defender) {
        // 默认实现为空
    }

    @Override
    public String name() {
        return "ChargerWeapon";
    }

    @Override
    public String info() {
        return "这件武器在击杀敌人时会触发特殊效果。<min,max>:<" + min() + "," + max() + ">";
    }
}
