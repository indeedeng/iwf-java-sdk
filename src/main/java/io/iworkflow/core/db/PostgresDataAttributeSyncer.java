package io.iworkflow.core.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * Executes the SELECT (load) and UPDATE (sync) against a user-owned Postgres table for the
 * data-attribute-to-column mirroring feature.
 * <p>
 * Security:
 * <ul>
 *   <li>All <b>values</b> (primary key value, data value) are bound via {@link PreparedStatement}
 *       parameters — never string-concatenated.</li>
 *   <li>All <b>identifiers</b> (table, primary-key column, data column) come from developer config,
 *       but they cannot be bound as parameters. They are validated against a strict allowlist and
 *       double-quoted, so a malformed/malicious identifier is rejected rather than injected.</li>
 *   <li>Connections are obtained from the caller-supplied {@link DataSource} and always used in a
 *       try-with-resources block to avoid connection leaks.</li>
 * </ul>
 */
public final class PostgresDataAttributeSyncer {

    // A safe SQL identifier: starts with a letter/underscore, followed by letters/digits/underscores.
    private static final Pattern SAFE_IDENTIFIER = Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*$");

    private PostgresDataAttributeSyncer() {
    }

    /**
     * @return the raw text stored in the cell, or null if the row does not exist or the column is null.
     */
    public static String select(
            final DataSource dataSource,
            final String tableName,
            final String pkColumnName,
            final String dataColumnName,
            final String pkValue) {
        final String sql = String.format(
                "SELECT %s FROM %s WHERE %s = ?",
                quoteIdentifier(dataColumnName),
                quoteQualifiedIdentifier(tableName),
                quoteIdentifier(pkColumnName));
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pkValue);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(1);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("failed to load data attribute from %s.%s where %s=%s",
                            tableName, dataColumnName, pkColumnName, pkValue),
                    e);
        }
    }

    /**
     * Updates the cell with the given text value. This is an idempotent single-row UPDATE, so it is
     * safe to retry (e.g. when the sync state is retried by the iWF server).
     */
    public static void update(
            final DataSource dataSource,
            final String tableName,
            final String pkColumnName,
            final String dataColumnName,
            final String pkValue,
            final String value) {
        final String sql = String.format(
                "UPDATE %s SET %s = ? WHERE %s = ?",
                quoteQualifiedIdentifier(tableName),
                quoteIdentifier(dataColumnName),
                quoteIdentifier(pkColumnName));
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            statement.setString(2, pkValue);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("failed to sync data attribute to %s.%s where %s=%s",
                            tableName, dataColumnName, pkColumnName, pkValue),
                    e);
        }
    }

    /**
     * Validates a single SQL identifier against the allowlist and wraps it in double quotes.
     */
    static String quoteIdentifier(final String identifier) {
        if (identifier == null || !SAFE_IDENTIFIER.matcher(identifier).matches()) {
            throw new IllegalArgumentException("unsafe SQL identifier: " + identifier);
        }
        return "\"" + identifier + "\"";
    }

    /**
     * Validates and quotes a possibly schema-qualified identifier (e.g. "public.orders"),
     * quoting each dot-separated part independently.
     */
    static String quoteQualifiedIdentifier(final String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            throw new IllegalArgumentException("unsafe SQL identifier: " + identifier);
        }
        final String[] parts = identifier.split("\\.", -1);
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append('.');
            }
            sb.append(quoteIdentifier(parts[i]));
        }
        return sb.toString();
    }
}
