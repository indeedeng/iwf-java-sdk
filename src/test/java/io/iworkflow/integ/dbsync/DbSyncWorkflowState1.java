package io.iworkflow.integ.dbsync;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

public class DbSyncWorkflowState1 implements WorkflowState<String> {

    // the value the state writes to the data attribute; this is what should end up mirrored to the DB
    public static final String MUTATED_VALUE = "synced-value";

    @Override
    public Class<String> getInputType() {
        return String.class;
    }

    @Override
    public StateDecision execute(
            final Context context,
            final String input,
            final CommandResults commandResults,
            final Persistence persistence,
            final Communication communication) {
        // the value loaded from the DB cell at workflow start (proves load-on-start)
        final String loadedFromDb = persistence.getDataAttribute(DbSyncWorkflow.STATUS_KEY, String.class);

        // mutate the DB-synced data attribute (proves sync-on-mutation): after this state, the worker
        // reroutes the decision through the sync state which writes MUTATED_VALUE to the DB cell.
        persistence.setDataAttribute(DbSyncWorkflow.STATUS_KEY, MUTATED_VALUE);

        // complete the workflow, returning the loaded value so the test can assert load worked
        return StateDecision.gracefulCompleteWorkflow(loadedFromDb);
    }
}
