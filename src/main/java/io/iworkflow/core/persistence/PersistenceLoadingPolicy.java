package io.iworkflow.core.persistence;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class PersistenceLoadingPolicy {

    public abstract PersistenceLoadingType getAttributeLoadingType();

    public abstract List<String> getPartialLoadingKeys();

    public static final PersistenceLoadingPolicy LoadAllWithoutLocking = ImmutablePersistenceLoadingPolicy.builder().build();
}
