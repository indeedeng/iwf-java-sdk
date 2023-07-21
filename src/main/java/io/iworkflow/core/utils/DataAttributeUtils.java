package io.iworkflow.core.utils;

import io.iworkflow.core.TypeMapsStore;

import java.util.Map;
import java.util.Optional;

public class DataAttributeUtils {
    public static Boolean isValidDataAttributeKey(
            final String key,
            final TypeMapsStore typeMapsStore) {

        final Map<String, Class<?>> keyToTypeMap = typeMapsStore.getNameToTypeStore();
        final Map<String, Class<?>> prefixToTypeMap = typeMapsStore.getPrefixToTypeStore();

        if (keyToTypeMap.containsKey(key)) {
            return true;
        }

        final Optional<String> first = prefixToTypeMap.keySet().stream()
                .filter(key::startsWith)
                .findFirst();

        return first.isPresent();
    }

    public static Class<?> getDataAttributeType(
            final String key,
            final TypeMapsStore typeMapsStore) {

        final Map<String, Class<?>> keyToTypeMap = typeMapsStore.getNameToTypeStore();
        final Map<String, Class<?>> prefixToTypeMap = typeMapsStore.getPrefixToTypeStore();

        if (keyToTypeMap.containsKey(key)) {
            return keyToTypeMap.get(key);
        }

        final Optional<String> first = prefixToTypeMap.keySet().stream()
                .filter(key::startsWith)
                .findFirst();

        if (first.isPresent()) {
            return prefixToTypeMap.get(first.get());
        }

        throw new IllegalArgumentException(
                String.format(
                        "Data attribute not registered: %s",
                        key
                )
        );
    }
}
