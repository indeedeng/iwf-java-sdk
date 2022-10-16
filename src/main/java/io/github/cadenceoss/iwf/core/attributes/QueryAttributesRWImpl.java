package io.github.cadenceoss.iwf.core.attributes;

import io.github.cadenceoss.iwf.core.ObjectEncoder;
import io.github.cadenceoss.iwf.gen.models.EncodedObject;
import io.github.cadenceoss.iwf.gen.models.KeyValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryAttributesRWImpl implements QueryAttributesRW{
    private final Map<String, Class<?>> queryAttributeNameToTypeMap;
    private final Map<String, EncodedObject> queryAttributeNameToEncodedObjectMap;
    private final Map<String, EncodedObject> queryAttributesToUpsert;
    private final ObjectEncoder objectEncoder;

    public QueryAttributesRWImpl(
            final Map<String, Class<?>> queryAttributeNameToTypeMap,
            final Map<String, EncodedObject> queryAttributeNameToValueMap,
            final ObjectEncoder objectEncoder) {
        this.queryAttributeNameToTypeMap = queryAttributeNameToTypeMap;
        this.queryAttributeNameToEncodedObjectMap = queryAttributeNameToValueMap;
        this.queryAttributesToUpsert = new HashMap<>();
        this.objectEncoder = objectEncoder;
    }

    @Override
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

    @Override
    public void set(String key, Object value) {
        if (!queryAttributeNameToTypeMap.containsKey(key)) {
            throw new IllegalArgumentException(String.format("Query attribute %s is not registered", key));
        }

        Class<?> registeredType = queryAttributeNameToTypeMap.get(key);
        if (!registeredType.isInstance(value)) {
            throw new IllegalArgumentException(String.format("Input is not an instance of class %s", registeredType.getName()));
        }

        this.queryAttributeNameToEncodedObjectMap.put(key, objectEncoder.encode(value));
        this.queryAttributesToUpsert.put(key, objectEncoder.encode(value));
    }

    public List<KeyValue> getUpsertQueryAttributes() {
        return queryAttributesToUpsert.entrySet().stream()
                .map(stringEncodedObjectEntry ->
                        new KeyValue()
                                .key(stringEncodedObjectEntry.getKey())
                                .value(stringEncodedObjectEntry.getValue()))
                .collect(Collectors.toList());
    }
}
