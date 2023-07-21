package io.iworkflow.core;

import org.immutables.value.Value;

import java.util.Map;

/**
 * This class is used to store both the nameToTypeStore and prefixToTypeStore for the
 * data attribute, signal, and internal channel
 * in registry.
 */

@Value.Immutable
public abstract class TypeMapsStore {
    public abstract Map<String, Class<?>> getNameToTypeStore();
    public abstract Map<String, Class<?>> getPrefixToTypeStore();
}
