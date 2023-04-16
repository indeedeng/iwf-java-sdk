package io.iworkflow.core;

import io.iworkflow.gen.models.IDReusePolicy;
import io.iworkflow.gen.models.SearchAttribute;
import io.iworkflow.gen.models.WorkflowConfig;
import io.iworkflow.gen.models.WorkflowRetryPolicy;
import io.iworkflow.gen.models.WorkflowStateOptions;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
public abstract class UnregisteredObjectOptions {
    public abstract Optional<IDReusePolicy> getObjectIdReusePolicy();

    public abstract Optional<String> getCronSchedule();

    public abstract Optional<WorkflowRetryPolicy> getObjectExecutionRetryPolicy();

    public abstract Optional<WorkflowStateOptions> getStartStateOptions();

    public abstract List<SearchAttribute> getInitialSearchAttribute();

    public abstract Optional<WorkflowConfig> getObjectConfigOverride();
}
