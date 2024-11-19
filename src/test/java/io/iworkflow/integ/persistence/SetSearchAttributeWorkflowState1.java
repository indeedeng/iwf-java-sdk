package io.iworkflow.integ.persistence;

import io.iworkflow.core.Context;
import io.iworkflow.core.ImmutableStateDecision;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.StateMovement;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.communication.SignalCommand;
import io.iworkflow.core.persistence.Persistence;

import java.util.Arrays;

public class SetSearchAttributeWorkflowState1 implements WorkflowState<String> {
    public static final String STATE_ID = "setSearchAttribute-s1";

    @Override
    public String getStateId() {
        return STATE_ID;
    }

    @Override
    public Class<String> getInputType() {
        return String.class;
    }

    @Override
    public CommandRequest waitUntil(Context context, String input, Persistence persistence, Communication communication) {

        return CommandRequest.empty; // TODO fix by Katie
    }

    @Override
    public StateDecision execute(
            final Context context,
            final String input,
            final CommandResults commandResults,
            final Persistence persistence,
            final Communication communication) {
        return ImmutableStateDecision.builder()
                .nextStates(Arrays.asList(StateMovement.gracefulCompleteWorkflow("test-result")))
                .build();
    }
}
