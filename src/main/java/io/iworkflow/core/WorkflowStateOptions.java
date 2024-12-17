package io.iworkflow.core;

import io.iworkflow.gen.models.ExecuteApiFailurePolicy;
import io.iworkflow.gen.models.PersistenceLoadingPolicy;
import io.iworkflow.gen.models.RetryPolicy;
import io.iworkflow.gen.models.WaitUntilApiFailurePolicy;

import java.util.Objects;

public class WorkflowStateOptions {
    private PersistenceLoadingPolicy searchAttributesLoadingPolicy;

    private PersistenceLoadingPolicy waitUntilApiSearchAttributesLoadingPolicy;

    private PersistenceLoadingPolicy executeApiSearchAttributesLoadingPolicy;

    private PersistenceLoadingPolicy dataAttributesLoadingPolicy;

    private PersistenceLoadingPolicy waitUntilApiDataAttributesLoadingPolicy;

    private PersistenceLoadingPolicy executeApiDataAttributesLoadingPolicy;

    private Integer waitUntilApiTimeoutSeconds;

    private Integer executeApiTimeoutSeconds;

    private RetryPolicy waitUntilApiRetryPolicy;

    private RetryPolicy executeApiRetryPolicy;

    private WaitUntilApiFailurePolicy waitUntilApiFailurePolicy;

    private ExecuteApiFailurePolicy executeApiFailurePolicy;

    private WorkflowStateOptions executeApiFailureProceedStateOptions;

    public WorkflowStateOptions() {
    }

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

    public WorkflowStateOptions waitUntilApiFailurePolicy(WaitUntilApiFailurePolicy waitUntilApiFailurePolicy) {
        setWaitUntilApiFailurePolicy(waitUntilApiFailurePolicy);
        return this;
    }

    public WaitUntilApiFailurePolicy getWaitUntilApiFailurePolicy() {
        return waitUntilApiFailurePolicy;
    }

    public void setWaitUntilApiFailurePolicy(WaitUntilApiFailurePolicy waitUntilApiFailurePolicy) {
        this.waitUntilApiFailurePolicy = waitUntilApiFailurePolicy;
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

    public WorkflowStateOptions executeApiFailureProceedStateOptions(WorkflowStateOptions executeApiFailureProceedStateOptions) {
        setExecuteApiFailureProceedStateOptions(executeApiFailureProceedStateOptions);
        return this;
    }

    public WorkflowStateOptions getExecuteApiFailureProceedStateOptions() {
        return executeApiFailureProceedStateOptions;
    }

    public void setExecuteApiFailureProceedStateOptions(WorkflowStateOptions executeApiFailureProceedStateOptions) {
        this.executeApiFailureProceedStateOptions = executeApiFailureProceedStateOptions;
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
                && Objects.equals(this.waitUntilApiFailurePolicy, workflowStateOptions.waitUntilApiFailurePolicy)
                && Objects.equals(this.executeApiFailurePolicy, workflowStateOptions.executeApiFailurePolicy)
                && Objects.equals(
                this.executeApiFailureProceedStateOptions,
                workflowStateOptions.executeApiFailureProceedStateOptions);
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
                waitUntilApiFailurePolicy,
                executeApiFailurePolicy,
                executeApiFailureProceedStateOptions);
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
        sb.append("    waitUntilApiFailurePolicy: ").append(toIndentedString(waitUntilApiFailurePolicy)).append("\n");
        sb.append("    executeApiFailurePolicy: ").append(toIndentedString(executeApiFailurePolicy)).append("\n");
        sb.append("    executeApiFailureProceedStateOptions: ")
                .append(toIndentedString(executeApiFailureProceedStateOptions))
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
}
