package io.iworkflow.core.utils;

import io.iworkflow.core.TypeMapsStore;
import io.iworkflow.core.communication.ChannelType;

import java.util.Map;
import java.util.Optional;

public class ChannelUtils {
    public static Class<?> getChannelType(
            final String name,
            final ChannelType channelType,
            final TypeMapsStore typeMapsStore
    ) {
        final Map<String, Class<?>> nameToTypeMap = typeMapsStore.getNameToTypeStore();
        final Map<String, Class<?>> prefixToTypeMap = typeMapsStore.getPrefixToTypeStore();

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
                        "%s channel not registered: %s",
                        channelType.toString(),
                        name
                )
        );
    }
}
