package io.iworkflow.core.persistence;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class PersistenceSchema {
    public abstract List<PersistenceFieldDef> getFields();

    public abstract PersistenceSchemaOptions getPersistenceSchemaOptions();

    public static PersistenceSchema empty() {
        return ImmutablePersistenceSchema.builder()
                .persistenceSchemaOptions(PersistenceSchemaOptions.getDefault())
                .build();
    }

    public static PersistenceSchema create(PersistenceFieldDef... fields) {
        return ImmutablePersistenceSchema.builder()
                .addFields(fields)
                .persistenceSchemaOptions(PersistenceSchemaOptions.getDefault())
                .build();
    }

    public static PersistenceSchema create(PersistenceSchemaOptions options, PersistenceFieldDef... fields) {
        return ImmutablePersistenceSchema.builder()
                .addFields(fields)
                .persistenceSchemaOptions(options)
                .build();
    }
}
