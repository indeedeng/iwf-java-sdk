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
import iwf.core.command.SignalCommand;
import iwf.demo.subscription.models.Customer;

public class UpdateChargeAmountState implements WorkflowState<Void> {
    public static final String WF_STATE_UPDATE_CHARGE_AMOUNT = "updateChargeAmount";

    @Override
    public String getStateId() {
        return WF_STATE_UPDATE_CHARGE_AMOUNT;
    }

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public CommandRequest start(Context context, Void input, StateLocalAttributesW stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes) {
        return CommandRequest.forAllCommandCompleted(
                new SignalCommand(SubscriptionWorkflow.SIGNAL_METHOD_UPDATE_BILLING_PERIOD_CHARGE_AMOUNT)
        );
    }

    @Override
    public StateDecision decide(Context context, Void input, CommandResults commandResults, StateLocalAttributesR stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes) {
        final int newAmount = commandResults.getSignalValueByIndex(0);
        final Customer customer = queryAttributes.get(SubscriptionWorkflow.QUERY_ATTRIBUTE_CUSTOMER);
        customer.getSubscription().setBillingPeriodCharge(newAmount);
        queryAttributes.upsert(SubscriptionWorkflow.QUERY_ATTRIBUTE_CUSTOMER, customer);

        return new StateDecision(
                new StateMovement(WF_STATE_UPDATE_CHARGE_AMOUNT) // go to a loop to update the value
        );
    }
}
