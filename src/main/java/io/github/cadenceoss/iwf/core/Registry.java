package io.github.cadenceoss.iwf.core;

import java.util.HashMap;
import java.util.Map;

public class Registry {
    // workflow type -> workflow
    private final Map<String,Workflow> workflowStore = new HashMap<>();

    // (workflow type, stateId)-> StateDef
    private final Map<String, StateDef> workflowStateStore = new HashMap<>();

    private static final String DELIMITER = "_";

    public void addWorkflow(final Workflow wf){
        String workflowType = wf.getClass().getSimpleName();

        if (workflowStore.containsKey(workflowType)) {
            throw new RuntimeException(String.format("Workflow type %s already exists", workflowType));
        }
        workflowStore.put(workflowType, wf);

        for (StateDef stateDef: wf.getStates()) {
            String key = getStateDefKey(workflowType, stateDef.getWorkflowState().getStateId());
            if (workflowStateStore.containsKey(key)) {
                throw new RuntimeException(String.format("Workflow state definition %s already exists", key));
            } else {
                workflowStateStore.put(key, stateDef);
            }
        }
    }

    public Workflow getWorkflow(final String workflowType){
        return workflowStore.get(workflowType);
    }

    public StateDef getWorkflowState(final String workflowType, final String stateId){
        return workflowStateStore.get(getStateDefKey(workflowType, stateId));
    }

    private String getStateDefKey(final String workflowType, final String stateId) {
        return workflowType + DELIMITER + stateId;
    }
}
