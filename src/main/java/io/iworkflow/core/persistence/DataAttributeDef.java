package io.iworkflow.core.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class DataAttributeDef implements PersistenceFieldDef {
    public abstract Class getDataAttributeType();
    public abstract Boolean isPrefix();

    /**
     * iWF will verify if the key has been registered for the data attribute created using this method,
     * allowing users to create only one data attribute with the same key and data type.
     *
     * @param dataType  required.
     * @param key       required. The unique key.
     * @return a data attribute definition
     */
    public static DataAttributeDef create(final Class dataType, final String key) {
        return ImmutableDataAttributeDef.builder()
                .key(key)
                .dataAttributeType(dataType)
                .isPrefix(false)
                .build();
    }

    /**
     * iWF now supports dynamically created data attributes with a shared prefix and the same data type.
     * (E.g., dynamically created data attributes of type String can be named with a common prefix like: data_attribute_prefix_1: "one", data_attribute_prefix_2: "two")
     * iWF will verify if the prefix has been registered for data attributes created using this method,
     * allowing users to create multiple data attributes with the same prefix and data type.
     *
     * @param dataType      required.
     * @param keyPrefix     required. The common prefix of a set of keys to be created later.
     * @return a data attribute definition
     */
    public static DataAttributeDef createByPrefix(final Class dataType, final String keyPrefix) {
        return ImmutableDataAttributeDef.builder()
                .key(keyPrefix)
                .dataAttributeType(dataType)
                .isPrefix(true)
                .build();
    }
}
