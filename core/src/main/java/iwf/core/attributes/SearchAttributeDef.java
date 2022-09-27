package iwf.core.attributes;

import org.immutables.value.Value;

@Value.Immutable
public interface SearchAttributeDef<T> {

    Class<T> getSearchAttributeType();

    String getSearchAttributeKey();
}
