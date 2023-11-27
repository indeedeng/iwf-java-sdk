package io.iworkflow.core.mapper;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.Registry;
import io.iworkflow.core.StateDef;
import io.iworkflow.core.WorkflowDefinitionException;
import io.iworkflow.gen.models.ExecuteApiFailurePolicy;
import io.iworkflow.gen.models.StateMovement;
import io.iworkflow.gen.models.WorkflowStateOptions;

import static io.iworkflow.core.StateMovement.RESERVED_STATE_ID_PREFIX;
import static io.iworkflow.core.WorkflowState.shouldSkipWaitUntil;

public class StateMovementMapper {

    public static StateMovement toGenerated(final io.iworkflow.core.StateMovement stateMovement, final String workflowType, final Registry registry, final ObjectEncoder objectEncoder) {
        final Object input = stateMovement.getStateInput().orElse(null);
        final StateMovement movement = new StateMovement()
                .stateId(stateMovement.getStateId())
                .stateInput(objectEncoder.encode(input));
        if (!stateMovement.getStateId().startsWith(RESERVED_STATE_ID_PREFIX)) {
            final StateDef stateDef = registry.getWorkflowState(workflowType, stateMovement.getStateId());
            if(stateDef == null){
                throw new IllegalArgumentException("state "+stateMovement.getStateId() +" is not registered in the workflow "+workflowType);
            }

            // Try to get the overrode stateOptions, if it's null, get the stateOptions from stateDef
            WorkflowStateOptions stateOptions = stateMovement.getStateOptionsOverride().orElse(null);
            if (stateOptions == null) {
                stateOptions = stateDef.getWorkflowState().getStateOptions();
            }

            if (shouldSkipWaitUntil(stateDef.getWorkflowState())) {
                if (stateOptions == null) {
                    stateOptions = new WorkflowStateOptions();
                }

                stateOptions.skipWaitUntil(true);
            }

            autoFillFailureProceedingStateOptions(stateOptions, workflowType, registry);

            if (stateOptions != null) {
                movement.stateOptions(stateOptions);
            }
        }
        return movement;
    }

    public static void autoFillFailureProceedingStateOptions(WorkflowStateOptions stateOptions, final String workflowType, final Registry registry) {
        if (stateOptions == null) {
            return;
        }
        if (stateOptions.getExecuteApiFailurePolicy() == ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE
                && stateOptions.getExecuteApiFailureProceedStateOptions() == null) {

            // fill the state options for the proceeding state
            String stateId = stateOptions.getExecuteApiFailureProceedStateId();
            final StateDef proceedStatDef = registry.getWorkflowState(workflowType, stateId);
            WorkflowStateOptions proceedStateOptions = proceedStatDef.getWorkflowState().getStateOptions();
            if (proceedStateOptions != null &&
                    proceedStateOptions.getExecuteApiFailurePolicy() == ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE) {
                throw new WorkflowDefinitionException("nested failure handling is not supported. You cannot set a failure proceeding state on top of another failure proceeding state.");
            }

            if (shouldSkipWaitUntil(proceedStatDef.getWorkflowState())) {
                if (proceedStateOptions == null) {
                    proceedStateOptions = new WorkflowStateOptions().skipWaitUntil(true);
                } else {
                    proceedStateOptions.skipWaitUntil(true);
                }
            }

            stateOptions.executeApiFailureProceedStateOptions(proceedStateOptions);
        }
    }
}
