package io.github.cadenceoss.iwf.integ.timer;

import io.github.cadenceoss.iwf.core.Context;
import io.github.cadenceoss.iwf.core.StateDecision;
import io.github.cadenceoss.iwf.core.WorkflowState;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.CommandResults;
import io.github.cadenceoss.iwf.core.command.TimerCommand;
import io.github.cadenceoss.iwf.core.communication.Communication;
import io.github.cadenceoss.iwf.core.persistence.DataObjectsRW;
import io.github.cadenceoss.iwf.core.persistence.SearchAttributesRW;
import io.github.cadenceoss.iwf.core.persistence.StateLocals;

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
            StateLocals stateLocals,
            SearchAttributesRW searchAttributes,
            DataObjectsRW queryAttributes, final Communication communication) {
        return CommandRequest.forAllCommandCompleted(TimerCommand.createByDuration(Duration.ofSeconds(input)));
    }

    @Override
    public StateDecision decide(
            Context context,
            Integer input,
            CommandResults commandResults,
            StateLocals stateLocals,
            SearchAttributesRW searchAttributes,
            DataObjectsRW queryAttributes, final Communication communication) {
        return StateDecision.gracefulCompleteWorkflow();
    }
}
