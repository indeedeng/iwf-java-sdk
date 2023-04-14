package io.iworkflow.core;

import io.iworkflow.gen.models.PersistenceLoadingType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is for annotating an RPC method for an implementation of {@link ObjectWorkflow}
 * The method must be in the form of one of {@link RpcDefinitions}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RPC {
    int timeoutSeconds() default 0;

    PersistenceLoadingType dataAttributesLoadingType() default PersistenceLoadingType.ALL_WITHOUT_LOCKING;

    // used when dataAttributesLoadingType is PARTIAL_WITHOUT_LOCKING
    String[] dataAttributesPartialLoadingKeys() default {};

    PersistenceLoadingType searchAttributesLoadingType() default PersistenceLoadingType.ALL_WITHOUT_LOCKING;

    // used when searchAttributesPartialLoadingKeys is PARTIAL_WITHOUT_LOCKING
    String[] searchAttributesPartialLoadingKeys() default {};
}