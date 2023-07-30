package io.iworkflow.integ.internalchannel;

import com.google.common.collect.ImmutableList;
import io.iworkflow.core.Context;
import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.StateDef;
import io.iworkflow.core.StateMovement;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.communication.CommunicationMethodDef;
import io.iworkflow.core.communication.InternalChannelCommand;
import io.iworkflow.core.communication.InternalChannelCommandResult;
import io.iworkflow.core.communication.InternalChannelDef;
import io.iworkflow.core.persistence.Persistence;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MultipleSameInternalChannelWorkflow implements ObjectWorkflow {
    public static final String INTER_CHANNEL_NAME_1 = "test-inter-channel-1";
    public static final String INTER_CHANNEL_ID_1 = "test-inter-channel-id-1";

    @Override
    public List<CommunicationMethodDef> getCommunicationSchema() {
        return Arrays.asList(
                InternalChannelDef.create(Integer.class, INTER_CHANNEL_NAME_1)
        );
    }

    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(
                StateDef.startingState(new MultipleSameInternalChannelWorkflowState0()),
                StateDef.nonStartingState(new MultipleSameInternalChannelWorkflowState1()),
                StateDef.nonStartingState(new MultipleSameInternalChannelWorkflowState2()),
                StateDef.nonStartingState(new MultipleSameInternalChannelWorkflowState3())
        );
    }
}

class MultipleSameInternalChannelWorkflowState0 implements WorkflowState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public StateDecision execute(
            Context context,
            Integer input,
            CommandResults commandResults,
            Persistence persistence, final Communication communication) {
        return StateDecision.multiNextStates(
                StateMovement.create(MultipleSameInternalChannelWorkflowState1.class, input),
                StateMovement.create(MultipleSameInternalChannelWorkflowState2.class, input),
                StateMovement.create(MultipleSameInternalChannelWorkflowState3.class, input)
        );
    }
}

class MultipleSameInternalChannelWorkflowState1 implements WorkflowState<Integer> {
    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public CommandRequest waitUntil(
            Context context,
            Integer input,
            Persistence persistence,
            final Communication communication) {
        return CommandRequest.forAnyCommandCombinationCompleted(
                ImmutableList.of(
                        ImmutableList.of(
                                MultipleSameInternalChannelWorkflow.INTER_CHANNEL_ID_1,
                                MultipleSameInternalChannelWorkflow.INTER_CHANNEL_ID_1
                        )
                ),
                InternalChannelCommand.create(
                        MultipleSameInternalChannelWorkflow.INTER_CHANNEL_ID_1,
                        MultipleSameInternalChannelWorkflow.INTER_CHANNEL_NAME_1,
                        2
                )
        );
    }

    @Override
    public StateDecision execute(
            Context context,
            Integer input,
            CommandResults commandResults,
            Persistence persistence,
            final Communication communication) {
        Integer output = 0;

        final InternalChannelCommandResult result1 = commandResults.getAllInternalChannelCommandResult().get(0);
        output += (Integer) result1.getValue().get();

        final InternalChannelCommandResult result2 = commandResults.getAllInternalChannelCommandResult().get(1);
        output += (Integer) result2.getValue().get();

        return StateDecision.gracefulCompleteWorkflow(output);
    }
}

class MultipleSameInternalChannelWorkflowState2 implements WorkflowState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public StateDecision execute(
            Context context,
            Integer input,
            CommandResults commandResults,
            Persistence persistence, final Communication communication) {
        communication.publishInternalChannel(MultipleSameInternalChannelWorkflow.INTER_CHANNEL_NAME_1, 2);
        return StateDecision.deadEnd();
    }
}

class MultipleSameInternalChannelWorkflowState3 implements WorkflowState<Integer> {

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public StateDecision execute(
            Context context,
            Integer input,
            CommandResults commandResults,
            Persistence persistence, final Communication communication) {
        communication.publishInternalChannel(MultipleSameInternalChannelWorkflow.INTER_CHANNEL_NAME_1, 3);
        return StateDecision.deadEnd();
    }
}