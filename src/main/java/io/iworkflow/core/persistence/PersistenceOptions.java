package io.iworkflow.core.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class PersistenceOptions {
    // This option will enable caching persistence (data/search attributes) so that GetDataAttributes and GetSearchAttributes API can
    // support a much higher throughput on a single workflow execution.
    // NOTES:
    // 1. The caching is implemented by Temporal upsertMemo feature. Only iWF service with Temporal as backend supports this feature ATM.
    // 2. It will cost extra action/event on updating data attribute, as iwf-server will upsertMemo(WorkflowPropertiesModified event in the history)
    public abstract boolean getEnableCaching();

    public static PersistenceOptions getDefault() {
        return ImmutablePersistenceOptions.builder()
                .enableCaching(false)
                .build();
    }

    public static ImmutablePersistenceOptions.Builder builder() {
        return ImmutablePersistenceOptions.builder();
    }
}
