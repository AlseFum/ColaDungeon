package com.coladungeon.traits;

import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Concrete instance of a trait attached to an entity, with attribute values.
 *
 * Serialization notes:
 * - Attributes support primitives and strings directly: Integer, Long, Float, Double, Boolean, String
 * - For collection-like attributes, use simple String sets (e.g., class names for resistances/immunities)
 */
public final class TraitInstance implements Bundlable {

	private static final String KEY_ID = "id";
	private static final String KEY_ATTR = "attrs";

	private String id;
	private final Map<String, Object> attributes = new LinkedHashMap<>();

	// no-arg constructor for bundle restore
	public TraitInstance() {}

	public TraitInstance(String id) {
		if (id == null || id.isEmpty()) {
			throw new IllegalArgumentException("TraitInstance id must be non-empty");
		}
		this.id = id;
	}

	public String id() {
		return id;
	}

	public TraitInstance setAttribute(String key, Object value) {
		if (key != null && !key.isEmpty()) {
			attributes.put(key, value);
		}
		return this;
	}

	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public Map<String, Object> attributes() {
		return Collections.unmodifiableMap(attributes);
	}

	public Set<Class<?>> resistanceClasses() {
		return toClassSet(classNameSetFromAttributes("resistances"));
	}

	public Set<Class<?>> immunityClasses() {
		return toClassSet(classNameSetFromAttributes("immunities"));
	}

	private Set<String> classNameSetFromAttributes(String key) {
		Object value = attributes.get(key);
		if (value instanceof Collection) {
			Set<String> result = new LinkedHashSet<>();
			for (Object v : (Collection<?>) value) {
				if (v instanceof String) result.add((String) v);
			}
			return result;
		}
		return Collections.emptySet();
	}

	private Set<Class<?>> toClassSet(Set<String> classNames) {
		Set<Class<?>> result = new LinkedHashSet<>();
		for (String name : classNames) {
			try {
				Class<?> c = Class.forName(name);
				result.add(c);
			} catch (Throwable ignore) {
				// skip unknown classes
			}
		}
		return result;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(KEY_ID, id);
		Bundle attrs = new Bundle();
		for (Map.Entry<String, Object> e : attributes.entrySet()) {
			Object v = e.getValue();
			if (v instanceof Integer) {
				attrs.put(e.getKey(), (Integer) v);
			} else if (v instanceof Long) {
				attrs.put(e.getKey(), (Long) v);
			} else if (v instanceof Float) {
				attrs.put(e.getKey(), (Float) v);
			} else if (v instanceof Double) {
				attrs.put(e.getKey(), (Double) v);
			} else if (v instanceof Boolean) {
				attrs.put(e.getKey(), (Boolean) v);
			} else if (v instanceof String) {
				attrs.put(e.getKey(), (String) v);
			} else if (v instanceof Collection) {
				// store collections of strings (e.g., class names)
				Collection<?> col = (Collection<?>) v;
				String[] arr = col.stream().filter(o -> o instanceof String).map(o -> (String) o).toArray(String[]::new);
				attrs.put(e.getKey(), arr);
			}
		}
		bundle.put(KEY_ATTR, attrs);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		this.id = bundle.getString(KEY_ID);
		Bundle attrs = bundle.getBundle(KEY_ATTR);
		if (attrs != null && !attrs.isNull()) {
			for (String key : attrs.getKeys()) {
				// Attempt to read supported types; we try arrays as String[]
				if (attrs.contains(key)) {
					Object val = attrs.get(key);
					// val may be an array for collections saved as arrays
					if (val instanceof String[]) {
						LinkedHashSet<String> set = new LinkedHashSet<>();
						Collections.addAll(set, (String[]) val);
						attributes.put(key, set);
					} else {
						attributes.put(key, val);
					}
				}
			}
		}
	}
}


