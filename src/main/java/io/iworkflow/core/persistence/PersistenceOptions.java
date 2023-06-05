package io.iworkflow.core.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class PersistenceOptions {
    // This option will enable caching persistence (data/search attributes) so that the readonly-RPC or GetDataAttributes API can
    // support a much higher throughput on a single workflow execution.
    // NOTES:
    // 1. The read after write will become eventual consistent, unless set bypassCachingForStrongConsistency to true in RPC annotation
    // 2. The caching is implemented by Temporal upsertMemo feature. Only iWF service with Temporal as backend is supporting this feature at the moment
    // 3. It will extra cost as it will upsertMemo(WorkflowPropertiesModified event in the history) for write
    // 4. Only useful for read-only RPC(no persistence.SetXXX API or communication API calls)
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
