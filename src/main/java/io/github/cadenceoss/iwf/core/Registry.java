package io.github.cadenceoss.iwf.core;

import io.github.cadenceoss.iwf.core.command.SignalChannelDef;
import io.github.cadenceoss.iwf.core.attributes.QueryAttributeDef;

import java.util.HashMap;
import java.util.Map;

public class Registry {
    private final Map<String,Workflow> workflowStore = new HashMap<>();
    // (workflow type, stateId)-> StateDef
    private final Map<String, StateDef> workflowStateStore = new HashMap<>();
    private final Map<String, Map<String, Class<?>>> signalTypeStore = new HashMap<>();
    private final Map<String, Map<String, Class<?>>> queryAttributeTypeStore = new HashMap<>();

    private static final String DELIMITER = "_";

    public void addWorkflow(final Workflow wf){
        registerWorkflow(wf);
        registerWorkflowState(wf);
        registerWorkflowSignal(wf);
        registerWorkflowQueryAttributes(wf);
    }

    private void registerWorkflow(final Workflow wf) {
        String workflowType = wf.getClass().getSimpleName();

        if (workflowStore.containsKey(workflowType)) {
            throw new WorkflowDefinitionException(String.format("Workflow type %s already exists", workflowType));
        }
        workflowStore.put(workflowType, wf);
    }

    private void registerWorkflowState(final Workflow wf) {
        String workflowType = wf.getClass().getSimpleName();
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
        String workflowType = wf.getClass().getSimpleName();
        if (wf.getSignalChannels() == null || wf.getSignalChannels().isEmpty()) {
            signalTypeStore.put(workflowType, new HashMap<>());
            return;
        }

        for (SignalChannelDef signalChannelDef: wf.getSignalChannels()) {
            Map<String, Class<?>> signalNameToTypeMap =
                    signalTypeStore.computeIfAbsent(workflowType, s -> new HashMap<>());
            if (signalNameToTypeMap.containsKey(signalChannelDef.getSignalChannelName())) {
                throw new WorkflowDefinitionException(
                        String.format("Signal channel name  %s already exists", signalChannelDef.getSignalChannelName()));
            }
            signalNameToTypeMap.put(signalChannelDef.getSignalChannelName(), signalChannelDef.getSignalValueType());
        }
    }

    private void registerWorkflowQueryAttributes(final Workflow wf) {
        String workflowType = wf.getClass().getSimpleName();
        if (wf.getQueryAttributes() == null || wf.getQueryAttributes().isEmpty()) {
            queryAttributeTypeStore.put(workflowType, new HashMap<>());
            return;
        }

        for (QueryAttributeDef queryAttributeDef: wf.getQueryAttributes()) {
            Map<String, Class<?>> queryAttributeKeyToTypeMap =
                    queryAttributeTypeStore.computeIfAbsent(workflowType, s -> new HashMap<>());
            if (queryAttributeKeyToTypeMap.containsKey(queryAttributeDef.getQueryAttributeKey())) {
                throw new WorkflowDefinitionException(
                        String.format(
                                "Query attribute key %s already exists",
                                queryAttributeDef.getQueryAttributeKey())
                );
            }
            queryAttributeKeyToTypeMap.put(
                    queryAttributeDef.getQueryAttributeKey(),
                    queryAttributeDef.getQueryAttributeType()
            );
        }
    }

    public Workflow getWorkflow(final String workflowType){
        return workflowStore.get(workflowType);
    }

    public StateDef getWorkflowState(final String workflowType, final String stateId){
        return workflowStateStore.get(getStateDefKey(workflowType, stateId));
    }

    public Map<String, Class<?>> getSignalChannelNameToSignalTypeMap(final String workflowType) {
        return signalTypeStore.get(workflowType);
    }

    public Map<String, Class<?>> getQueryAttributeKeyToTypeMap(final String workflowType) {
        return queryAttributeTypeStore.get(workflowType);
    }

    private String getStateDefKey(final String workflowType, final String stateId) {
        return workflowType + DELIMITER + stateId;
    }
}
