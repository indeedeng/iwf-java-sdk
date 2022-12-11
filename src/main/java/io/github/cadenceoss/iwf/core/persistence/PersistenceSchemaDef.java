package io.github.cadenceoss.iwf.core.persistence;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class PersistenceSchemaDef {
    public abstract List<PersistenceField> getPersistenceFields();

    public static PersistenceSchemaDef create(PersistenceField... fields) {
        return ImmutablePersistenceSchemaDef.builder()
                .addPersistenceFields(fields)
                .build();
    }
}
