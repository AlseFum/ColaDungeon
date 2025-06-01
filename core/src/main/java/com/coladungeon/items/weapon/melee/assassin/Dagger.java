package com.coladungeon.items.weapon.melee.assassin;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Invisibility;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.Speck;
import com.coladungeon.items.weapon.melee.MeleeWeapon;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.scenes.PixelScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

public class Dagger extends AssassinWeapon {
	
	{
		image = ItemSpriteSheet.DAGGER;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.1f;

		tier = 1;
		
		bones = false;
	}

	@Override
	public int max(int lvl) {
		return  4*(tier+1) +    //8 base, down from 10
				lvl*(tier+1);   //scaling unchanged
	}
	
	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	public boolean useTargeting(){
		return false;
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		sneakAbility(hero, target, 5, 2+buffedLvl(), this);
	}

	@Override
	public String abilityInfo() {
		if (levelKnown){
			return Messages.get(this, "ability_desc", 2+buffedLvl());
		} else {
			return Messages.get(this, "typical_ability_desc", 2);
		}
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return Integer.toString(2+level);
	}

	public static void sneakAbility(Hero hero, Integer target, int maxDist, int invisTurns, MeleeWeapon wep){
		if (target == null) {
			return;
		}

		PathFinder.buildDistanceMap(Dungeon.hero.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null), maxDist);
		if (PathFinder.distance[target] == Integer.MAX_VALUE || !Dungeon.level.heroFOV[target] || hero.rooted) {
			GLog.w(Messages.get(wep, "ability_target_range"));
			if (Dungeon.hero.rooted) PixelScene.shake( 1, 1f );
			return;
		}

		if (Actor.findChar(target) != null) {
			GLog.w(Messages.get(wep, "ability_occupied"));
			return;
		}

		wep.beforeAbilityUsed(hero, null);
		Buff.affect(hero, Invisibility.class, invisTurns-1); //1 fewer turns as ability is instant

		Dungeon.hero.sprite.turnTo( Dungeon.hero.pos, target);
		Dungeon.hero.pos = target;
		Dungeon.level.occupyCell(Dungeon.hero);
		Dungeon.observe();
		GameScene.updateFog();
		Dungeon.hero.checkVisibleMobs();

		Dungeon.hero.sprite.place( Dungeon.hero.pos );
		CellEmitter.get( Dungeon.hero.pos ).burst( Speck.factory( Speck.WOOL ), 6 );
		Sample.INSTANCE.play( Assets.Sounds.PUFF );

		hero.next();
		wep.afterAbilityUsed(hero);
	}
}
