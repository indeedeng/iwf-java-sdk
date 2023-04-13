package io.iworkflow.core;

import io.iworkflow.gen.models.IDReusePolicy;
import io.iworkflow.gen.models.WorkflowConfig;
import io.iworkflow.gen.models.WorkflowRetryPolicy;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

@Value.Immutable
public abstract class WorkflowOptions {
    public abstract Optional<IDReusePolicy> getWorkflowIdReusePolicy();

    public abstract Optional<String> getCronSchedule();

    public abstract Optional<WorkflowRetryPolicy> getWorkflowRetryPolicy();

    public abstract Map<String, Object> getInitialSearchAttribute();

    public abstract Optional<WorkflowConfig> getWorkflowConfigOverride();
}
