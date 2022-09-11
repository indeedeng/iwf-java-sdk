package iwf.demo.subscription;

import iwf.core.Context;
import iwf.core.StateDecision;
import iwf.core.StateMovement;
import iwf.core.WorkflowState;
import iwf.core.attributes.QueryAttributesRW;
import iwf.core.attributes.SearchAttributesRW;
import iwf.core.attributes.StateLocalAttributesR;
import iwf.core.attributes.StateLocalAttributesW;
import iwf.core.command.CommandRequest;
import iwf.core.command.CommandResults;
import iwf.demo.subscription.models.Customer;

import static iwf.demo.subscription.WaitForPeriodState.WF_STATE_WAIT_FOR_NEXT_PERIOD;

public class ChargeCurrentPeriodState implements WorkflowState<Void> {
    public static final String WF_STATE_CHARGE_CURRENT_PERIOD = "chargeCurrentPeriod";

    @Override
    public String getStateId() {
        return WF_STATE_CHARGE_CURRENT_PERIOD;
    }

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public CommandRequest start(Context context, Void input, StateLocalAttributesW stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes) {
        final Customer customer = searchAttributes.get(SubscriptionWorkflow.QUERY_ATTRIBUTE_CUSTOMER);
        final int currentPeriod = queryAttributes.get(SubscriptionWorkflow.QUERY_ATTRIBUTE_BILLING_PERIOD_NUMBER);
        // invoke API here to chart the user
        // control the timeout by customizing the WorkflowStateOptions
        return CommandRequest.none();
    }

    @Override
    public StateDecision decide(Context context, Void input, CommandResults commandResults, StateLocalAttributesR stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes) {
        return new StateDecision(
                new StateMovement(WF_STATE_WAIT_FOR_NEXT_PERIOD)
        );
    }
}
