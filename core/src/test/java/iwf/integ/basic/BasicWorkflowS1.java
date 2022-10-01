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

public class BasicWorkflowS1 implements WorkflowState {

    public static final String StateId = "S1";

    @Override
    public String getStateId() {
        return StateId;
    }

    @Override
    public Class getInputType() {
        return null;
    }

    @Override
    public CommandRequest start(final Context context, final Object input, final StateLocalAttributesW stateLocals, final SearchAttributesRW searchAttributes, final QueryAttributesRW queryAttributes) {
        return CommandRequest.empty;
    }

    @Override
    public StateDecision decide(final Context context, final Object input, final CommandResults commandResults, final StateLocalAttributesR stateLocals, final SearchAttributesRW searchAttributes, final QueryAttributesRW queryAttributes) {
        return StateDecision.COMPLETING_WORKFLOW;
    }
}
