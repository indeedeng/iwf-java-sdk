package io.github.cadenceoss.iwf.core.attributes;

import org.immutables.value.Value;

@Value.Immutable
public abstract class QueryAttributeDef {
    public abstract Class getQueryAttributeType();

    public abstract String getQueryAttributeKey();

    public static QueryAttributeDef create(Class attributeType, String attributeKey) {
        return ImmutableQueryAttributeDef.builder()
                .queryAttributeKey(attributeKey)
                .queryAttributeType(attributeType)
                .build();
    }
}
