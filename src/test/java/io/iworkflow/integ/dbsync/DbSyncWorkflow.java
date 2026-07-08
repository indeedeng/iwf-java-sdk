package io.iworkflow.integ.dbsync;

import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.StateDef;
import io.iworkflow.core.persistence.CellLocation;
import io.iworkflow.core.persistence.DataAttributeDef;
import io.iworkflow.core.persistence.DbAttributeSync;
import io.iworkflow.core.persistence.PersistenceFieldDef;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * Integration-test workflow demonstrating the data-attribute Postgres DB-sync feature.
 * The "status" data attribute is mirrored to the iwf_integ_data.data_col cell keyed by
 * {@link #ROW_PK}. On start it is loaded from that cell; when the state mutates it, it is synced back.
 */
@Component
public class DbSyncWorkflow implements ObjectWorkflow {

    public static final String STATUS_KEY = "status";
    public static final String TABLE_NAME = "iwf_integ_data";
    public static final String PK_COLUMN = "pk";
    public static final String DATA_COLUMN = "data_col";
    public static final String ROW_PK = "db-sync-test-key";

    public static final String JDBC_URL = "jdbc:postgresql://localhost:5455/iwf_integ";
    public static final String DB_USER = "iwf";
    public static final String DB_PASSWORD = "iwf";

    public static DataSource buildDataSource() {
        final PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(JDBC_URL);
        ds.setUser(DB_USER);
        ds.setPassword(DB_PASSWORD);
        return ds;
    }

    private static final DataSource DATA_SOURCE = buildDataSource();

    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(StateDef.startingState(new DbSyncWorkflowState1()));
    }

    @Override
    public List<PersistenceFieldDef> getPersistenceSchema() {
        return Arrays.asList(
                DataAttributeDef.create(
                        String.class,
                        STATUS_KEY,
                        DbAttributeSync.of(
                                DATA_SOURCE,
                                TABLE_NAME,
                                PK_COLUMN,
                                (workflowId, persistence) -> new CellLocation(ROW_PK, DATA_COLUMN)))
        );
    }
}
