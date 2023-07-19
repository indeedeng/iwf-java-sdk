package io.iworkflow.core.utils;

import java.util.Map;
import java.util.Optional;

public class DataAttributeUtils {
    public static Boolean isValidDataAttributeKey(
            final String key,
            final Map<String, Class<?>> dataAttributeKeyToTypeMap,
            final Map<String, Class<?>> dataAttributePrefixToTypeMap) {

        if (dataAttributeKeyToTypeMap.containsKey(key)) {
            return true;
        }

        final Optional<String> first = dataAttributePrefixToTypeMap.keySet().stream()
                .filter(key::startsWith)
                .findFirst();

        return first.isPresent();
    }

    public static Class<?> getDataAttributeType(
            final String key,
            final Map<String, Class<?>> dataAttributeKeyToTypeMap,
            final Map<String, Class<?>> dataAttributePrefixToTypeMap) {

        if (dataAttributeKeyToTypeMap.containsKey(key)) {
            return dataAttributeKeyToTypeMap.get(key);
        }

        final Optional<String> first = dataAttributePrefixToTypeMap.keySet().stream()
                .filter(key::startsWith)
                .findFirst();

        if (first.isPresent()) {
            return dataAttributePrefixToTypeMap.get(first.get());
        }

        throw new IllegalArgumentException(
                String.format(
                        "Data attribute not registered: %s",
                        key
                )
        );
    }
}
