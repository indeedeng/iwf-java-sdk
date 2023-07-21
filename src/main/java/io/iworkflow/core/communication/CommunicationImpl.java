package io.iworkflow.core.communication;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.StateMovement;
import io.iworkflow.core.WorkflowDefinitionException;
import io.iworkflow.core.utils.InternalChannelUtils;
import io.iworkflow.gen.models.EncodedObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunicationImpl implements Communication {

    final Map<String, Class<?>> nameToTypeMap;
    final Map<String, Class<?>> prefixToTypeMap;
    final Map<String, List<EncodedObject>> toPublish = new HashMap<>();

    final List<StateMovement> stateMovements = new ArrayList<>();

    final boolean allowTriggerStateMovements;
    final ObjectEncoder objectEncoder;

    public CommunicationImpl(
            final Map<String, Class<?>> nameToTypeMap,
            final Map<String, Class<?>> prefixToTypeMap,
            final ObjectEncoder objectEncoder,
            final boolean allowTriggerStateMovements) {
        this.nameToTypeMap = nameToTypeMap;
        this.prefixToTypeMap = prefixToTypeMap;
        this.objectEncoder = objectEncoder;
        this.allowTriggerStateMovements = allowTriggerStateMovements;
    }

    @Override
    public void publishInternalChannel(final String channelName, final Object value) {
        final Class<?> type = InternalChannelUtils.getInternalChannelType(
                channelName,
                nameToTypeMap,
                prefixToTypeMap
        );
        if (value != null && !type.isInstance(value)) {
            throw new WorkflowDefinitionException(String.format("InternalChannel value is not of type %s", type.getName()));
        }
        final List<EncodedObject> publish = toPublish.computeIfAbsent(channelName, s -> new ArrayList<>());
        publish.add(objectEncoder.encode(value));
    }

    @Override
    public void triggerStateMovements(final StateMovement... stateMovements) {
        if (!allowTriggerStateMovements) {
            throw new WorkflowDefinitionException("triggerStateMovements is not allowed to be used here");
        }
        this.stateMovements.addAll(Arrays.asList(stateMovements));
    }

    public Map<String, List<EncodedObject>> getToPublishInternalChannels() {
        return toPublish;
    }

    public List<StateMovement> getStateMovements() {
        return this.stateMovements;
    }
}
