package io.github.cadenceoss.iwf.core.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class DataObjectDef implements PersistenceFieldDef {
    public abstract Class getDataObjectType();

    public static DataObjectDef create(Class dataType, String key) {
        return ImmutableDataObjectDef.builder()
                .key(key)
                .dataObjectType(dataType)
                .build();
    }
}
