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
import iwf.core.command.TimerCommand;
import iwf.demo.subscription.models.Customer;

import java.util.ArrayList;

import static iwf.demo.subscription.ChargeCurrentPeriodState.WF_STATE_CHARGE_CURRENT_PERIOD;
import static iwf.demo.subscription.SubscriptionOverState.WF_STATE_SUBSCRIPTION_OVER;

public class WaitForPeriodState implements WorkflowState<Void> {
    public static final String WF_STATE_WAIT_FOR_NEXT_PERIOD = "waitForNextPeriod";

    @Override
    public String getStateId() {
        return WF_STATE_WAIT_FOR_NEXT_PERIOD;
    }

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public CommandRequest start(Context context, Void input, StateLocalAttributesW stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes) {
        final Customer customer = queryAttributes.get(SubscriptionWorkflow.QUERY_ATTRIBUTE_CUSTOMER);

        return CommandRequest.forAllCommandCompleted(
                new TimerCommand((int) (System.currentTimeMillis() / 1000) + customer.getSubscription().getPeriodsInSubscription())
        );
    }

    @Override
    public StateDecision decide(Context context, Void input, CommandResults commandResults, StateLocalAttributesR stateLocals, SearchAttributesRW searchAttributes, QueryAttributesRW queryAttributes) {
        ArrayList<StateMovement> nextStates = new ArrayList();
        final Customer customer = queryAttributes.get(SubscriptionWorkflow.QUERY_ATTRIBUTE_CUSTOMER);
        int currentPeriodNum = queryAttributes.get(SubscriptionWorkflow.QUERY_ATTRIBUTE_BILLING_PERIOD_NUMBER);
        if (currentPeriodNum < customer.getSubscription().getPeriodsInSubscription()) {
            queryAttributes.upsert(SubscriptionWorkflow.QUERY_ATTRIBUTE_BILLING_PERIOD_NUMBER, currentPeriodNum + 1);
            nextStates.add(new StateMovement(WF_STATE_CHARGE_CURRENT_PERIOD));
        } else {
            nextStates.add(new StateMovement(WF_STATE_SUBSCRIPTION_OVER));
        }

        return new StateDecision(
                nextStates
        );
    }
}
