package io.iworkflow.integ.basic;

import io.iworkflow.core.Context;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

public class ModelInputWorkflowState1 implements WorkflowState<io.iworkflow.gen.models.Context> {

    @Override
    public Class<io.iworkflow.gen.models.Context> getInputType() {
        return io.iworkflow.gen.models.Context.class;
    }

    @Override
    public CommandRequest waitUntil(final Context context, final io.iworkflow.gen.models.Context input, Persistence persistence, final Communication communication) {
        return CommandRequest.empty;
    }

    @Override
    public StateDecision execute(final Context context, final io.iworkflow.gen.models.Context input, final CommandResults commandResults, Persistence persistence, final Communication communication) {
        return StateDecision.gracefulCompleteObjectExecution(1);
    }
}