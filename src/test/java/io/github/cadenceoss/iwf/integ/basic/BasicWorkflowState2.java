package io.github.cadenceoss.iwf.integ.basic;

import io.github.cadenceoss.iwf.core.Context;
import io.github.cadenceoss.iwf.core.StateDecision;
import io.github.cadenceoss.iwf.core.WorkflowState;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.CommandResults;
import io.github.cadenceoss.iwf.core.communication.Communication;
import io.github.cadenceoss.iwf.core.persistence.DataObjectsRW;
import io.github.cadenceoss.iwf.core.persistence.SearchAttributesRW;
import io.github.cadenceoss.iwf.core.persistence.StateLocals;

public class BasicWorkflowState2 implements WorkflowState<Integer> {

    public static final String StateId = "S2";

    @Override
    public String getStateId() {
        return StateId;
    }

    @Override
    public Class<Integer> getInputType() {
        return Integer.class;
    }

    @Override
    public CommandRequest start(final Context context, final Integer input, final StateLocals stateLocals, final SearchAttributesRW searchAttributes, final DataObjectsRW queryAttributes, final Communication communication) {
        return CommandRequest.empty;
    }

    @Override
    public StateDecision decide(final Context context, final Integer input, final CommandResults commandResults, final StateLocals stateLocals, final SearchAttributesRW searchAttributes, final DataObjectsRW queryAttributes, final Communication communication) {
        final int output = input + 1;
        return StateDecision.gracefulCompleteWorkflow(output);
    }
}
