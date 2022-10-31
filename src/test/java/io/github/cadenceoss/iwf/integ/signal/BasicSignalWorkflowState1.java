package io.github.cadenceoss.iwf.integ.signal;

import io.github.cadenceoss.iwf.core.Context;
import io.github.cadenceoss.iwf.core.StateDecision;
import io.github.cadenceoss.iwf.core.WorkflowState;
import io.github.cadenceoss.iwf.core.attributes.QueryAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.SearchAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.StateLocalAttributesR;
import io.github.cadenceoss.iwf.core.attributes.StateLocalAttributesRW;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.CommandResults;
import io.github.cadenceoss.iwf.core.command.SignalCommand;
import io.github.cadenceoss.iwf.core.command.SignalCommandResult;

public class BasicSignalWorkflowState1 implements WorkflowState<Integer> {
    public static final String STATE_ID = "signal-s1";
    public static final String SIGNAL_CHANNEL_NAME = "test-signal";
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
            StateLocalAttributesRW stateLocals,
            SearchAttributesRW searchAttributes,
            QueryAttributesRW queryAttributes) {
        return CommandRequest.forAllCommandCompleted(SignalCommand.create(COMMAND_ID, SIGNAL_CHANNEL_NAME));
    }

    @Override
    public StateDecision decide(
            Context context,
            Integer input,
            CommandResults commandResults,
            StateLocalAttributesR stateLocals,
            SearchAttributesRW searchAttributes,
            QueryAttributesRW queryAttributes) {
        SignalCommandResult signalCommandResult = commandResults.getAllSignalCommandResults().get(0);
        Integer output = input + (Integer) signalCommandResult.getSignalValue();
        return StateDecision.gracefulCompleteWorkflow(output);
    }
}
