package io.iworkflow.core;

import io.iworkflow.gen.models.ExecuteApiFailurePolicy;
import io.iworkflow.gen.models.WaitUntilApiFailurePolicy;
import io.iworkflow.gen.models.WorkflowStateOptions;

/**
 * WorkflowStateOptionsExtension provides extension to WorkflowStateOptions
 * to make it easier to build some fields of the stateOptions.
 * This is also because WorkflowState interface uses WorkflowStateOptions
 * directly instead of using a separate model.
 * See <a href="https://github.com/indeedeng/iwf-java-sdk/issues/200">TODO</a>
 */
public class WorkflowStateOptionsExtension extends WorkflowStateOptions {

    /**
     * Set the state to proceed to the specified state after the execute API exhausted all retries
     * This is useful for some advanced use cases like SAGA pattern.
     * RetryPolicy is required to be set with maximumAttempts or maximumAttemptsDurationSeconds for execute API.
     * See more in <a href="https://github.com/indeedeng/iwf/wiki/WorkflowStateOptions">wiki</a>
     * @param proceedingState the state to proceed to
     * @return this
     */
    public WorkflowStateOptionsExtension setProceedAfterRetryExhaustedOnExecute(
            final Class<? extends WorkflowState> proceedingState) {
        return this.setProceedOnExecuteFailure(proceedingState);
    }

    /**
     * Set the state to proceed to the specified state after the execute API exhausted all retries
     * This is useful for some advanced use cases like SAGA pattern.
     * RetryPolicy is required to be set with maximumAttempts or maximumAttemptsDurationSeconds for execute API.
     * See more in <a href="https://github.com/indeedeng/iwf/wiki/WorkflowStateOptions">wiki</a>
     * @param proceedingState the state to proceed to
     * @param stateOptionsOverride the stateOptions for the proceeding state. This is for a rare case that you
     *                             need to override the stateOptions returned from state instance.
     * @return this
     */
    public WorkflowStateOptionsExtension setProceedAfterRetryExhaustedOnExecute(
            final Class<? extends WorkflowState> proceedingState, WorkflowStateOptions stateOptionsOverride) {
        return this.setProceedOnExecuteFailure(proceedingState, stateOptionsOverride);
    }

    /**
     * If set to true, then after waitUntil API exhausted all retries, proceed to the execute API
     * This is useful for some advanced use cases like SAGA pattern.
     * RetryPolicy is required to be set with maximumAttempts or maximumAttemptsDurationSeconds for waitUntil API.
     * See more in <a href="https://github.com/indeedeng/iwf/wiki/WorkflowStateOptions">wiki</a>
     * @param proceed true to proceed
     * @return this
     */
    public WorkflowStateOptionsExtension setProceedAfterRetryExhaustedOnWaitUntil(boolean proceed){
        if(proceed){
            this.waitUntilApiFailurePolicy(WaitUntilApiFailurePolicy.PROCEED_ON_FAILURE);
        }else{
            this.waitUntilApiFailurePolicy(WaitUntilApiFailurePolicy.FAIL_WORKFLOW_ON_FAILURE);
        }

        return this;
    }

    /**
     * Use setProceedAfterRetryExhaustedOnExecuteFailure instead.
     * It's a renaming for better clarity.
     */
    @Deprecated
    public WorkflowStateOptionsExtension setProceedOnExecuteFailure(final Class<? extends WorkflowState> proceedingState) {
        this.executeApiFailurePolicy(ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE);
        this.executeApiFailureProceedStateId(proceedingState.getSimpleName());
        return this;
    }

    /**
     * Use setProceedAfterRetryExhaustedOnExecuteFailure instead
     * It's a renaming for better clarity.
     */
    @Deprecated
    public WorkflowStateOptionsExtension setProceedOnExecuteFailure(final Class<? extends WorkflowState> proceedingState, WorkflowStateOptions stateOptionsOverride) {
        this.executeApiFailurePolicy(ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE);
        this.executeApiFailureProceedStateId(proceedingState.getSimpleName());
        this.executeApiFailureProceedStateOptions(stateOptionsOverride);
        return this;
    }
}
