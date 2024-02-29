package io.iworkflow.core;

import org.immutables.value.Value;

@Value.Immutable
abstract class RpcMethodMetadata {
    public abstract boolean hasInput();
    public abstract int getInputIndex();
    public abstract boolean usesPersistence();
}
