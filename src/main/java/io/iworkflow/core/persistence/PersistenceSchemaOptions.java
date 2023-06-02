package io.iworkflow.core.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class PersistenceSchemaOptions {
    // this option will enable storing data attributes as memo, which works like a caching
    // the RPC/GetDataAttributes API will support a much higher throughput
    // However:
    // 1. The read after write will become eventual consistent, unless set strongConsistencyReadWithCaching=true in RPC annotation
    // 2. Only iWF service with Temporal as backend is supporting this feature at the moment
    // 3. It will extra cost as it will upsertMemo(WorkflowPropertiesModified event in the history) for write
    public abstract boolean getUsingMemoForCachingDataAttributes();

    public static PersistenceSchemaOptions getDefault() {
        return ImmutablePersistenceSchemaOptions.builder()
                .usingMemoForCachingDataAttributes(false)
                .build();
    }

    public static ImmutablePersistenceSchemaOptions.Builder builder() {
        return ImmutablePersistenceSchemaOptions.builder();
    }
}
