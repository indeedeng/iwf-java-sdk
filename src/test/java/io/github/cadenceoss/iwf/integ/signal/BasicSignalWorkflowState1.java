package io.github.cadenceoss.iwf.integ.signal;

import io.github.cadenceoss.iwf.core.Context;
import io.github.cadenceoss.iwf.core.StateDecision;
import io.github.cadenceoss.iwf.core.WorkflowState;
import io.github.cadenceoss.iwf.core.attributes.QueryAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.SearchAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.StateLocal;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.CommandResults;
import io.github.cadenceoss.iwf.core.command.InterStateChannel;
import io.github.cadenceoss.iwf.core.command.SignalCommand;
import io.github.cadenceoss.iwf.core.command.SignalCommandResult;
import io.github.cadenceoss.iwf.gen.models.SignalResult;

public class BasicSignalWorkflowState1 implements WorkflowState<Integer> {
    public static final String STATE_ID = "signal-s1";
    public static final String SIGNAL_CHANNEL_NAME_1 = "test-signal-1";

    public static final String SIGNAL_CHANNEL_NAME_2 = "test-signal-2";
    public static final String COMMAND_ID = "test-signal-id";

    @Override
    public String getStateId() {
        return STATE_ID;
    }

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public CommandRequest start(
            Context context,
            Integer input,
            StateLocal stateLocals,
            SearchAttributesRW searchAttributes,
            QueryAttributesRW queryAttributes, final InterStateChannel interStateChannel) {
        return CommandRequest.forAnyCommandCompleted(
                SignalCommand.create(COMMAND_ID, SIGNAL_CHANNEL_NAME_1),
                SignalCommand.create(COMMAND_ID, SIGNAL_CHANNEL_NAME_2)
        );
    }

    @Override
    public StateDecision decide(
            Context context,
            Integer input,
            CommandResults commandResults,
            StateLocal stateLocals,
            SearchAttributesRW searchAttributes,
            QueryAttributesRW queryAttributes, final InterStateChannel interStateChannel) {
        SignalCommandResult signalCommandResult = commandResults.getAllSignalCommandResults().get(0);
        Integer output = input + (Integer) signalCommandResult.getSignalValue().get();

        SignalCommandResult signalCommandResult2 = commandResults.getAllSignalCommandResults().get(1);
        if (signalCommandResult2.getSignalRequestStatusEnum() != SignalResult.SignalRequestStatusEnum.WAITING) {
            throw new RuntimeException("the second signal should be waiting");
        }
        return StateDecision.gracefulCompleteWorkflow(output);
    }
}
