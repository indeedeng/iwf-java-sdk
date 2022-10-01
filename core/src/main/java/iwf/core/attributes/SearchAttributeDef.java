package iwf.core.attributes;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SearchAttributeDef<T> {

    public abstract Class<T> getSearchAttributeType();

    public abstract String getSearchAttributeKey();
}
