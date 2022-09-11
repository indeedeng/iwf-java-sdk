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

import static iwf.demo.subscription.CancelSubscriptionState.WF_STATE_CANCEL_SUBSCRIPTION;
import static iwf.demo.subscription.UpdateChargeAmountState.WF_STATE_UPDATE_CHARGE_AMOUNT;
import static iwf.demo.subscription.WaitForPeriodState.WF_STATE_WAIT_FOR_NEXT_PERIOD;

public class WelcomeEmailState implements WorkflowState<Customer> {
    public static final String WF_STATE_SEND_WELCOME_EMAIL = "sendWelcomeEmail";

    @Override
    public String getStateId() {
        return WF_STATE_SEND_WELCOME_EMAIL;
    }

    @Override
    public Class<Customer> getInputType() {
        return Customer.class;
    }

    @Override
    public CommandRequest start(Context context, Customer input, StateLocalAttributesW stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes) {
        // invoke API here to send subscription start email.
        // control the timeout by customizing the WorkflowStateOptions
        return CommandRequest.none();
    }

    @Override
    public StateDecision decide(final Context context,final Customer customer, final CommandResults commandResults, final StateLocalAttributesR stateLocals,
                                final SearchAttributesRW searchAttributes, final QueryAttributesRW queryAttributes) {
        queryAttributes.upsert(SubscriptionWorkflow.QUERY_ATTRIBUTE_BILLING_PERIOD_NUMBER, 0); // starting from 0
        queryAttributes.upsert(SubscriptionWorkflow.QUERY_ATTRIBUTE_CUSTOMER, customer);

        return new StateDecision(
                new StateMovement(WF_STATE_CANCEL_SUBSCRIPTION),
                new StateMovement(WF_STATE_UPDATE_CHARGE_AMOUNT),
                new StateMovement(WF_STATE_WAIT_FOR_NEXT_PERIOD)
        );
    }
}
