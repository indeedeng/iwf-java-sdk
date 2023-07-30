package io.iworkflow.integ.signal;

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
import io.iworkflow.core.communication.SignalChannelDef;
import io.iworkflow.core.communication.SignalCommand;
import io.iworkflow.core.communication.SignalCommandResult;
import io.iworkflow.core.persistence.Persistence;
import static io.iworkflow.integ.signal.MultipleSameSignalWorkflow.SIGNAL_ID_1;
import static io.iworkflow.integ.signal.MultipleSameSignalWorkflow.SIGNAL_NAME_1;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MultipleSameSignalWorkflow implements ObjectWorkflow {
    public static final String SIGNAL_NAME_1 = "test-signal-1";
    public static final String SIGNAL_ID_1 = "test-signal-id-1";

    @Override
    public List<CommunicationMethodDef> getCommunicationSchema() {
        return Arrays.asList(
                SignalChannelDef.create(Integer.class, SIGNAL_NAME_1)
        );
    }

    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(
                StateDef.startingState(new MultipleSameSignalWorkflowState0()),
                StateDef.nonStartingState(new MultipleSameSignalWorkflowState1())
        );
    }
}

class MultipleSameSignalWorkflowState0 implements WorkflowState<Integer> {

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
                StateMovement.create(MultipleSameSignalWorkflowState1.class, input)
        );
    }
}

class MultipleSameSignalWorkflowState1 implements WorkflowState<Integer> {
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
                                SIGNAL_ID_1, SIGNAL_ID_1
                        )
                ),
                SignalCommand.create(
                        SIGNAL_ID_1,
                        SIGNAL_NAME_1,
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

        final SignalCommandResult result1 = commandResults.getAllSignalCommandResults().get(0);
        output += (Integer) result1.getSignalValue().get();

        final SignalCommandResult result2 = commandResults.getAllSignalCommandResults().get(1);
        output += (Integer) result2.getSignalValue().get();

        return StateDecision.gracefulCompleteWorkflow(output);
    }
}