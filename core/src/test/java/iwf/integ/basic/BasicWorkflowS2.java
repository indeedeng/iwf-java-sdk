package iwf.integ.basic;

import iwf.core.Context;
import iwf.core.StateDecision;
import iwf.core.WorkflowState;
import iwf.core.attributes.QueryAttributesRW;
import iwf.core.attributes.SearchAttributesRW;
import iwf.core.attributes.StateLocalAttributesR;
import iwf.core.attributes.StateLocalAttributesW;
import iwf.core.command.CommandRequest;
import iwf.core.command.CommandResults;

public class BasicWorkflowS2 implements WorkflowState<Integer> {

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
    public CommandRequest start(final Context context, final Integer input, final StateLocalAttributesW stateLocals, final SearchAttributesRW searchAttributes, final QueryAttributesRW queryAttributes) {
        return CommandRequest.empty;
    }

    @Override
    public StateDecision decide(final Context context, final Integer input, final CommandResults commandResults, final StateLocalAttributesR stateLocals, final SearchAttributesRW searchAttributes, final QueryAttributesRW queryAttributes) {
        final int output = input + 1;
        return StateDecision.gracefulCompleteWorkflow(output);
    }
}
