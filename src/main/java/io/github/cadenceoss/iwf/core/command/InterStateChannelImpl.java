package io.github.cadenceoss.iwf.core.command;

import io.github.cadenceoss.iwf.core.ObjectEncoder;
import io.github.cadenceoss.iwf.core.WorkflowDefinitionException;
import io.github.cadenceoss.iwf.gen.models.EncodedObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterStateChannelImpl implements InterStateChannel {

    final Map<String, Class<?>> nameToTypeMap;
    final Map<String, List<EncodedObject>> toPublish = new HashMap<>();

    final ObjectEncoder objectEncoder;

    public InterStateChannelImpl(
            final Map<String, Class<?>> nameToTypeMap,
            final ObjectEncoder objectEncoder) {
        this.nameToTypeMap = nameToTypeMap;
        this.objectEncoder = objectEncoder;
    }

    @Override
    public void publish(final String channelName, final Object value) {
        final Class<?> type = nameToTypeMap.get(channelName);
        if (!type.isInstance(value)) {
            throw new WorkflowDefinitionException(String.format("InterStateChannel value is not of type %s", type.getName()));
        }
        final List<EncodedObject> publish = toPublish.computeIfAbsent(channelName, s -> new ArrayList<>());
        publish.add(objectEncoder.encode(value));
    }

    public Map<String, List<EncodedObject>> getToPublish() {
        return toPublish;
    }
}
