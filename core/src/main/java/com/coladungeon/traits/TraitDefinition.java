package com.coladungeon.traits;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Describes a trait type that can be dynamically declared at runtime.
 * A definition contains:
 * - a unique id
 * - an optional attribute schema (attribute name -> expected type hint)
 *
 * Attribute type hints are advisory and not strictly enforced at runtime,
 * but can be used by editors/tools for validation.
 */
public final class TraitDefinition {

	private final String id;
	private final Map<String, Class<?>> attributeSchema;

	// Optional metadata that can be leveraged by game systems
	// Using class names to avoid hard dependencies when serializing definitions if needed
	private final Set<String> defaultResistanceClassNames;
	private final Set<String> defaultImmunityClassNames;

	private TraitDefinition(Builder builder) {
		this.id = builder.id;
		this.attributeSchema = Collections.unmodifiableMap(new LinkedHashMap<>(builder.attributeSchema));
		this.defaultResistanceClassNames = Collections.unmodifiableSet(new LinkedHashSet<>(builder.defaultResistanceClassNames));
		this.defaultImmunityClassNames = Collections.unmodifiableSet(new LinkedHashSet<>(builder.defaultImmunityClassNames));
	}

	public String id() {
		return id;
	}

	public Map<String, Class<?>> attributeSchema() {
		return attributeSchema;
	}

	public Set<String> defaultResistanceClassNames() {
		return defaultResistanceClassNames;
	}

	public Set<String> defaultImmunityClassNames() {
		return defaultImmunityClassNames;
	}

	public static Builder define(String id) {
		return new Builder(id);
	}

	public static final class Builder {
		private final String id;
		private final Map<String, Class<?>> attributeSchema = new LinkedHashMap<>();
		private final Set<String> defaultResistanceClassNames = new LinkedHashSet<>();
		private final Set<String> defaultImmunityClassNames = new LinkedHashSet<>();

		private Builder(String id) {
			if (id == null || id.isEmpty()) {
				throw new IllegalArgumentException("TraitDefinition id must be non-empty");
			}
			this.id = id;
		}

		public Builder attribute(String name, Class<?> typeHint) {
			if (name == null || name.isEmpty()) return this;
			attributeSchema.put(name, typeHint);
			return this;
		}

		public Builder defaultResistance(Class<?> effectClass) {
			if (effectClass != null) {
				defaultResistanceClassNames.add(effectClass.getName());
			}
			return this;
		}

		public Builder defaultImmunity(Class<?> effectClass) {
			if (effectClass != null) {
				defaultImmunityClassNames.add(effectClass.getName());
			}
			return this;
		}

		public TraitDefinition build() {
			return new TraitDefinition(this);
		}
	}
}


