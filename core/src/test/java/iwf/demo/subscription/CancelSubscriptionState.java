package iwf.demo.subscription;

import iwf.core.Context;
import iwf.core.StateDecision;
import iwf.core.WorkflowState;
import iwf.core.attributes.QueryAttributesRW;
import iwf.core.attributes.SearchAttributesRW;
import iwf.core.attributes.StateLocalAttributesR;
import iwf.core.attributes.StateLocalAttributesW;
import iwf.core.command.CommandRequest;
import iwf.core.command.CommandResults;
import iwf.core.command.SignalCommand;

import static iwf.core.StateDecision.COMPLETING_WORKFLOW;

public class CancelSubscriptionState implements WorkflowState<Void> {
    public static final String WF_STATE_CANCEL_SUBSCRIPTION = "cancelSubscription";

    @Override
    public String getStateId() {
        return WF_STATE_CANCEL_SUBSCRIPTION;
    }

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public CommandRequest start(Context context, Void input, StateLocalAttributesW stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes) {
        return CommandRequest.forAllCommandCompleted(
                new SignalCommand(SubscriptionWorkflow.SIGNAL_METHOD_CANCEL_SUBSCRIPTION)
        );
    }

    @Override
    public StateDecision decide(Context context, Void input, CommandResults commandResults, StateLocalAttributesR stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes) {
        return COMPLETING_WORKFLOW;
    }
}
