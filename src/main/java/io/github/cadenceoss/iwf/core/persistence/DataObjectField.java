package io.github.cadenceoss.iwf.core.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class DataObjectField implements PersistenceField {
    public abstract Class getDataObjectType();

    public static DataObjectField create(Class dataType, String key) {
        return ImmutableDataObjectField.builder()
                .key(key)
                .dataObjectType(dataType)
                .build();
    }
}
