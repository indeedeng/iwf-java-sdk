package io.iworkflow.core.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class DataAttributeDef implements PersistenceFieldDef {
    public abstract Class getDataAttributeType();
    public abstract Boolean isPrefix();

    public static DataAttributeDef create(final Class dataType, final String key) {
        return ImmutableDataAttributeDef.builder()
                .key(key)
                .dataAttributeType(dataType)
                .isPrefix(false)
                .build();
    }

    public static DataAttributeDef createByPrefix(final Class dataType, final String key) {
        return ImmutableDataAttributeDef.builder()
                .key(key)
                .dataAttributeType(dataType)
                .isPrefix(true)
                .build();
    }
}
