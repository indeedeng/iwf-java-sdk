package io.iworkflow.core;

import io.iworkflow.gen.models.ExecuteApiFailurePolicy;
import io.iworkflow.gen.models.PersistenceLoadingPolicy;
import io.iworkflow.gen.models.RetryPolicy;

import java.util.ArrayList;
import java.util.Objects;

public class WorkflowStateOptions implements Cloneable {
    // Loading policies for search attributes
    private PersistenceLoadingPolicy searchAttributesLoadingPolicy;

    private PersistenceLoadingPolicy waitUntilApiSearchAttributesLoadingPolicy;

    private PersistenceLoadingPolicy executeApiSearchAttributesLoadingPolicy;

    // Loading policies for data attributes
    private PersistenceLoadingPolicy dataAttributesLoadingPolicy;

    private PersistenceLoadingPolicy waitUntilApiDataAttributesLoadingPolicy;

    private PersistenceLoadingPolicy executeApiDataAttributesLoadingPolicy;

    // Wait Until API specific options
    private Integer waitUntilApiTimeoutSeconds;

    private RetryPolicy waitUntilApiRetryPolicy;

    // Execute API specific options
    private Integer executeApiTimeoutSeconds;

    private RetryPolicy executeApiRetryPolicy;

    // By default, workflow would fail after waitUntil API retry exhausted.
    //   This policy is to allow proceeding to the execute API after waitUntil API exhausted all retries.
    //   This is useful for some advanced use cases like SAGA pattern.
    //   RetryPolicy is required to be set with maximumAttempts or maximumAttemptsDurationSeconds for waitUntil API.
    //   NOTE: execute API will use commandResults to check whether the waitUntil has succeeded or not.
    //   See more in <a href="https://github.com/indeedeng/iwf/wiki/WorkflowStateOptions">wiki</a>
    private Boolean proceedToExecuteWhenWaitUntilRetryExhausted;

    // By default, workflow would fail after execute API retry exhausted.
    //   Set the state to proceed to the specified state after the execute API exhausted all retries
    //   This is useful for some advanced use cases like SAGA pattern.
    //   RetryPolicy is required to be set with maximumAttempts or maximumAttemptsDurationSeconds for execute API.
    //   Note that the failure handling state will take the same input as the failed from state.
    private Class<? extends WorkflowState> proceedToStateWhenExecuteRetryExhausted;

    // Policy stating to continue or fail after execute API retry is exhausted
    private ExecuteApiFailurePolicy executeApiFailurePolicy;

    // The state options override to use when proceeding to the configured state after execute API retry is exhausted
    private WorkflowStateOptions proceedToStateWhenExecuteRetryExhaustedStateOptions;

    public WorkflowStateOptions searchAttributesLoadingPolicy(PersistenceLoadingPolicy searchAttributesLoadingPolicy) {
        setSearchAttributesLoadingPolicy(searchAttributesLoadingPolicy);
        return this;
    }

    public PersistenceLoadingPolicy getSearchAttributesLoadingPolicy() {
        return searchAttributesLoadingPolicy;
    }

    public void setSearchAttributesLoadingPolicy(PersistenceLoadingPolicy searchAttributesLoadingPolicy) {
        this.searchAttributesLoadingPolicy = searchAttributesLoadingPolicy;
    }

    public WorkflowStateOptions waitUntilApiSearchAttributesLoadingPolicy(PersistenceLoadingPolicy waitUntilApiSearchAttributesLoadingPolicy) {
        setWaitUntilApiSearchAttributesLoadingPolicy(waitUntilApiSearchAttributesLoadingPolicy);
        return this;
    }

    public PersistenceLoadingPolicy getWaitUntilApiSearchAttributesLoadingPolicy() {
        return waitUntilApiSearchAttributesLoadingPolicy;
    }

    public void setWaitUntilApiSearchAttributesLoadingPolicy(PersistenceLoadingPolicy waitUntilApiSearchAttributesLoadingPolicy) {
        this.waitUntilApiSearchAttributesLoadingPolicy = waitUntilApiSearchAttributesLoadingPolicy;
    }

    public WorkflowStateOptions executeApiSearchAttributesLoadingPolicy(PersistenceLoadingPolicy executeApiSearchAttributesLoadingPolicy) {
        setExecuteApiSearchAttributesLoadingPolicy(executeApiSearchAttributesLoadingPolicy);
        return this;
    }

    public PersistenceLoadingPolicy getExecuteApiSearchAttributesLoadingPolicy() {
        return executeApiSearchAttributesLoadingPolicy;
    }

    public void setExecuteApiSearchAttributesLoadingPolicy(PersistenceLoadingPolicy executeApiSearchAttributesLoadingPolicy) {
        this.executeApiSearchAttributesLoadingPolicy = executeApiSearchAttributesLoadingPolicy;
    }

    public WorkflowStateOptions dataAttributesLoadingPolicy(PersistenceLoadingPolicy dataAttributesLoadingPolicy) {
        setDataAttributesLoadingPolicy(dataAttributesLoadingPolicy);
        return this;
    }

    public PersistenceLoadingPolicy getDataAttributesLoadingPolicy() {
        return dataAttributesLoadingPolicy;
    }

    public void setDataAttributesLoadingPolicy(PersistenceLoadingPolicy dataAttributesLoadingPolicy) {
        this.dataAttributesLoadingPolicy = dataAttributesLoadingPolicy;
    }

    public WorkflowStateOptions waitUntilApiDataAttributesLoadingPolicy(PersistenceLoadingPolicy waitUntilApiDataAttributesLoadingPolicy) {
        setWaitUntilApiDataAttributesLoadingPolicy(waitUntilApiDataAttributesLoadingPolicy);
        return this;
    }

    public PersistenceLoadingPolicy getWaitUntilApiDataAttributesLoadingPolicy() {
        return waitUntilApiDataAttributesLoadingPolicy;
    }

    public void setWaitUntilApiDataAttributesLoadingPolicy(PersistenceLoadingPolicy waitUntilApiDataAttributesLoadingPolicy) {
        this.waitUntilApiDataAttributesLoadingPolicy = waitUntilApiDataAttributesLoadingPolicy;
    }

    public WorkflowStateOptions executeApiDataAttributesLoadingPolicy(PersistenceLoadingPolicy executeApiDataAttributesLoadingPolicy) {
        setExecuteApiDataAttributesLoadingPolicy(executeApiDataAttributesLoadingPolicy);
        return this;
    }

    public PersistenceLoadingPolicy getExecuteApiDataAttributesLoadingPolicy() {
        return executeApiDataAttributesLoadingPolicy;
    }

    public void setExecuteApiDataAttributesLoadingPolicy(PersistenceLoadingPolicy executeApiDataAttributesLoadingPolicy) {
        this.executeApiDataAttributesLoadingPolicy = executeApiDataAttributesLoadingPolicy;
    }

    public WorkflowStateOptions waitUntilApiTimeoutSeconds(Integer waitUntilApiTimeoutSeconds) {
        setWaitUntilApiTimeoutSeconds(waitUntilApiTimeoutSeconds);
        return this;
    }

    public Integer getWaitUntilApiTimeoutSeconds() {
        return waitUntilApiTimeoutSeconds;
    }

    public void setWaitUntilApiTimeoutSeconds(Integer waitUntilApiTimeoutSeconds) {
        this.waitUntilApiTimeoutSeconds = waitUntilApiTimeoutSeconds;
    }

    public WorkflowStateOptions executeApiTimeoutSeconds(Integer executeApiTimeoutSeconds) {
        setExecuteApiTimeoutSeconds(executeApiTimeoutSeconds);
        return this;
    }

    public Integer getExecuteApiTimeoutSeconds() {
        return executeApiTimeoutSeconds;
    }

    public void setExecuteApiTimeoutSeconds(Integer executeApiTimeoutSeconds) {
        this.executeApiTimeoutSeconds = executeApiTimeoutSeconds;
    }

    public WorkflowStateOptions waitUntilApiRetryPolicy(RetryPolicy waitUntilApiRetryPolicy) {
        setWaitUntilApiRetryPolicy(waitUntilApiRetryPolicy);
        return this;
    }

    public RetryPolicy getWaitUntilApiRetryPolicy() {
        return waitUntilApiRetryPolicy;
    }

    public void setWaitUntilApiRetryPolicy(RetryPolicy waitUntilApiRetryPolicy) {
        this.waitUntilApiRetryPolicy = waitUntilApiRetryPolicy;
    }

    public WorkflowStateOptions executeApiRetryPolicy(RetryPolicy executeApiRetryPolicy) {
        setExecuteApiRetryPolicy(executeApiRetryPolicy);
        return this;
    }

    public RetryPolicy getExecuteApiRetryPolicy() {
        return executeApiRetryPolicy;
    }

    public void setExecuteApiRetryPolicy(RetryPolicy executeApiRetryPolicy) {
        this.executeApiRetryPolicy = executeApiRetryPolicy;
    }

    public WorkflowStateOptions proceedToExecuteWhenWaitUntilRetryExhausted(boolean proceed){
        setProceedToExecuteWhenWaitUntilRetryExhausted(proceed);
        return this;
    }

    public Boolean getProceedToExecuteWhenWaitUntilRetryExhausted() {
        return proceedToExecuteWhenWaitUntilRetryExhausted;
    }

    public void setProceedToExecuteWhenWaitUntilRetryExhausted(Boolean proceed) {
        this.proceedToExecuteWhenWaitUntilRetryExhausted =  proceed;
    }

    public WorkflowStateOptions executeApiFailurePolicy(ExecuteApiFailurePolicy executeApiFailurePolicy) {
        setExecuteApiFailurePolicy(executeApiFailurePolicy);
        return this;
    }

    public ExecuteApiFailurePolicy getExecuteApiFailurePolicy() {
        return executeApiFailurePolicy;
    }

    public void setExecuteApiFailurePolicy(ExecuteApiFailurePolicy executeApiFailurePolicy) {
        this.executeApiFailurePolicy = executeApiFailurePolicy;
    }

    public WorkflowStateOptions proceedToStateWhenExecuteRetryExhausted(Class<? extends WorkflowState> proceedToStateWhenExecuteRetryExhausted) {
        setProceedToStateWhenExecuteRetryExhausted(proceedToStateWhenExecuteRetryExhausted);
        return this;
    }

    public Class<? extends WorkflowState> getProceedToStateWhenExecuteRetryExhausted() {
        return proceedToStateWhenExecuteRetryExhausted;
    }

    public void setProceedToStateWhenExecuteRetryExhausted(Class<? extends WorkflowState> proceedToStateWhenExecuteRetryExhausted) {
        setProceedToStateWhenExecuteRetryExhausted(proceedToStateWhenExecuteRetryExhausted, null);
    }

    public void setProceedToStateWhenExecuteRetryExhausted(
            Class<? extends WorkflowState> proceedToStateWhenExecuteRetryExhausted,
            WorkflowStateOptions stateOptionsOverride) {
        if (proceedToStateWhenExecuteRetryExhausted != null) {
            this.executeApiFailurePolicy(ExecuteApiFailurePolicy.PROCEED_TO_CONFIGURED_STATE);
        }
        this.proceedToStateWhenExecuteRetryExhausted = proceedToStateWhenExecuteRetryExhausted;
        this.proceedToStateWhenExecuteRetryExhaustedStateOptions = stateOptionsOverride;
    }

    public WorkflowStateOptions getProceedToStateWhenExecuteRetryExhaustedStateOptions() {
        return proceedToStateWhenExecuteRetryExhaustedStateOptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WorkflowStateOptions workflowStateOptions = (WorkflowStateOptions) o;
        return Objects.equals(this.searchAttributesLoadingPolicy, workflowStateOptions.searchAttributesLoadingPolicy)
                && Objects.equals(
                this.waitUntilApiSearchAttributesLoadingPolicy,
                workflowStateOptions.waitUntilApiSearchAttributesLoadingPolicy)
                && Objects.equals(
                this.executeApiSearchAttributesLoadingPolicy,
                workflowStateOptions.executeApiSearchAttributesLoadingPolicy)
                && Objects.equals(this.dataAttributesLoadingPolicy, workflowStateOptions.dataAttributesLoadingPolicy)
                && Objects.equals(
                this.waitUntilApiDataAttributesLoadingPolicy,
                workflowStateOptions.waitUntilApiDataAttributesLoadingPolicy)
                && Objects.equals(
                this.executeApiDataAttributesLoadingPolicy,
                workflowStateOptions.executeApiDataAttributesLoadingPolicy)
                && Objects.equals(this.waitUntilApiTimeoutSeconds, workflowStateOptions.waitUntilApiTimeoutSeconds)
                && Objects.equals(this.executeApiTimeoutSeconds, workflowStateOptions.executeApiTimeoutSeconds)
                && Objects.equals(this.waitUntilApiRetryPolicy, workflowStateOptions.waitUntilApiRetryPolicy)
                && Objects.equals(this.executeApiRetryPolicy, workflowStateOptions.executeApiRetryPolicy)
                && Objects.equals(this.proceedToExecuteWhenWaitUntilRetryExhausted, workflowStateOptions.proceedToExecuteWhenWaitUntilRetryExhausted)
                && Objects.equals(this.executeApiFailurePolicy, workflowStateOptions.executeApiFailurePolicy)
                && Objects.equals(this.proceedToStateWhenExecuteRetryExhausted, workflowStateOptions.proceedToStateWhenExecuteRetryExhausted)
                && Objects.equals(this.proceedToStateWhenExecuteRetryExhaustedStateOptions, workflowStateOptions.proceedToStateWhenExecuteRetryExhaustedStateOptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                searchAttributesLoadingPolicy,
                waitUntilApiSearchAttributesLoadingPolicy,
                executeApiSearchAttributesLoadingPolicy,
                dataAttributesLoadingPolicy,
                waitUntilApiDataAttributesLoadingPolicy,
                executeApiDataAttributesLoadingPolicy,
                waitUntilApiTimeoutSeconds,
                executeApiTimeoutSeconds,
                waitUntilApiRetryPolicy,
                executeApiRetryPolicy,
                proceedToExecuteWhenWaitUntilRetryExhausted,
                executeApiFailurePolicy,
                proceedToStateWhenExecuteRetryExhausted,
                proceedToStateWhenExecuteRetryExhaustedStateOptions);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class WorkflowStateOptions {\n");
        sb.append("    searchAttributesLoadingPolicy: ").append(toIndentedString(searchAttributesLoadingPolicy)).append("\n");
        sb.append("    waitUntilApiSearchAttributesLoadingPolicy: ")
                .append(toIndentedString(waitUntilApiSearchAttributesLoadingPolicy))
                .append("\n");
        sb.append("    executeApiSearchAttributesLoadingPolicy: ")
                .append(toIndentedString(executeApiSearchAttributesLoadingPolicy))
                .append("\n");
        sb.append("    dataAttributesLoadingPolicy: ").append(toIndentedString(dataAttributesLoadingPolicy)).append("\n");
        sb.append("    waitUntilApiDataAttributesLoadingPolicy: ")
                .append(toIndentedString(waitUntilApiDataAttributesLoadingPolicy))
                .append("\n");
        sb.append("    executeApiDataAttributesLoadingPolicy: ")
                .append(toIndentedString(executeApiDataAttributesLoadingPolicy))
                .append("\n");
        sb.append("    waitUntilApiTimeoutSeconds: ").append(toIndentedString(waitUntilApiTimeoutSeconds)).append("\n");
        sb.append("    executeApiTimeoutSeconds: ").append(toIndentedString(executeApiTimeoutSeconds)).append("\n");
        sb.append("    waitUntilApiRetryPolicy: ").append(toIndentedString(waitUntilApiRetryPolicy)).append("\n");
        sb.append("    executeApiRetryPolicy: ").append(toIndentedString(executeApiRetryPolicy)).append("\n");
        sb.append("    proceedToExecuteWhenWaitUntilRetryExhausted: ").append(toIndentedString(proceedToExecuteWhenWaitUntilRetryExhausted)).append("\n");
        sb.append("    executeApiFailurePolicy: ").append(toIndentedString(executeApiFailurePolicy)).append("\n");
        sb.append("    proceedToStateWhenExecuteRetryExhausted: ")
                .append(toIndentedString(proceedToStateWhenExecuteRetryExhausted))
                .append("\n");
        sb.append("    proceedToStateWhenExecuteRetryExhaustedStateOptions: ")
                .append(toIndentedString(proceedToStateWhenExecuteRetryExhaustedStateOptions))
                .append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    @Override
    public WorkflowStateOptions clone() {
        final WorkflowStateOptions clone = new WorkflowStateOptions();

        clone.setSearchAttributesLoadingPolicy(clone(searchAttributesLoadingPolicy));
        clone.setWaitUntilApiSearchAttributesLoadingPolicy(clone(waitUntilApiSearchAttributesLoadingPolicy));
        clone.setExecuteApiDataAttributesLoadingPolicy(clone(executeApiSearchAttributesLoadingPolicy));
        clone.setDataAttributesLoadingPolicy(clone(dataAttributesLoadingPolicy));
        clone.setWaitUntilApiDataAttributesLoadingPolicy(clone(waitUntilApiDataAttributesLoadingPolicy));
        clone.setExecuteApiDataAttributesLoadingPolicy(clone(executeApiDataAttributesLoadingPolicy));
        clone.setWaitUntilApiTimeoutSeconds(waitUntilApiTimeoutSeconds);
        clone.setExecuteApiTimeoutSeconds(executeApiTimeoutSeconds);
        clone.setWaitUntilApiRetryPolicy(clone(waitUntilApiRetryPolicy));
        clone.setExecuteApiRetryPolicy(clone(executeApiRetryPolicy));
        clone.setProceedToExecuteWhenWaitUntilRetryExhausted(proceedToExecuteWhenWaitUntilRetryExhausted);
        clone.setProceedToStateWhenExecuteRetryExhausted(
                proceedToStateWhenExecuteRetryExhausted,
                proceedToStateWhenExecuteRetryExhaustedStateOptions);
        clone.setExecuteApiFailurePolicy(executeApiFailurePolicy);

        return clone;
    }

    // Perform a deep copy of PersistenceLoadingPolicy
    private PersistenceLoadingPolicy clone(PersistenceLoadingPolicy origPolicy) {
        if (origPolicy == null) {
            return null;
        }
        final PersistenceLoadingPolicy clone = new PersistenceLoadingPolicy();
        clone.setPersistenceLoadingType(origPolicy.getPersistenceLoadingType());
        clone.setPartialLoadingKeys(origPolicy.getPartialLoadingKeys() == null
                ? null
                : new ArrayList<>(origPolicy.getPartialLoadingKeys()));
        clone.setLockingKeys(origPolicy.getLockingKeys() == null ? null : new ArrayList<>(origPolicy.getLockingKeys()));
        clone.setUseKeyAsPrefix(origPolicy.getUseKeyAsPrefix());

        return clone;
    }

    // Perform a deep copy of RetryPolicy
    private RetryPolicy clone(RetryPolicy origPolicy) {
        if (origPolicy == null) {
            return null;
        }
        final RetryPolicy clone = new RetryPolicy();
        clone.setInitialIntervalSeconds(origPolicy.getInitialIntervalSeconds());
        clone.setBackoffCoefficient(origPolicy.getBackoffCoefficient());
        clone.setMaximumIntervalSeconds(origPolicy.getMaximumIntervalSeconds());
        clone.setMaximumAttempts(origPolicy.getMaximumAttempts());
        clone.setMaximumAttemptsDurationSeconds(origPolicy.getMaximumAttemptsDurationSeconds());

        return clone;
    }
}
