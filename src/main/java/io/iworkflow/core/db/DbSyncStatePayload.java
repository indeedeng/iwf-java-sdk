package io.iworkflow.core.db;

import io.iworkflow.gen.models.StateDecision;

import java.util.List;

/**
 * The input carried into the (unregistered, worker-only) SyncDataAttributesToDb system state.
 * <p>
 * When a normal state's execute method mutates a DB-mapped data attribute, the worker reroutes that
 * state's decision through the sync state. To let the workflow continue exactly as intended, the
 * original decision is captured in its already-generated form ({@link StateDecision}, which is
 * Jackson-serializable) and carried here, together with the list of mutated data-attribute keys that
 * must be written to the database. The sync state performs the writes and then replays
 * {@link #getOriginalDecision()} verbatim.
 */
public class DbSyncStatePayload {
    private StateDecision originalDecision;
    private List<String> dataAttributeKeys;

    // for Jackson deserialization
    public DbSyncStatePayload() {
    }

    public DbSyncStatePayload(final StateDecision originalDecision, final List<String> dataAttributeKeys) {
        this.originalDecision = originalDecision;
        this.dataAttributeKeys = dataAttributeKeys;
    }

    public StateDecision getOriginalDecision() {
        return originalDecision;
    }

    public void setOriginalDecision(final StateDecision originalDecision) {
        this.originalDecision = originalDecision;
    }

    public List<String> getDataAttributeKeys() {
        return dataAttributeKeys;
    }

    public void setDataAttributeKeys(final List<String> dataAttributeKeys) {
        this.dataAttributeKeys = dataAttributeKeys;
    }
}
