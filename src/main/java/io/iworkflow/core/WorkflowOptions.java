package io.iworkflow.core;

import io.iworkflow.gen.models.RetryPolicy;
import io.iworkflow.gen.models.WorkflowIDReusePolicy;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

@Value.Immutable
public abstract class WorkflowOptions {
    public abstract Integer getWorkflowTimeoutSeconds();

    public abstract Optional<WorkflowIDReusePolicy> getWorkflowIdReusePolicy();

    public abstract Optional<String> getCronSchedule();

    public abstract Optional<RetryPolicy> getWorkflowRetryPolicy();

    public abstract Optional<Map<String, Object>> getInitialSearchAttribute();
}
