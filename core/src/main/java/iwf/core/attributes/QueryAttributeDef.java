package iwf.core.attributes;

import org.immutables.value.Value;

@Value.Immutable
public abstract class QueryAttributeDef<T> {
    public abstract Class<T> getQueryAttributeType();

    public abstract String getQueryAttributeKey();
}
