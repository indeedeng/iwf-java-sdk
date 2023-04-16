package io.iworkflow.core;

import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
public abstract class ObjectSearchResponse {
    public abstract List<ObjectExecution> getObjectExecutions();

    public abstract Optional<String> getNextPageToken();
}

