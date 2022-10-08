package io.github.cadenceoss.iwf.core.attributes;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class AttributeLoadingPolicy {

    public abstract AttributeLoadingType getAttributeLoadingType();

    public abstract List<String> getAttributeKeys();

    public static final AttributeLoadingPolicy LoadAllWithoutLocking = ImmutableAttributeLoadingPolicy.builder().build();
}
