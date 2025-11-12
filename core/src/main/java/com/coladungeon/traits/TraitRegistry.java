package com.coladungeon.traits;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global registry for trait definitions, supports dynamic declaration.
 */
public final class TraitRegistry {

	private static final Map<String, TraitDefinition> DEFINITIONS = new LinkedHashMap<>();

	private TraitRegistry() {}

	public static synchronized void registerOrUpdate(TraitDefinition def) {
		if (def == null) return;
		DEFINITIONS.put(def.id(), def);
	}

	public static synchronized TraitDefinition get(String id) {
		return DEFINITIONS.get(id);
	}

	public static synchronized boolean exists(String id) {
		return DEFINITIONS.containsKey(id);
	}

	public static synchronized Collection<TraitDefinition> all() {
		return Collections.unmodifiableCollection(DEFINITIONS.values());
	}
}


