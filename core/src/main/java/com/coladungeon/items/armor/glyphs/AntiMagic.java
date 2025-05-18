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

package com.coladungeon.items.armor.glyphs;

import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Charm;
import com.coladungeon.actors.buffs.Degrade;
import com.coladungeon.actors.buffs.Hex;
import com.coladungeon.actors.buffs.MagicalSleep;
import com.coladungeon.actors.buffs.Vulnerable;
import com.coladungeon.actors.buffs.Weakness;
import com.coladungeon.actors.hero.spells.Judgement;
import com.coladungeon.actors.hero.abilities.duelist.ElementalStrike;
import com.coladungeon.actors.hero.abilities.mage.ElementalBlast;
import com.coladungeon.actors.hero.abilities.mage.WarpBeacon;
import com.coladungeon.actors.hero.spells.GuidingLight;
import com.coladungeon.actors.hero.spells.HolyLance;
import com.coladungeon.actors.hero.spells.HolyWeapon;
import com.coladungeon.actors.hero.spells.Smite;
import com.coladungeon.actors.hero.spells.Sunray;
import com.coladungeon.actors.mobs.CrystalWisp;
import com.coladungeon.actors.mobs.DM100;
import com.coladungeon.actors.mobs.Eye;
import com.coladungeon.actors.mobs.Shaman;
import com.coladungeon.actors.mobs.Warlock;
import com.coladungeon.actors.mobs.YogFist;
import com.coladungeon.items.armor.Armor;
import com.coladungeon.items.bombs.ArcaneBomb;
import com.coladungeon.items.bombs.HolyBomb;
import com.coladungeon.items.scrolls.ScrollOfRetribution;
import com.coladungeon.items.scrolls.ScrollOfTeleportation;
import com.coladungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.coladungeon.items.wands.CursedWand;
import com.coladungeon.items.wands.WandOfBlastWave;
import com.coladungeon.items.wands.WandOfDisintegration;
import com.coladungeon.items.wands.WandOfFireblast;
import com.coladungeon.items.wands.WandOfFrost;
import com.coladungeon.items.wands.WandOfLightning;
import com.coladungeon.items.wands.WandOfLivingEarth;
import com.coladungeon.items.wands.WandOfMagicMissile;
import com.coladungeon.items.wands.WandOfPrismaticLight;
import com.coladungeon.items.wands.WandOfTransfusion;
import com.coladungeon.items.wands.WandOfWarding;
import com.coladungeon.items.weapon.enchantments.Blazing;
import com.coladungeon.items.weapon.enchantments.Grim;
import com.coladungeon.items.weapon.enchantments.Shocking;
import com.coladungeon.items.weapon.missiles.darts.HolyDart;
import com.coladungeon.levels.traps.DisintegrationTrap;
import com.coladungeon.levels.traps.GrimTrap;
import com.coladungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

import java.util.HashSet;

public class AntiMagic extends Armor.Glyph {

	private static ItemSprite.Glowing TEAL = new ItemSprite.Glowing( 0x88EEFF );
	
	public static final HashSet<Class> RESISTS = new HashSet<>();
	static {
		RESISTS.add( MagicalSleep.class );
		RESISTS.add( Charm.class );
		RESISTS.add( Weakness.class );
		RESISTS.add( Vulnerable.class );
		RESISTS.add( Hex.class );
		RESISTS.add( Degrade.class );
		
		RESISTS.add( DisintegrationTrap.class );
		RESISTS.add( GrimTrap.class );

		RESISTS.add( ArcaneBomb.class );
		RESISTS.add( HolyBomb.HolyDamage.class );
		RESISTS.add( ScrollOfRetribution.class );
		RESISTS.add( ScrollOfPsionicBlast.class );
		RESISTS.add( ScrollOfTeleportation.class );
		RESISTS.add( HolyDart.class );

		RESISTS.add( GuidingLight.class );
		RESISTS.add( HolyWeapon.class );
		RESISTS.add( Sunray.class );
		RESISTS.add( HolyLance.class );
		RESISTS.add( Smite.class );
		RESISTS.add( Judgement.class );

		RESISTS.add( ElementalBlast.class );
		RESISTS.add( CursedWand.class );
		RESISTS.add( WandOfBlastWave.class );
		RESISTS.add( WandOfDisintegration.class );
		RESISTS.add( WandOfFireblast.class );
		RESISTS.add( WandOfFrost.class );
		RESISTS.add( WandOfLightning.class );
		RESISTS.add( WandOfLivingEarth.class );
		RESISTS.add( WandOfMagicMissile.class );
		RESISTS.add( WandOfPrismaticLight.class );
		RESISTS.add( WandOfTransfusion.class );
		RESISTS.add( WandOfWarding.Ward.class );

		RESISTS.add( ElementalStrike.class );
		RESISTS.add( Blazing.class );
		RESISTS.add( WandOfFireblast.FireBlastOnHit.class );
		RESISTS.add( Shocking.class );
		RESISTS.add( WandOfLightning.LightningOnHit.class );
		RESISTS.add( Grim.class );

		RESISTS.add( WarpBeacon.class );
		
		RESISTS.add( DM100.LightningBolt.class );
		RESISTS.add( Shaman.EarthenBolt.class );
		RESISTS.add( CrystalWisp.LightBeam.class );
		RESISTS.add( Warlock.DarkBolt.class );
		RESISTS.add( Eye.DeathGaze.class );
		RESISTS.add( YogFist.BrightFist.LightBeam.class );
		RESISTS.add( YogFist.DarkFist.DarkBolt.class );
	}
	
	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {
		//no proc effect, triggers in Char.damage
		return damage;
	}
	
	public static int drRoll( Char owner, int level ){
		if (level == -1){
			return 0;
		} else {
			return Random.NormalIntRange(
					Math.round(level * genericProcChanceMultiplier(owner)),
					Math.round((3 + (level * 1.5f)) * genericProcChanceMultiplier(owner)));
		}
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return TEAL;
	}

}