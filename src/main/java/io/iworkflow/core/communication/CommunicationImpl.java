package io.iworkflow.core.communication;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.WorkflowDefinitionException;
import io.iworkflow.gen.models.EncodedObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunicationImpl implements Communication {

    final Map<String, Class<?>> nameToTypeMap;
    final Map<String, List<EncodedObject>> toPublish = new HashMap<>();

    final ObjectEncoder objectEncoder;

    public CommunicationImpl(
            final Map<String, Class<?>> nameToTypeMap,
            final ObjectEncoder objectEncoder) {
        this.nameToTypeMap = nameToTypeMap;
        this.objectEncoder = objectEncoder;
    }

    @Override
    public void publishInternalChannel(final String channelName, final Object value) {
        final Class<?> type = nameToTypeMap.get(channelName);
        if (!type.isInstance(value)) {
            throw new WorkflowDefinitionException(String.format("InternalChannel value is not of type %s", type.getName()));
        }
        final List<EncodedObject> publish = toPublish.computeIfAbsent(channelName, s -> new ArrayList<>());
        publish.add(objectEncoder.encode(value));
    }

    public Map<String, List<EncodedObject>> getToPublishInternalChannels() {
        return toPublish;
    }
}
