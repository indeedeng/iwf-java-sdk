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
import iwf.demo.subscription.models.Customer;

public class SubscriptionOverState implements WorkflowState<Void> {
    public static final String WF_STATE_SUBSCRIPTION_OVER = "subscriptionOver";

    @Override
    public String getStateId() {
        return WF_STATE_SUBSCRIPTION_OVER;
    }

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public CommandRequest start(Context context, Void input, StateLocalAttributesW stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes) {
        final Customer customer = queryAttributes.get(SubscriptionWorkflow.QUERY_ATTRIBUTE_CUSTOMER);
        // invoke API here to send subscription over email.
        // control the timeout by customizing the WorkflowStateOptions
        return CommandRequest.none();
    }

    @Override
    public StateDecision decide(Context context, Void input, CommandResults commandResults, StateLocalAttributesR stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes) {
        return StateDecision.COMPLETING_WORKFLOW;
    }
}
