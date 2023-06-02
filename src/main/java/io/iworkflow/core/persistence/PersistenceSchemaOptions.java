package io.iworkflow.core.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class PersistenceSchemaOptions {
    public abstract boolean getUsingMemoForDataAttributes();

    public static PersistenceSchemaOptions getDefault() {
        return ImmutablePersistenceSchemaOptions.builder()
                .usingMemoForDataAttributes(false)
                .build();
    }

    public static ImmutablePersistenceSchemaOptions.Builder builder() {
        return ImmutablePersistenceSchemaOptions.builder();
    }
}
