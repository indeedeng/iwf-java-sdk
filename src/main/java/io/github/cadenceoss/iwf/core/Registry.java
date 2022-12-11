package io.github.cadenceoss.iwf.core;

import io.github.cadenceoss.iwf.core.communication.InterStateChannel;
import io.github.cadenceoss.iwf.core.communication.SignalChannel;
import io.github.cadenceoss.iwf.core.persistence.DataObjectField;
import io.github.cadenceoss.iwf.core.persistence.SearchAttributeField;
import io.github.cadenceoss.iwf.core.persistence.SearchAttributeType;

import java.util.HashMap;
import java.util.Map;

public class Registry {
    private final Map<String, Workflow> workflowStore = new HashMap<>();
    // (workflow type, stateId)-> StateDef
    private final Map<String, StateDef> workflowStateStore = new HashMap<>();
    private final Map<String, Map<String, Class<?>>> signalTypeStore = new HashMap<>();

    private final Map<String, Map<String, Class<?>>> interstateChannelTypeStore = new HashMap<>();
    private final Map<String, Map<String, Class<?>>> dataObjectTypeStore = new HashMap<>();

    private final Map<String, Map<String, SearchAttributeType>> searchAttributeTypeStore = new HashMap<>();

    private static final String DELIMITER = "_";

    public void addWorkflow(final Workflow wf) {
        registerWorkflow(wf);
        registerWorkflowState(wf);
        registerWorkflowSignal(wf);
        registerWorkflowInterstateChannel(wf);
        registerWorkflowDataObjects(wf);
        registerWorkflowSearchAttributes(wf);
    }

    public static String getWorkflowType(final Workflow wf) {
        if (wf.getWorkflowType().isEmpty()) {
            return wf.getClass().getSimpleName();
        }
        return wf.getWorkflowType();
    }

    private void registerWorkflow(final Workflow wf) {
        String workflowType = getWorkflowType(wf);

        if (workflowStore.containsKey(workflowType)) {
            throw new WorkflowDefinitionException(String.format("Workflow type %s already exists", workflowType));
        }
        workflowStore.put(workflowType, wf);
    }

    private void registerWorkflowState(final Workflow wf) {
        String workflowType = getWorkflowType(wf);
        if(wf.getStates() == null || wf.getStates().size() == 0){
            throw new WorkflowDefinitionException(String.format("Workflow type %s must contain at least one state", workflowType));
        }
        for (StateDef stateDef: wf.getStates()) {
            String key = getStateDefKey(workflowType, stateDef.getWorkflowState().getStateId());
            if (workflowStateStore.containsKey(key)) {
                throw new WorkflowDefinitionException(String.format("Workflow state definition %s already exists", key));
            } else {
                workflowStateStore.put(key, stateDef);
            }
        }
    }

    private void registerWorkflowSignal(final Workflow wf) {
        String workflowType = getWorkflowType(wf);
        if (wf.getSignalChannels() == null || wf.getSignalChannels().isEmpty()) {
            signalTypeStore.put(workflowType, new HashMap<>());
            return;
        }

        for (SignalChannel signalChannel : wf.getSignalChannels()) {
            Map<String, Class<?>> signalNameToTypeMap =
                    signalTypeStore.computeIfAbsent(workflowType, s -> new HashMap<>());
            if (signalNameToTypeMap.containsKey(signalChannel.getSignalChannelName())) {
                throw new WorkflowDefinitionException(
                        String.format("Signal channel name  %s already exists", signalChannel.getSignalChannelName()));
            }
            signalNameToTypeMap.put(signalChannel.getSignalChannelName(), signalChannel.getSignalValueType());
        }
    }

    private void registerWorkflowInterstateChannel(final Workflow wf) {
        String workflowType = getWorkflowType(wf);
        if (wf.getInterStateChannels() == null || wf.getInterStateChannels().isEmpty()) {
            interstateChannelTypeStore.put(workflowType, new HashMap<>());
            return;
        }

        for (InterStateChannel interstateChannel : wf.getInterStateChannels()) {
            Map<String, Class<?>> nameToTypeMap =
                    interstateChannelTypeStore.computeIfAbsent(workflowType, s -> new HashMap<>());
            if (nameToTypeMap.containsKey(interstateChannel.getChannelName())) {
                throw new WorkflowDefinitionException(
                        String.format("InterStateChannel name  %s already exists", interstateChannel.getChannelName()));
            }
            nameToTypeMap.put(interstateChannel.getChannelName(), interstateChannel.getValueType());
        }
    }

    private void registerWorkflowDataObjects(final Workflow wf) {
        String workflowType = getWorkflowType(wf);
        if (wf.getQueryAttributes() == null || wf.getQueryAttributes().isEmpty()) {
            dataObjectTypeStore.put(workflowType, new HashMap<>());
            return;
        }

        for (DataObjectField dataObjectField : wf.getQueryAttributes()) {
            Map<String, Class<?>> queryAttributeKeyToTypeMap =
                    dataObjectTypeStore.computeIfAbsent(workflowType, s -> new HashMap<>());
            if (queryAttributeKeyToTypeMap.containsKey(dataObjectField.getKey())) {
                throw new WorkflowDefinitionException(
                        String.format(
                                "Query attribute key %s already exists",
                                dataObjectField.getDataObjectType())
                );
            }
            queryAttributeKeyToTypeMap.put(
                    dataObjectField.getKey(),
                    dataObjectField.getDataObjectType()
            );
        }
    }

    private void registerWorkflowSearchAttributes(final Workflow wf) {
        String workflowType = getWorkflowType(wf);
        if (wf.getSearchAttributes() == null || wf.getSearchAttributes().isEmpty()) {
            searchAttributeTypeStore.put(workflowType, new HashMap<>());
            return;
        }

        for (SearchAttributeField searchAttributeField : wf.getSearchAttributes()) {
            Map<String, SearchAttributeType> searchAttributeKeyToTypeMap =
                    searchAttributeTypeStore.computeIfAbsent(workflowType, s -> new HashMap<>());

            if (searchAttributeKeyToTypeMap.containsKey(searchAttributeField.getKey())) {
                throw new WorkflowDefinitionException(
                        String.format(
                                "Search attribute key %s already exists",
                                searchAttributeField.getKey())
                );
            }
            searchAttributeKeyToTypeMap.put(
                    searchAttributeField.getKey(),
                    searchAttributeField.getSearchAttributeType()
            );
        }
    }

    public Workflow getWorkflow(final String workflowType) {
        return workflowStore.get(workflowType);
    }

    public StateDef getWorkflowState(final String workflowType, final String stateId) {
        return workflowStateStore.get(getStateDefKey(workflowType, stateId));
    }

    public Map<String, Class<?>> getSignalChannelNameToSignalTypeMap(final String workflowType) {
        return signalTypeStore.get(workflowType);
    }

    public Map<String, Class<?>> getInterStateChannelNameToTypeMap(final String workflowType) {
        return interstateChannelTypeStore.get(workflowType);
    }

    public Map<String, Class<?>> getQueryAttributeKeyToTypeMap(final String workflowType) {
        return dataObjectTypeStore.get(workflowType);
    }

    public Map<String, SearchAttributeType> getSearchAttributeKeyToTypeMap(final String workflowType) {
        return searchAttributeTypeStore.get(workflowType);
    }

    private String getStateDefKey(final String workflowType, final String stateId) {
        return workflowType + DELIMITER + stateId;
    }
}
