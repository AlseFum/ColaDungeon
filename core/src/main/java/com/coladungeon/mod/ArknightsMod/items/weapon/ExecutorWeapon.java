package com.coladungeon.mod.ArknightsMod.items.weapon;

import com.coladungeon.actors.Char;
import com.coladungeon.actors.blobs.Blob;
import com.coladungeon.actors.blobs.SmokeScreen;
import com.coladungeon.actors.buffs.Blindness;
import com.coladungeon.actors.buffs.Paralysis;
import com.coladungeon.actors.buffs.Sleep;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.items.weapon.melee.MeleeWeapon;

public class ExecutorWeapon extends MeleeWeapon {
    {
        tier=1;
    }
    public float ambushRatio=0.5f;
    public float ambushAmp=0.4f;
    @Override
    public int damageRoll(Char owner) {
        int _damage=0;
        if (owner instanceof Hero hero) {
			Char enemy = hero.enemy();
			if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
				//deals 75% toward max to max on surprise, instead of min to max.
				int diff = max() - min();
				int damage = augment.damageFactor(Hero.heroDamageIntRange(
						min() + Math.round(diff*ambushRatio),
						max()));
				int exStr = hero.STR() - STRReq();
				if (exStr > 0) {
					damage += Hero.heroDamageIntRange(0, exStr);
				}
				_damage=damage;
			}
		}else{
            _damage=super.damageRoll(owner);

        }
		return _damage;
    }
    @Override
    public int min(int lvl) {
        return 1;
    }
    @Override
	public int max(int lvl) {
		return  4*(tier+1) +    //8 base, down from 10
				lvl*(tier+1);   //scaling unchanged
	}
    @Override
    public int proc(Char attacker, Char defender, int damage) {
        damage=super.proc(attacker, defender, damage);
        damage*=1+ambushAmp*ambushLevel(attacker,defender);
        if(damage>max()){
            damage=SE(attacker,defender,damage);
        }
        return damage;
    }
    public int SE(Char owner,Char enemy,int damage){
        return damage;
    }
    public static int ambushLevel(Char owner, Char enemy) {
        int lvl = 0;
        
        // 1. 检测敌人是否被玩家伏击
        if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(owner)) {
            lvl += 1;
        }
        
        // 2. 检测敌人是否在睡眠状态
        if (enemy.buff(Sleep.class) != null) {
            lvl += 1;
        }
        
        // 3. 检测玩家或敌人是否处于迷雾中（需替换为实际Buff类）
        if (Blob.volumeAt(owner.pos, SmokeScreen.class) > 0 || Blob.volumeAt(enemy.pos, SmokeScreen.class) > 0) {
            lvl += 1;
        }
        
        // 4. 检测敌人是否被眩晕或失明
        if (enemy.buff(Paralysis.class) != null || enemy.buff(Blindness.class) != null) {
            lvl += 1;
        }
        
        return lvl;
    }

    @Override
    public String name(){
        return"ExecutorWeapon";
    }
    @Override
    public String info(){
        return "这件武器在你伏击时能提升最小伤害。造成的伤害足够大时会触发额外效果。";
    }

}
