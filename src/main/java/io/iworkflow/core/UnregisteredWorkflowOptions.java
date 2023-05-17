package io.iworkflow.core;

import io.iworkflow.gen.models.IDReusePolicy;
import io.iworkflow.gen.models.SearchAttribute;
import io.iworkflow.gen.models.WorkflowConfig;
import io.iworkflow.gen.models.WorkflowRetryPolicy;
import io.iworkflow.gen.models.WorkflowStateOptions;
import org.immutables.value.Value;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;

@Value.Immutable
public abstract class UnregisteredWorkflowOptions {
    public abstract Optional<IDReusePolicy> getWorkflowIdReusePolicy();

    public abstract Optional<String> getCronSchedule();

    public abstract Optional<WorkflowRetryPolicy> getWorkflowRetryPolicy();

    public abstract Optional<WorkflowStateOptions> getStartStateOptions();

    public abstract List<SearchAttribute> getInitialSearchAttribute();

    public abstract Optional<WorkflowConfig> getWorkflowConfigOverride();
}
