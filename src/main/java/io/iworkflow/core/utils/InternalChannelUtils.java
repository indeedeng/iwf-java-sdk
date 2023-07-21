package io.iworkflow.core.utils;

import java.util.Map;
import java.util.Optional;

public class InternalChannelUtils {
    public static Class<?> getInternalChannelType(
            final String name,
            final Map<String, Class<?>> nameToTypeMap,
            final Map<String, Class<?>> prefixToTypeMap) {

        if (nameToTypeMap.containsKey(name)) {
            return nameToTypeMap.get(name);
        }

        final Optional<String> first = prefixToTypeMap.keySet().stream()
                .filter(name::startsWith)
                .findFirst();

        if (first.isPresent()) {
            return prefixToTypeMap.get(first.get());
        }

        throw new IllegalArgumentException(
                String.format(
                        "InternalChannel not registered: %s",
                        name
                )
        );
    }
}
