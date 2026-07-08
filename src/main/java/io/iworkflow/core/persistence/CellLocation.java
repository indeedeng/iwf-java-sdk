package io.iworkflow.core.persistence;

/**
 * Identifies a single cell (row + column) in a user-owned database table that a data attribute is
 * mirrored to. It is the return value of the locator lambda in {@link DbAttributeSync}.
 * <p>
 * The primary key value is a String — it is bound as a {@link java.sql.PreparedStatement} parameter,
 * so it can represent any text/UUID/numeric primary key as long as the column type is compatible.
 */
public final class CellLocation {
    private final String pkValue;
    private final String columnName;

    public CellLocation(final String pkValue, final String columnName) {
        if (pkValue == null) {
            throw new IllegalArgumentException("pkValue cannot be null");
        }
        if (columnName == null) {
            throw new IllegalArgumentException("columnName cannot be null");
        }
        this.pkValue = pkValue;
        this.columnName = columnName;
    }

    public String getPkValue() {
        return pkValue;
    }

    public String getColumnName() {
        return columnName;
    }
}
