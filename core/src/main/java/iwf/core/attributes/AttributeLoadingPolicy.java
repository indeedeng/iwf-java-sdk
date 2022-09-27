package iwf.core.attributes;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface AttributeLoadingPolicy {

    AttributeLoadingType getAttributeLoadingType();

    List<String> getAttributeKeys();

    AttributeLoadingPolicy LoadAllWithoutLocking = ImmutableAttributeLoadingPolicy.builder().build();
}
