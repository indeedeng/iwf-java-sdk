package iwf.core;

public class Context {
    private String workflowId;
    private String workflowRunId;
    private String stateExecutionId;
    private Integer workflowStartTimestampSeconds;

    public Context(final String workflowId, final String workflowRunId, final String stateExecutionId, final Integer workflowStartTimestampSeconds) {
        this.workflowId = workflowId;
        this.workflowRunId = workflowRunId;
        this.stateExecutionId = stateExecutionId;
        this.workflowStartTimestampSeconds = workflowStartTimestampSeconds;
    }

    public Integer getWorkflowStartTimestampSeconds() {
        return workflowStartTimestampSeconds;
    }

    public void setWorkflowStartTimestampSeconds(final Integer workflowStartTimestampSeconds) {
        this.workflowStartTimestampSeconds = workflowStartTimestampSeconds;
    }

    public String getStateExecutionId() {
        return stateExecutionId;
    }

    public void setStateExecutionId(final String stateExecutionId) {
        this.stateExecutionId = stateExecutionId;
    }

    public String getWorkflowRunId() {
        return workflowRunId;
    }

    public void setWorkflowRunId(final String workflowRunId) {
        this.workflowRunId = workflowRunId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(final String workflowId) {
        this.workflowId = workflowId;
    }
}
