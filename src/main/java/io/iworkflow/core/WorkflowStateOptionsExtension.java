package io.iworkflow.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.iworkflow.gen.models.ExecuteApiFailurePolicy;
import io.iworkflow.gen.models.WorkflowStateOptions;

// WorkflowStateOptionsExtension provides extension to WorkflowStateOptions
// to make it easier to build
public class WorkflowStateOptionsExtension extends WorkflowStateOptions {

    public WorkflowStateOptionsExtension setProceedOnExecuteFailure(final Class<? extends WorkflowState> proceedingState) {
        this.executeApiFailurePolicy(ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE);
        this.executeApiFailureProceedStateId(proceedingState.getSimpleName());
        return this;
    }

    public WorkflowStateOptionsExtension setProceedOnExecuteFailure(final Class<? extends WorkflowState> proceedingState, WorkflowStateOptions stateOptions) {
        this.executeApiFailurePolicy(ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE);
        this.executeApiFailureProceedStateId(proceedingState.getSimpleName());
        this.executeApiFailureProceedStateOptions(stateOptions);
        return this;
    }

    /**
     * Uses JSON serialization to deep copy WorkflowStateOptions
     * @param stateOptions the state options to deep copy.
     * @return the newly created copy.
     */
    public static WorkflowStateOptions deepCopyStateOptions(WorkflowStateOptions stateOptions) {
        if (stateOptions == null) {
            return null;
        }

        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(objectMapper.writeValueAsString(stateOptions), WorkflowStateOptions.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
