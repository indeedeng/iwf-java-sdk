package io.iworkflow.core.persistence;

import javax.sql.DataSource;
import java.util.function.BiFunction;

/**
 * Configuration that maps a single data attribute to a cell (row + column) of a user-owned Postgres
 * table so that the data attribute is:
 * <ul>
 *     <li>loaded from the cell when the workflow starts, and</li>
 *     <li>written back to the cell whenever the data attribute is mutated in a state's execute method.</li>
 * </ul>
 * The iWF data attribute remains the source of truth; the database cell is a mirror.
 * <p>
 * The SDK never stores database credentials — the caller supplies a fully-configured
 * {@link javax.sql.DataSource} (owning its own connection pooling and credentials).
 *
 * @see DataAttributeDef#create(Class, String, DbAttributeSync)
 */
public final class DbAttributeSync {

    private final DataSource dataSource;
    private final String tableName;
    private final String pkColumnName;
    private final BiFunction<String, Persistence, CellLocation> locator;

    private DbAttributeSync(
            final DataSource dataSource,
            final String tableName,
            final String pkColumnName,
            final BiFunction<String, Persistence, CellLocation> locator) {
        if (dataSource == null) {
            throw new IllegalArgumentException("dataSource cannot be null");
        }
        if (tableName == null) {
            throw new IllegalArgumentException("tableName cannot be null");
        }
        if (pkColumnName == null) {
            throw new IllegalArgumentException("pkColumnName cannot be null");
        }
        if (locator == null) {
            throw new IllegalArgumentException("locator cannot be null");
        }
        this.dataSource = dataSource;
        this.tableName = tableName;
        this.pkColumnName = pkColumnName;
        this.locator = locator;
    }

    /**
     * @param dataSource   required. A user-owned, fully-configured DataSource (credentials + pooling).
     * @param tableName    required. The table name, optionally schema-qualified (e.g. "public.orders").
     * @param pkColumnName required. The primary-key column name used in the WHERE clause.
     * @param locator      required. Given the workflowId and the current {@link Persistence}, returns the
     *                     {@link CellLocation} (primary-key value + data column name) to load from / sync to.
     * @return a DbAttributeSync configuration
     */
    public static DbAttributeSync of(
            final DataSource dataSource,
            final String tableName,
            final String pkColumnName,
            final BiFunction<String, Persistence, CellLocation> locator) {
        return new DbAttributeSync(dataSource, tableName, pkColumnName, locator);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getTableName() {
        return tableName;
    }

    public String getPkColumnName() {
        return pkColumnName;
    }

    public BiFunction<String, Persistence, CellLocation> getLocator() {
        return locator;
    }
}
