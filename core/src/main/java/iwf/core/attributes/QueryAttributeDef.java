package iwf.core.attributes;

import org.immutables.value.Value;

@Value.Immutable
public interface QueryAttributeDef<T> {
    Class<T> getQueryAttributeType();

    String getQueryAttributeKey();
}
