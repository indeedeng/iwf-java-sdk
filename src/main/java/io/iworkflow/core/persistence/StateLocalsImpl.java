package io.iworkflow.core.persistence;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.WorkflowDefinitionException;
import io.iworkflow.gen.models.EncodedObject;
import io.iworkflow.gen.models.KeyValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StateLocalsImpl implements StateLocals {

    private final Map<String, EncodedObject> recordEvents;
    private final Map<String, EncodedObject> attributeNameToEncodedObjectMap;
    private final Map<String, EncodedObject> upsertAttributesToReturnToServer;
    private final ObjectEncoder objectEncoder;

    public StateLocalsImpl(final Map<String, EncodedObject> attributeNameToEncodedObjectMap,
                           final ObjectEncoder objectEncoder) {
        this.objectEncoder = objectEncoder;
        this.attributeNameToEncodedObjectMap = attributeNameToEncodedObjectMap;
        upsertAttributesToReturnToServer = new HashMap<>();
        recordEvents = new HashMap<>();
    }

    @Override
    public void setStateLocal(final String key, final Object value) {
        final EncodedObject encodedData = objectEncoder.encode(value);
        attributeNameToEncodedObjectMap.put(key, encodedData);
        upsertAttributesToReturnToServer.put(key, encodedData);
    }

    @Override
    public <T> T getStateLocal(final String key, final Class<T> type) {
        final EncodedObject encodedData = this.attributeNameToEncodedObjectMap.get(key);
        if (encodedData == null) {
            return null;
        }
        return objectEncoder.decode(encodedData, type);
    }

    @Override
    public void recordStateEvent(final String key, final Object... eventData) {
        if (recordEvents.containsKey(key)) {
            throw new WorkflowDefinitionException("cannot record the same event for more than once");
        }
        if (eventData != null && eventData.length == 1) {
            recordEvents.put(key, objectEncoder.encode(eventData[0]));
        }
        recordEvents.put(key, objectEncoder.encode(eventData));
    }

    public List<KeyValue> getUpsertStateLocalAttributes() {
        return upsertAttributesToReturnToServer.entrySet().stream()
                .map(stringEncodedObjectEntry ->
                        new KeyValue()
                                .key(stringEncodedObjectEntry.getKey())
                                .value(stringEncodedObjectEntry.getValue()))
                .collect(Collectors.toList());
    }

    public List<KeyValue> getRecordEvents() {
        return recordEvents.entrySet().stream()
                .map(stringEncodedObjectEntry ->
                        new KeyValue()
                                .key(stringEncodedObjectEntry.getKey())
                                .value(stringEncodedObjectEntry.getValue()))
                .collect(Collectors.toList());
    }
}
