package io.iworkflow.core.mapper;

import io.iworkflow.core.ObjectEncoder;
import io.iworkflow.core.Registry;
import io.iworkflow.core.StateDef;
import io.iworkflow.core.WorkflowStateOptions;
import io.iworkflow.core.WorkflowDefinitionException;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.gen.models.ExecuteApiFailurePolicy;
import io.iworkflow.gen.models.RetryPolicy;
import io.iworkflow.gen.models.StateMovement;
import io.iworkflow.gen.models.WaitUntilApiFailurePolicy;

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
            io.iworkflow.gen.models.WorkflowStateOptions stateOptions;
            if (stateMovement.getStateOptionsOverride().isPresent()) {
                // Always deep copy the state options so we don't modify the original
                stateOptions = toIdlWorkflowStateOptions(stateMovement.getStateOptionsOverride().get().clone());
            } else {
                stateOptions = validateAndGetIdlStateOptions(stateDef);
            }

            if (shouldSkipWaitUntil(stateDef.getWorkflowState())) {
                if (stateOptions == null) {
                    stateOptions = new io.iworkflow.gen.models.WorkflowStateOptions();
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

    public static void autoFillFailureProceedingStateOptions(io.iworkflow.gen.models.WorkflowStateOptions stateOptions, final String workflowType, final Registry registry) {
        if (stateOptions == null) {
            return;
        }
        if (stateOptions.getExecuteApiFailurePolicy() == ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE
                && stateOptions.getExecuteApiFailureProceedStateOptions() == null) {

            // fill the state options for the proceeding state
            String proceedStateId = stateOptions.getExecuteApiFailureProceedStateId();
            final StateDef proceedStatDef = registry.getWorkflowState(workflowType, proceedStateId);
            io.iworkflow.gen.models.WorkflowStateOptions proceedStateOptions = validateAndGetIdlStateOptions(proceedStatDef);
            if (proceedStateOptions != null &&
                    proceedStateOptions.getExecuteApiFailurePolicy() == ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE) {
                throw new WorkflowDefinitionException("nested failure handling is not supported. You cannot set a failure proceeding state on top of another failure proceeding state.");
            }

            if (shouldSkipWaitUntil(proceedStatDef.getWorkflowState())) {
                if (proceedStateOptions == null) {
                    proceedStateOptions = new io.iworkflow.gen.models.WorkflowStateOptions().skipWaitUntil(true);
                } else {
                    proceedStateOptions.skipWaitUntil(true);
                }
            }

            stateOptions.executeApiFailureProceedStateOptions(proceedStateOptions);
        }
    }

    public static io.iworkflow.gen.models.WorkflowStateOptions validateAndGetIdlStateOptions(
            final StateDef stateDef) {
        final WorkflowState state = stateDef.getWorkflowState();
        WorkflowStateOptions stateOptions = state.getStateOptions();
        if (stateOptions == null){
            return null;
        }
        // Always deep copy the state options so we don't modify the original
        stateOptions = stateOptions.clone();
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
        if(Boolean.FALSE.equals(stateOptions.getProceedToExecuteWhenWaitUntilRetryExhausted())){
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
        return toIdlWorkflowStateOptions(stateOptions);
    }

    public static io.iworkflow.gen.models.WorkflowStateOptions toIdlWorkflowStateOptions(
            WorkflowStateOptions workflowStateOptions) {
        if (workflowStateOptions == null) {
            return null;
        }

        final io.iworkflow.gen.models.WorkflowStateOptions idlWorkflowStateOptions =
                new io.iworkflow.gen.models.WorkflowStateOptions();

        idlWorkflowStateOptions.setSearchAttributesLoadingPolicy(workflowStateOptions.getSearchAttributesLoadingPolicy());
        idlWorkflowStateOptions.setWaitUntilApiSearchAttributesLoadingPolicy(workflowStateOptions.getWaitUntilApiSearchAttributesLoadingPolicy());
        idlWorkflowStateOptions.setExecuteApiSearchAttributesLoadingPolicy(workflowStateOptions.getExecuteApiSearchAttributesLoadingPolicy());
        idlWorkflowStateOptions.setDataAttributesLoadingPolicy(workflowStateOptions.getDataAttributesLoadingPolicy());
        idlWorkflowStateOptions.setWaitUntilApiDataAttributesLoadingPolicy(workflowStateOptions.getWaitUntilApiDataAttributesLoadingPolicy());
        idlWorkflowStateOptions.setExecuteApiDataAttributesLoadingPolicy(workflowStateOptions.getExecuteApiDataAttributesLoadingPolicy());
        idlWorkflowStateOptions.setWaitUntilApiTimeoutSeconds(workflowStateOptions.getWaitUntilApiTimeoutSeconds());
        idlWorkflowStateOptions.setExecuteApiTimeoutSeconds(workflowStateOptions.getExecuteApiTimeoutSeconds());
        idlWorkflowStateOptions.setWaitUntilApiRetryPolicy(workflowStateOptions.getWaitUntilApiRetryPolicy());
        idlWorkflowStateOptions.setExecuteApiRetryPolicy(workflowStateOptions.getExecuteApiRetryPolicy());
        idlWorkflowStateOptions.setWaitUntilApiFailurePolicy(Boolean.TRUE.equals(workflowStateOptions.getProceedToExecuteWhenWaitUntilRetryExhausted())
                ? WaitUntilApiFailurePolicy.PROCEED_ON_FAILURE
                : WaitUntilApiFailurePolicy.FAIL_WORKFLOW_ON_FAILURE);
        idlWorkflowStateOptions.setExecuteApiFailurePolicy(workflowStateOptions.getExecuteApiFailurePolicy());
        if (workflowStateOptions.getProceedToStateWhenExecuteRetryExhausted() != null) {
            idlWorkflowStateOptions.setExecuteApiFailureProceedStateId(workflowStateOptions.getProceedToStateWhenExecuteRetryExhausted()
                    .getSimpleName());
        }
        if (workflowStateOptions.getProceedToStateWhenExecuteRetryExhaustedStateOptions() != null) {
            idlWorkflowStateOptions.setExecuteApiFailureProceedStateOptions(toIdlWorkflowStateOptions(workflowStateOptions.getProceedToStateWhenExecuteRetryExhaustedStateOptions()));
        }

        return idlWorkflowStateOptions;
    }
}
