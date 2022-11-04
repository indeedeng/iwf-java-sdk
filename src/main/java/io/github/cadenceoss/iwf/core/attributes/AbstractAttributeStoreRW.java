package io.github.cadenceoss.iwf.core.attributes;

import io.github.cadenceoss.iwf.core.ObjectEncoder;
import io.github.cadenceoss.iwf.gen.models.EncodedObject;

import java.util.HashMap;
import java.util.Map;

public class AbstractAttributeStoreRW {
    protected final Map<String, Class<?>> queryAttributeNameToTypeMap;
    protected final Map<String, EncodedObject> queryAttributeNameToEncodedObjectMap;
    protected final ObjectEncoder objectEncoder;

    public AbstractAttributeStoreRW(final Map<String, Class<?>> queryAttributeNameToTypeMap,
                                    final Map<String, EncodedObject> queryAttributeNameToValueMap,
                                    final ObjectEncoder objectEncoder) {
        this.queryAttributeNameToTypeMap = queryAttributeNameToTypeMap;
        this.queryAttributeNameToEncodedObjectMap = queryAttributeNameToValueMap;
        this.objectEncoder = objectEncoder;
    }

    public <T> T get(String key, Class<T> type) {
        if (!queryAttributeNameToTypeMap.containsKey(key)) {
            throw new IllegalArgumentException(String.format("Query attribute %s is not registered", key));
        }
        if (!queryAttributeNameToEncodedObjectMap.containsKey(key)) {
            return null;
        }

        Class<?> registeredType = queryAttributeNameToTypeMap.get(key);
        if (!type.isAssignableFrom(registeredType)) {
            throw new IllegalArgumentException(
                    String.format(
                            "%s is not assignable from registered type %s",
                            type.getName(),
                            registeredType.getName()));
        }

        return type.cast(
                objectEncoder.decode(queryAttributeNameToEncodedObjectMap.get(key), registeredType));
    }

    public void set(String key, Object value) {
        if (!queryAttributeNameToTypeMap.containsKey(key)) {
            throw new IllegalArgumentException(String.format("Query attribute %s is not registered", key));
        }

        Class<?> registeredType = queryAttributeNameToTypeMap.get(key);
        if (!registeredType.isInstance(value)) {
            throw new IllegalArgumentException(String.format("Input is not an instance of class %s", registeredType.getName()));
        }

        this.queryAttributeNameToEncodedObjectMap.put(key, objectEncoder.encode(value));
    }
}
