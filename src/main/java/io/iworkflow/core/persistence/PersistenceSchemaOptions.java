package io.iworkflow.core.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class PersistenceSchemaOptions {
    // this option will enable caching data attributes as memo
    // the RPC/GetDataAttributes API will support a much higher throughput
    // However:
    // 1. The read after write will become eventual consistent, unless set bypassCachingForStrongConsistency=true in RPC annotation
    // 2. Only iWF service with Temporal as backend is supporting this feature at the moment
    // 3. It will extra cost as it will upsertMemo(WorkflowPropertiesModified event in the history) for write
    public abstract boolean getCachingDataAttributesByMemo();

    public static PersistenceSchemaOptions getDefault() {
        return ImmutablePersistenceSchemaOptions.builder()
                .cachingDataAttributesByMemo(false)
                .build();
    }

    public static ImmutablePersistenceSchemaOptions.Builder builder() {
        return ImmutablePersistenceSchemaOptions.builder();
    }
}
