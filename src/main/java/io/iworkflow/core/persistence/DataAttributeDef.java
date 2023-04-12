package io.iworkflow.core.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class DataAttributeDef implements PersistenceFieldDef {
    public abstract Class getDataAttributeType();

    public static DataAttributeDef create(Class dataType, String key) {
        return ImmutableDataAttributeDef.builder()
                .key(key)
                .dataObjectType(dataType)
                .build();
    }
}
