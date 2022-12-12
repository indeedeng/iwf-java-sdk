package io.iworkflow.core.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SearchAttributeDef implements PersistenceFieldDef {

    public abstract SearchAttributeType getSearchAttributeType();

    public static SearchAttributeDef create(SearchAttributeType attributeType, String key) {
        return ImmutableSearchAttributeDef.builder()
                .key(key)
                .searchAttributeType(attributeType)
                .build();
    }
}
