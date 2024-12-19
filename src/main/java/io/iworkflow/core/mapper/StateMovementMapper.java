package io.iworkflow.core.mapper;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.Registry;
import io.iworkflow.core.StateDef;
import io.iworkflow.core.WorkflowDefinitionException;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.gen.models.ExecuteApiFailurePolicy;
import io.iworkflow.gen.models.RetryPolicy;
import io.iworkflow.gen.models.StateMovement;
import io.iworkflow.gen.models.WaitUntilApiFailurePolicy;
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
            WorkflowStateOptions stateOptions;
            if (stateMovement.getStateOptionsOverride().isPresent()) {
                // Always deep copy the state options so we don't modify the original
                stateOptions = toIdlWorkflowStateOptions(stateMovement.getStateOptionsOverride().get());
            } else {
                stateOptions = validateAndGetIdlStateOptions(stateDef);
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

            stateMovement.getWaitForKey().ifPresent(movement::waitForKey);
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
            String proceedStateId = stateOptions.getExecuteApiFailureProceedStateId();
            final StateDef proceedStatDef = registry.getWorkflowState(workflowType, proceedStateId);
            WorkflowStateOptions proceedStateOptions = validateAndGetIdlStateOptions(proceedStatDef);
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

    public static WorkflowStateOptions validateAndGetIdlStateOptions(final StateDef stateDef) {
        final WorkflowState state = stateDef.getWorkflowState();
        if (state.getStateOptions() == null) {
            return null;
        }

        // Convert to IDL WorkflowStateOptions so we don't modify the original
        final WorkflowStateOptions stateOptions = toIdlWorkflowStateOptions(state.getStateOptions());

        // Validate required fields if Execute failure policy is configured to proceed
        if(stateOptions.getExecuteApiFailurePolicy() == ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE){
            // retry policy must be set
            if(stateOptions.getExecuteApiRetryPolicy() == null){
                throw new WorkflowDefinitionException("RetryPolicy must be set for the execute "+state.getStateId());
            }
            final RetryPolicy policy = stateOptions.getExecuteApiRetryPolicy();
            // either maximumAttempts or maximumAttemptsDurationSeconds must be set and greater than zero
            if(policy.getMaximumAttempts() == null && policy.getMaximumAttemptsDurationSeconds() == null){
                throw new WorkflowDefinitionException("Either maximumAttempts or maximumAttemptsDurationSeconds must be set for the execute "+state.getStateId());
            }
        }

        // Validate required fields if Wait Until failure policy is configured to proceed
        if(stateOptions.getWaitUntilApiFailurePolicy() == WaitUntilApiFailurePolicy.PROCEED_ON_FAILURE){
            // retry policy must be set
            if(stateOptions.getWaitUntilApiRetryPolicy() == null){
                throw new WorkflowDefinitionException("RetryPolicy must be set for the waitUntil "+state.getStateId());
            }
            final RetryPolicy policy = stateOptions.getWaitUntilApiRetryPolicy();
            // either maximumAttempts or maximumAttemptsDurationSeconds must be set and greater than zero
            if(policy.getMaximumAttempts() == null && policy.getMaximumAttemptsDurationSeconds() == null){
                throw new WorkflowDefinitionException("Either maximumAttempts or maximumAttemptsDurationSeconds must be set for the waitUntil "+state.getStateId());
            }
        }

        return stateOptions;
    }

    public static WorkflowStateOptions toIdlWorkflowStateOptions(io.iworkflow.core.WorkflowStateOptions stateOptions) {
        if (stateOptions == null) {
            return null;
        }

        // Guarantee workflow state options copy is not holding references to the original by cloning object
        stateOptions = stateOptions.clone();

        final WorkflowStateOptions idlWorkflowStateOptions = new WorkflowStateOptions();

        idlWorkflowStateOptions.setSearchAttributesLoadingPolicy(stateOptions.getSearchAttributesLoadingPolicy());
        idlWorkflowStateOptions.setWaitUntilApiSearchAttributesLoadingPolicy(stateOptions.getWaitUntilApiSearchAttributesLoadingPolicy());
        idlWorkflowStateOptions.setExecuteApiSearchAttributesLoadingPolicy(stateOptions.getExecuteApiSearchAttributesLoadingPolicy());
        idlWorkflowStateOptions.setDataAttributesLoadingPolicy(stateOptions.getDataAttributesLoadingPolicy());
        idlWorkflowStateOptions.setWaitUntilApiDataAttributesLoadingPolicy(stateOptions.getWaitUntilApiDataAttributesLoadingPolicy());
        idlWorkflowStateOptions.setExecuteApiDataAttributesLoadingPolicy(stateOptions.getExecuteApiDataAttributesLoadingPolicy());
        idlWorkflowStateOptions.setWaitUntilApiTimeoutSeconds(stateOptions.getWaitUntilApiTimeoutSeconds());
        idlWorkflowStateOptions.setExecuteApiTimeoutSeconds(stateOptions.getExecuteApiTimeoutSeconds());
        idlWorkflowStateOptions.setWaitUntilApiRetryPolicy(stateOptions.getWaitUntilApiRetryPolicy());
        idlWorkflowStateOptions.setExecuteApiRetryPolicy(stateOptions.getExecuteApiRetryPolicy());
        if (stateOptions.getProceedToExecuteWhenWaitUntilRetryExhausted() != null) {
            idlWorkflowStateOptions.setWaitUntilApiFailurePolicy(Boolean.TRUE.equals(stateOptions.getProceedToExecuteWhenWaitUntilRetryExhausted())
                    ? WaitUntilApiFailurePolicy.PROCEED_ON_FAILURE
                    : WaitUntilApiFailurePolicy.FAIL_WORKFLOW_ON_FAILURE);
        }
        if (stateOptions.getProceedToStateWhenExecuteRetryExhausted() != null) {
            idlWorkflowStateOptions.setExecuteApiFailurePolicy(ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE);
            idlWorkflowStateOptions.setExecuteApiFailureProceedStateId(stateOptions.getProceedToStateWhenExecuteRetryExhausted()
                    .getSimpleName());
        }
        if (stateOptions.getProceedToStateWhenExecuteRetryExhaustedStateOptions() != null) {
            idlWorkflowStateOptions.setExecuteApiFailureProceedStateOptions(toIdlWorkflowStateOptions(stateOptions.getProceedToStateWhenExecuteRetryExhaustedStateOptions()));
        }

        return idlWorkflowStateOptions;
    }
}
