package io.iworkflow.core;

import io.iworkflow.core.persistence.PersistenceSchemaOptions;
import io.iworkflow.gen.models.PersistenceLoadingType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is for annotating an RPC method for an implementation of {@link ObjectWorkflow}
 * The method must be in the form of one of {@link RpcDefinitions}
 * An RPC implementation can call any APIs to update external systems directly.
 * However, it can also trigger some state execution (using {@link io.iworkflow.core.communication.Communication} API)
 * to update in the background to ensure the consistency across systems.
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

    /**
     * Only used when workflow has enabled {@link PersistenceSchemaOptions} CachingPersistenceByMemo
     * By default, it's false for high throughput support
     * flip to true to bypass the caching for a strong consistent read
     */
    boolean bypassCachingForStrongConsistency() default false;
}