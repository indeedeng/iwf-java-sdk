package io.github.cadenceoss.iwf.core.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class DataObjectFieldDef implements PersistenceFieldDef {
    public abstract Class getDataObjectType();

    public static DataObjectFieldDef create(Class dataType, String key) {
        return ImmutableDataObjectFieldDef.builder()
                .key(key)
                .dataObjectType(dataType)
                .build();
    }
}
