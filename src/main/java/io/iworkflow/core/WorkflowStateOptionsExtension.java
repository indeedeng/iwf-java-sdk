package io.iworkflow.core;

import io.iworkflow.gen.models.ExecuteApiFailurePolicy;
import io.iworkflow.gen.models.WaitUntilApiFailurePolicy;

import java.util.Objects;

/**
 * WorkflowStateOptionsExtension provides extension to WorkflowStateOptions
 * to make it easier to build some fields of the stateOptions.
 * This is also because WorkflowState interface uses WorkflowStateOptions
 * directly instead of using a separate model.
 * See <a href="https://github.com/indeedeng/iwf-java-sdk/issues/200">TODO</a>
 * Example usage in a state implementation:
 *     public WorkflowStateOptions getStateOptions() {
 *         return new WorkflowStateOptionsExtension()
 *                 .setProceedAfterRetryExhaustedOnExecute(StateRecoverBasic.class)
 *                 .executeApiRetryPolicy(
 *                         new RetryPolicy()
 *                                 .maximumAttempts(10)
 *                 );
 *     }
 */
public class WorkflowStateOptionsExtension extends WorkflowStateOptions {
    private static final JacksonJsonObjectEncoder JSON_ENCODER = new JacksonJsonObjectEncoder();

    /**
     * By default, workflow would fail after execute API retry exhausted.
     * Set the state to proceed to the specified state after the execute API exhausted all retries
     * This is useful for some advanced use cases like SAGA pattern.
     * RetryPolicy is required to be set with maximumAttempts or maximumAttemptsDurationSeconds for execute API.
     * NOTE: The proceeding state will take the same input as the failed state that proceeded from.
     * See more in <a href="https://github.com/indeedeng/iwf/wiki/WorkflowStateOptions">wiki</a>
     * @param proceedingWorkflowState the state to proceed to
     * @return this
     */
    public WorkflowStateOptionsExtension setProceedWhenExecuteRetryExhausted(
            Class<? extends WorkflowState> proceedingWorkflowState) {
        setExecuteApiFailurePolicy(ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE);
        setExecuteApiFailureProceedState(proceedingWorkflowState);

        return this;
    }

    /**
     * By default, workflow would fail after waitUntil API retry exhausted.
     * If set to true, then after waitUntil API exhausted all retries, proceed to the execute API
     * This is useful for some advanced use cases like SAGA pattern.
     * RetryPolicy is required to be set with maximumAttempts or maximumAttemptsDurationSeconds for waitUntil API.
     * NOTE: execute API will use commandResults to check whether the waitUntil has succeeded or not.
     * See more in <a href="https://github.com/indeedeng/iwf/wiki/WorkflowStateOptions">wiki</a>
     * @param proceed true to proceed
     * @return this
     */
    public WorkflowStateOptionsExtension setProceedWhenWaitUntilRetryExhausted(boolean proceed) {
        setWaitUntilApiFailurePolicy(proceed
                ? WaitUntilApiFailurePolicy.PROCEED_ON_FAILURE
                : WaitUntilApiFailurePolicy.FAIL_WORKFLOW_ON_FAILURE);

        return this;
    }

    // TODO: This is a workaround due to openapi-generator's "generateBuilders" config not working.
    //  https://openapi-generator.tech/docs/generators/java/#config-options
    //  I have opened a ticket with them (https://github.com/OpenAPITools/openapi-generator/issues/20320).
    /**
     * Uses JSON serialization to deep copy WorkflowStateOptions.
     * @param stateOptions the state options to deep copy.
     * @return the newly created copy.
     */
    public static WorkflowStateOptions deepCopyStateOptions(WorkflowStateOptions stateOptions) {
        final WorkflowStateOptions deepCopy =
                stateOptions == null ? null : JSON_ENCODER.decode(JSON_ENCODER.encode(stateOptions), stateOptions.getClass());

        if (!Objects.deepEquals(stateOptions, deepCopy)) {
            throw new ObjectEncoderException("Deep copy of WorkflowStateOptions did not match.");
        }

        return deepCopy;
    }
}
