package io.github.cadenceoss.iwf.core.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SearchAttributeFieldDef implements PersistenceFieldDef {

    public abstract SearchAttributeType getSearchAttributeType();

    public static SearchAttributeFieldDef create(SearchAttributeType attributeType, String key) {
        return ImmutableSearchAttributeFieldDef.builder()
                .key(key)
                .searchAttributeType(attributeType)
                .build();
    }
}
