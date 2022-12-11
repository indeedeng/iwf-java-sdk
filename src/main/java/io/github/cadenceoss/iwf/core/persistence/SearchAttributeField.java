package io.github.cadenceoss.iwf.core.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SearchAttributeField implements PersistenceField {

    public abstract SearchAttributeType getSearchAttributeType();

    public static SearchAttributeField create(SearchAttributeType attributeType, String key) {
        return ImmutableSearchAttributeField.builder()
                .key(key)
                .searchAttributeType(attributeType)
                .build();
    }
}
