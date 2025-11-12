package com.coladungeon.traits;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Concise static API for declaring and instantiating traits without 'new'.
 *
 * Usage:
 * - Declare/register: Trait.let("Wartorn", "int1", Integer.class, "int2", Integer.class);
 * - Instantiate: Trait.of("Wartorn", "int1", 3, "int2", 7);
 */
public final class Trait {

	private Trait() {}

	/**
	 * Register a trait definition (id and optional schema).
	 * schemaKv is a flat list of key/value pairs: "attrName", Class<?> typeHint
	 */
	public static TraitDefinition let(String id, Object... schemaKv) {
		TraitDefinition.Builder b = TraitDefinition.define(id);
		if (schemaKv != null) {
			for (int i = 0; i + 1 < schemaKv.length; i += 2) {
				Object k = schemaKv[i];
				Object v = schemaKv[i + 1];
				if (!(k instanceof String)) continue;
				if (v instanceof Class<?>) {
					b.attribute((String) k, (Class<?>) v);
				}
			}
		}
		TraitDefinition def = b.build();
		TraitRegistry.registerOrUpdate(def);
		return def;
	}

	/**
	 * Register a pre-built trait definition.
	 */
	public static TraitDefinition let(TraitDefinition def) {
		TraitRegistry.registerOrUpdate(def);
		return def;
	}

	/**
	 * Create a trait instance with attributes via key/value pairs:
	 * - keys must be String
	 * - values can be primitives, String, Class (stored as class name), arrays or collections of those
	 * - special keys "resistances"/"immunities" accept Class, Class[], or Collection<Class<?>>
	 */
	public static TraitInstance of(String id, Object... keyValues) {
		TraitInstance t = new TraitInstance(id);
		if (keyValues == null) return t;
		for (int i = 0; i + 1 < keyValues.length; i += 2) {
			Object k = keyValues[i];
			Object v = keyValues[i + 1];
			if (!(k instanceof String)) continue;
			String key = (String) k;

			if ("resistances".equals(key) || "immunities".equals(key)) {
				Set<String> classNames = toClassNameSet(v);
				if (!classNames.isEmpty()) {
					t.setAttribute(key, classNames);
				}
				continue;
			}

			if (v instanceof Class<?>) {
				t.setAttribute(key, ((Class<?>) v).getName());
			} else if (v instanceof Class<?>[]) {
				Class<?>[] arr = (Class<?>[]) v;
				String[] names = new String[arr.length];
				for (int j = 0; j < arr.length; j++) names[j] = arr[j].getName();
				t.setAttribute(key, names);
			} else if (v instanceof Collection) {
				// Map collection elements similarly
				Collection<?> col = (Collection<?>) v;
				LinkedHashSet<Object> mapped = new LinkedHashSet<>();
				for (Object elem : col) {
					if (elem instanceof Class<?>) mapped.add(((Class<?>) elem).getName());
					else mapped.add(elem);
				}
				t.setAttribute(key, mapped);
			} else {
				t.setAttribute(key, v);
			}
		}
		return t;
	}

	private static LinkedHashSet<String> toClassNameSet(Object v) {
		LinkedHashSet<String> out = new LinkedHashSet<>();
		if (v instanceof Class<?>) {
			out.add(((Class<?>) v).getName());
		} else if (v instanceof Class<?>[]) {
			for (Class<?> c : (Class<?>[]) v) out.add(c.getName());
		} else if (v instanceof Collection) {
			for (Object o : (Collection<?>) v) {
				if (o instanceof Class<?>) out.add(((Class<?>) o).getName());
				else if (o instanceof String) out.add((String) o);
			}
		}
		return out;
	}
}


