package io.iworkflow.core.persistence;

import io.iworkflow.gen.models.SearchAttributeValueType;
import org.immutables.value.Value;

@Value.Immutable
public abstract class SearchAttributeDef implements PersistenceFieldDef {

    public abstract SearchAttributeValueType getSearchAttributeType();

    /**
     * The search attribute types are all from Cadence/Temporal
     * See doc https://cadenceworkflow.io/docs/concepts/search-workflows/ and https://docs.temporal.io/concepts/what-is-a-search-attribute/
     * to understand how to register new search attributes and run query
     * NOTE that KEYWORD_ARRAY should be registered as KEYWORD in Cadence/Temporal
     *
     * @param attributeType
     * @param key
     * @return
     */
    public static SearchAttributeDef create(SearchAttributeValueType attributeType, String key) {
        return ImmutableSearchAttributeDef.builder()
                .key(key)
                .searchAttributeType(attributeType)
                .build();
    }
}
