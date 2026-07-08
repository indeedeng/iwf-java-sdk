package io.iworkflow.integ;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.WorkflowOptions;
import io.iworkflow.gen.models.IDReusePolicy;
import io.iworkflow.integ.dbsync.DbSyncWorkflow;
import io.iworkflow.integ.dbsync.DbSyncWorkflowState1;
import io.iworkflow.spring.TestSingletonWorkerService;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;

import javax.sql.DataSource;

/**
 * End-to-end test of the data-attribute Postgres DB-sync feature against a real Postgres
 * (script/docker-compose.yml: iwf-integ-postgres) and a live iWF server.
 * <p>
 * The schema, table, and initial dataset are created by script/postgres-init/iwf-integ-init.sql on
 * first container start. @BeforeEach resets the target row to the known initial value so the test is
 * deterministic across reruns against a persistent local DB.
 */
public class DbSyncTest {

    private static final String INITIAL_DB_JSON = "\"seeded-value\"";
    private static final String INITIAL_DB_VALUE = "seeded-value";

    private final DataSource dataSource = DbSyncWorkflow.buildDataSource();

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        TestSingletonWorkerService.startWorkerIfNotUp();
        resetSeedRow();
    }

    private void resetSeedRow() {
        final String sql = "INSERT INTO " + DbSyncWorkflow.TABLE_NAME
                + " (pk, data_col) VALUES (?, ?) ON CONFLICT (pk) DO UPDATE SET data_col = EXCLUDED.data_col";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, DbSyncWorkflow.ROW_PK);
            ps.setString(2, INITIAL_DB_JSON);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("failed to reset seed row (is iwf-integ-postgres up?)", e);
        }
    }

    private String readColumn() {
        final String sql = "SELECT data_col FROM " + DbSyncWorkflow.TABLE_NAME + " WHERE pk = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, DbSyncWorkflow.ROW_PK);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to read column", e);
        }
    }

    // In CI (Linux) the iWF server reaches the host worker via 172.17.0.1 (ClientOptions.localDefault).
    // On macOS/OrbStack the host is instead reachable via host.docker.internal, so set
    // IWF_WORKER_URL=http://host.docker.internal:8802 when running locally there.
    private static ClientOptions clientOptions() {
        final String workerUrl = System.getenv("IWF_WORKER_URL");
        if (workerUrl != null && !workerUrl.isEmpty()) {
            return ClientOptions.minimum(workerUrl, ClientOptions.defaultServerUrl);
        }
        return ClientOptions.localDefault;
    }

    @Test
    public void testDataAttributeDbSync() {
        final Client client = new Client(WorkflowRegistry.registry, clientOptions());
        final String wfId = "db-sync-test-id-" + System.currentTimeMillis() / 1000;

        client.startWorkflow(
                DbSyncWorkflow.class, wfId, 10, "start",
                WorkflowOptions.basicBuilder()
                        .workflowIdReusePolicy(IDReusePolicy.ALLOW_IF_NO_RUNNING)
                        .build());

        // (a) load-on-start: the state read the seeded DB value, returned as the workflow output
        final String output = client.getSimpleWorkflowResultWithWait(String.class, wfId);
        Assertions.assertEquals(INITIAL_DB_VALUE, output);

        // (b) sync-on-mutation: the mutated value was written back to the DB cell (stored as JSON text)
        Assertions.assertEquals("\"" + DbSyncWorkflowState1.MUTATED_VALUE + "\"", readColumn());
    }
}
