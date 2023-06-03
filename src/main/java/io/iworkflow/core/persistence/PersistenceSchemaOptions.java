package io.iworkflow.core.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class PersistenceSchemaOptions {
    // This option will enable caching persistence (data/search attributes)
    // The readonly-RPC or GetDataAttributes API will support a much higher throughput
    // NOTES:
    // 1. The read after write will become eventual consistent, unless set bypassCachingForStrongConsistency to true in RPC annotation
    // 2. Only iWF service with Temporal as backend is supporting this feature at the moment
    // 3. It will extra cost as it will upsertMemo(WorkflowPropertiesModified event in the history) for write
    // 4. Only useful for read-only RPC(no persistence.SetXXX API or communication API calls)
    public abstract boolean getCachingPersistenceByMemo();

    public static PersistenceSchemaOptions getDefault() {
        return ImmutablePersistenceSchemaOptions.builder()
                .cachingPersistenceByMemo(false)
                .build();
    }

    public static ImmutablePersistenceSchemaOptions.Builder builder() {
        return ImmutablePersistenceSchemaOptions.builder();
    }
}
