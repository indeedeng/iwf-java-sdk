package io.iworkflow.core;

import io.iworkflow.gen.models.IDReusePolicy;
import io.iworkflow.gen.models.WorkflowConfig;
import io.iworkflow.gen.models.WorkflowRetryPolicy;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

@Value.Immutable
public abstract class ObjectOptions {
    public abstract Optional<IDReusePolicy> getObjectIdReusePolicy();

    public abstract Optional<String> getCronSchedule();

    public abstract Optional<WorkflowRetryPolicy> getObjectExecutionRetryPolicy();

    public abstract Map<String, Object> getInitialSearchAttribute();

    public abstract Optional<WorkflowConfig> getObjectConfigOverride();
}
