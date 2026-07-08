package io.iworkflow.core.persistence;

import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
public abstract class DataAttributeDef implements PersistenceFieldDef {
    public abstract Class getDataAttributeType();
    public abstract Boolean isPrefix();

    /**
     * @return optional configuration to automatically mirror this data attribute to a cell of a
     * user-owned Postgres table (load on workflow start, sync on execute-mutation). Null when the
     * data attribute is not DB-synced. Not supported together with prefix data attributes.
     */
    @Nullable
    public abstract DbAttributeSync getDbSync();

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
     * Creates a data attribute that is automatically mirrored to a cell of a user-owned Postgres table:
     * the value is loaded from the cell when the workflow starts, and written back to the cell whenever
     * the data attribute is mutated in a state's execute method. The iWF data attribute remains the
     * source of truth; the database cell is a mirror.
     *
     * @param dataType required.
     * @param key      required. The unique key.
     * @param dbSync   required. The table/column mapping and DataSource. See {@link DbAttributeSync}.
     * @return a data attribute definition with DB sync enabled
     */
    public static DataAttributeDef create(final Class dataType, final String key, final DbAttributeSync dbSync) {
        return ImmutableDataAttributeDef.builder()
                .key(key)
                .dataAttributeType(dataType)
                .isPrefix(false)
                .dbSync(dbSync)
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
