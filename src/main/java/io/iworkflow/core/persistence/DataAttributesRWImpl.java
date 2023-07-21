package io.iworkflow.core.persistence;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.TypeMapsStore;
import io.iworkflow.core.utils.DataAttributeUtils;
import io.iworkflow.gen.models.EncodedObject;
import io.iworkflow.gen.models.KeyValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataAttributesRWImpl implements DataAttributesRW {
    private final TypeMapsStore typeMapsStore;
    private final Map<String, EncodedObject> keyToEncodedObjectMap;
    private final Map<String, EncodedObject> toReturnToServer;
    private final ObjectEncoder objectEncoder;

    public DataAttributesRWImpl(
            final TypeMapsStore typeMapsStore,
            final Map<String, EncodedObject> keyToValueMap,
            final ObjectEncoder objectEncoder) {
        this.typeMapsStore = typeMapsStore;
        this.keyToEncodedObjectMap = keyToValueMap;
        this.toReturnToServer = new HashMap<>();
        this.objectEncoder = objectEncoder;
    }

    @Override
    public <T> T getDataAttribute(String key, Class<T> type) {
        if (!DataAttributeUtils.isValidDataAttributeKey(key, typeMapsStore)) {
            throw new IllegalArgumentException(String.format("data attribute %s is not registered", key));
        }
        if (!keyToEncodedObjectMap.containsKey(key)) {
            return null;
        }

        final Class<?> registeredType = DataAttributeUtils.getDataAttributeType(key, typeMapsStore);
        if (!type.isAssignableFrom(registeredType)) {
            throw new IllegalArgumentException(
                    String.format(
                            "registered type %s is not assignable from %s",
                            registeredType.getName(),
                            type.getName()));
        }

        return type.cast(
                objectEncoder.decode(keyToEncodedObjectMap.get(key), registeredType));
    }

    @Override
    public void setDataAttribute(String key, Object value) {
        if (!DataAttributeUtils.isValidDataAttributeKey(key, typeMapsStore)) {
            throw new IllegalArgumentException(String.format("data attribute %s is not registered", key));
        }

        final Class<?> registeredType = DataAttributeUtils.getDataAttributeType(key, typeMapsStore);
        if (value != null && !registeredType.isAssignableFrom(value.getClass())) {
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
