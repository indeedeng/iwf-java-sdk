package io.iworkflow.core.options;

public enum WorkflowIdReusePolicy {
    ALLOW_DUPLICATE_FAILED_ONLY,
    ALLOW_DUPLICATE,
    REJECT_DUPLICATE,
    TERMINATE_IF_RUNNING
}
