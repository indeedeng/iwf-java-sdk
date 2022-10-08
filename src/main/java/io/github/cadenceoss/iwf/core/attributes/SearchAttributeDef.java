package io.github.cadenceoss.iwf.core.attributes;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SearchAttributeDef {

    public abstract SearchAttributeType getSearchAttributeType();

    public abstract String getSearchAttributeKey();

    public static SearchAttributeDef create(SearchAttributeType attributeType, String attributeKey) {
        return ImmutableSearchAttributeDef.builder()
                .searchAttributeKey(attributeKey)
                .searchAttributeType(attributeType)
                .build();
    }
}
