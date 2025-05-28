package com.coladungeon.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class BundleAnnotation {
    /**
     * Meta-annotation for methods that store object state in a Bundle
     * Used to mark methods responsible for serializing object state
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface StoreInBundle {
        /**
         * Optional description of what is being stored
         */
        String value() default "";
    }

    /**
     * Meta-annotation for methods that restore object state from a Bundle
     * Used to mark methods responsible for deserializing object state
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface RestoreFromBundle {
        /**
         * Optional description of what is being restored
         */
        String value() default "";
    }

    /**
     * Annotation to automatically handle Bundle serialization for fields
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Bundle {
        /**
         * Type of the field for Bundle serialization
         */
        enum Type {
            INT, 
            STRING, 
            BOOLEAN, 
            FLOAT, 
            LONG, 
            BUNDLABLE, 
            CLASS
        }

        /**
         * Specifies the type of the field for Bundle serialization
         */
        Type type();

        /**
         * Optional key to use when storing in Bundle. 
         * If not specified, uses the field name
         */
        String key() default "";
    }
}
