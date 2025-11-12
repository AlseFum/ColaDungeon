package com.coladungeon.traits;

import com.coladungeon.actors.Char;
import com.coladungeon.actors.blobs.Electricity;
import com.coladungeon.actors.buffs.*;
import com.coladungeon.actors.mobs.Elemental;
import com.coladungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.coladungeon.items.scrolls.ScrollOfRetribution;
import com.coladungeon.items.wands.WandOfFireblast;
import com.coladungeon.items.wands.WandOfFrost;
import com.coladungeon.items.wands.WandOfLightning;
import com.coladungeon.items.weapon.enchantments.Blazing;
import com.coladungeon.items.weapon.missiles.darts.ShockingDart;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Bridges between legacy Char.Property and new trait ids,
 * and registers built-in trait definitions corresponding to those properties.
 *
 * This enables incremental migration: old property checks continue to work,
 * while new code can use traits directly.
 */
public final class PropertyTraitBridge {

	private static final Map<String, Char.Property> TRAIT_TO_PROPERTY = new HashMap<>();

	static {
		// Register built-in traits that correspond to old properties
		registerPropertyTrait("boss", Char.Property.BOSS,
				setOf(Grim.class, com.coladungeon.levels.traps.GrimTrap.class, ScrollOfRetribution.class, ScrollOfPsionicBlast.class),
				setOf(AllyBuff.class, Dread.class));

		registerPropertyTrait("miniboss", Char.Property.MINIBOSS,
				setOf(), setOf(AllyBuff.class, Dread.class));

		registerPropertyTrait("boss_minion", Char.Property.BOSS_MINION, setOf(), setOf());
		registerPropertyTrait("undead", Char.Property.UNDEAD, setOf(), setOf());
		registerPropertyTrait("demonic", Char.Property.DEMONIC, setOf(), setOf());

		registerPropertyTrait("inorganic", Char.Property.INORGANIC,
				setOf(), setOf(Bleeding.class, com.coladungeon.actors.blobs.ToxicGas.class, Poison.class));

		registerPropertyTrait("fiery", Char.Property.FIERY,
				setOf(WandOfFireblast.class, Elemental.FireElemental.class),
				setOf(Burning.class, Blazing.class));

		registerPropertyTrait("icy", Char.Property.ICY,
				setOf(WandOfFrost.class, Elemental.FrostElemental.class),
				setOf(Frost.class, Chill.class));

		registerPropertyTrait("acidic", Char.Property.ACIDIC,
				setOf(Corrosion.class),
				setOf(Ooze.class));

		registerPropertyTrait("electric", Char.Property.ELECTRIC,
				setOf(WandOfLightning.class, com.coladungeon.actors.buffs.Shocking.class, Potential.class,
						Electricity.class, ShockingDart.class, Elemental.ShockElemental.class),
				setOf());

		registerPropertyTrait("large", Char.Property.LARGE, setOf(), setOf());

		registerPropertyTrait("immovable", Char.Property.IMMOVABLE,
				setOf(), setOf(Vertigo.class));

		registerPropertyTrait("static", Char.Property.STATIC,
				setOf(),
				setOf(AllyBuff.class, Dread.class, Terror.class, Amok.class, Charm.class, Sleep.class,
						Paralysis.class, Frost.class, Chill.class, Slow.class, Speed.class));
	}

	private PropertyTraitBridge() {}

	private static <T> Set<Class<?>> setOf(Class<?>... classes) {
		HashSet<Class<?>> s = new HashSet<>();
		for (Class<?> c : classes) s.add(c);
		return s;
	}

	private static void registerPropertyTrait(String traitId, Char.Property property,
	                                         Set<Class<?>> defaultResists, Set<Class<?>> defaultImmunes) {
		TRAIT_TO_PROPERTY.put(traitId, property);

		TraitDefinition.Builder b = TraitDefinition.define(traitId);
		for (Class<?> c : defaultResists) b.defaultResistance(c);
		for (Class<?> c : defaultImmunes) b.defaultImmunity(c);
		// Register with registry for dynamic availability
		TraitRegistry.registerOrUpdate(b.build());
	}

	public static Char.Property propertyForTrait(String traitId) {
		return TRAIT_TO_PROPERTY.get(traitId);
	}
}


