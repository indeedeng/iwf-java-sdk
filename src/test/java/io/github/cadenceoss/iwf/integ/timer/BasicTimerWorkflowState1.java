package io.github.cadenceoss.iwf.integ.timer;

import io.github.cadenceoss.iwf.core.Context;
import io.github.cadenceoss.iwf.core.StateDecision;
import io.github.cadenceoss.iwf.core.WorkflowState;
import io.github.cadenceoss.iwf.core.attributes.QueryAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.SearchAttributesRW;
import io.github.cadenceoss.iwf.core.attributes.StateLocalAttributesR;
import io.github.cadenceoss.iwf.core.attributes.StateLocalAttributesRW;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.CommandResults;
import io.github.cadenceoss.iwf.core.command.TimerCommand;

import java.time.Duration;

public class BasicTimerWorkflowState1 implements WorkflowState<Integer> {
    public static final String STATE_ID = "timer-s1";
    public static final String COMMAND_ID = "test-timer-id";

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
        return CommandRequest.forAllCommandCompleted(TimerCommand.createByDuration(COMMAND_ID, Duration.ofSeconds(input)));
    }

    @Override
    public StateDecision decide(
            Context context,
            Integer input,
            CommandResults commandResults,
            StateLocalAttributesR stateLocals,
            SearchAttributesRW searchAttributes,
            QueryAttributesRW queryAttributes) {
        return StateDecision.gracefulCompleteWorkflow();
    }
}
