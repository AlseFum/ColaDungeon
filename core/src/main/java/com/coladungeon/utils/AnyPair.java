package com.coladungeon.utils;

import java.io.Serializable;
import java.util.Objects;

/**
 * A generic pair class that can hold a class type and its corresponding value.
 * Provides type-safe access and additional utility methods.
 *
 * @param <T> The type of the class
 * @param <V> The type of the value
 */
public record AnyPair<T, V>(Class<T> cls, V value) implements Serializable {
    /**
     * Constructs a new AnyPair with the given class and value.
     * 
     * @param cls The class of the type
     * @param value The value associated with the class
     */
    public AnyPair {
        Objects.requireNonNull(cls, "Class cannot be null");
    }

    /**
     * Checks if the value is an instance of the specified class.
     * 
     * @return true if the value is an instance of the class, false otherwise
     */
    public boolean isValidType() {
        return value == null || cls.isInstance(value);
    }

    /**
     * Safely casts the value to the specified class type.
     * 
     * @return The value cast to the class type
     * @throws ClassCastException if the value cannot be cast to the class type
     */
    @SuppressWarnings("unchecked")
    public T castValue() {
        if (!isValidType()) {
            throw new ClassCastException("Value cannot be cast to " + cls.getName());
        }
        return (T) value;
    }

    /**
     * Creates a new AnyPair with the same class but a different value.
     * 
     * @param newValue The new value to associate with the class
     * @return A new AnyPair with the updated value
     */
    public AnyPair<T, V> withValue(V newValue) {
        return new AnyPair<>(cls, newValue);
    }

    @Override
    public String toString() {
        return String.format("AnyPair[class=%s, value=%s]", cls.getSimpleName(), value);
    }
}