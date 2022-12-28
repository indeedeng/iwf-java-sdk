package io.iworkflow.core.persistence;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.gen.models.EncodedObject;
import io.iworkflow.gen.models.KeyValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataObjectsRWImpl implements DataObjectsRW {
    private final Map<String, Class<?>> keyToTypeMap;
    private final Map<String, EncodedObject> keyToEncodedObjectMap;
    private final Map<String, EncodedObject> toReturnToServer;
    private final ObjectEncoder objectEncoder;

    public DataObjectsRWImpl(
            final Map<String, Class<?>> keyToTypeMap,
            final Map<String, EncodedObject> keyToValueMap,
            final ObjectEncoder objectEncoder) {
        this.keyToTypeMap = keyToTypeMap;
        this.keyToEncodedObjectMap = keyToValueMap;
        this.toReturnToServer = new HashMap<>();
        this.objectEncoder = objectEncoder;
    }

    @Override
    public <T> T getDataObject(String key, Class<T> type) {
        if (!keyToTypeMap.containsKey(key)) {
            throw new IllegalArgumentException(String.format("data object %s is not registered", key));
        }
        if (!keyToEncodedObjectMap.containsKey(key)) {
            return null;
        }

        Class<?> registeredType = keyToTypeMap.get(key);
        if (!type.isAssignableFrom(registeredType)) {
            throw new IllegalArgumentException(
                    String.format(
                            "%s is not assignable from registered type %s",
                            type.getName(),
                            registeredType.getName()));
        }

        return type.cast(
                objectEncoder.decode(keyToEncodedObjectMap.get(key), registeredType));
    }

    @Override
    public void setDataObject(String key, Object value) {
        if (!keyToTypeMap.containsKey(key)) {
            throw new IllegalArgumentException(String.format("data object %s is not registered", key));
        }

        Class<?> registeredType = keyToTypeMap.get(key);
        if (!registeredType.isInstance(value)) {
            throw new IllegalArgumentException(String.format("Input is not an instance of class %s", registeredType.getName()));
        }

        this.keyToEncodedObjectMap.put(key, objectEncoder.encode(value));
        this.toReturnToServer.put(key, objectEncoder.encode(value));
    }

    public List<KeyValue> getToReturnToServer() {
        return toReturnToServer.entrySet().stream()
                .map(stringEncodedObjectEntry ->
                        new KeyValue()
                                .key(stringEncodedObjectEntry.getKey())
                                .value(stringEncodedObjectEntry.getValue()))
                .collect(Collectors.toList());
    }
}
