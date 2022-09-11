package iwf.demo.subscription;

import iwf.core.StateDef;
import iwf.core.Workflow;
import iwf.core.attributes.QueryAttributeDef;
import iwf.core.attributes.SearchAttributeDef;
import iwf.core.command.SignalMethodDef;

import java.util.List;

public class SubscriptionWorkflow implements Workflow {
    public static final String SIGNAL_METHOD_CANCEL_SUBSCRIPTION = "CancelSubscription";
    public static final String SIGNAL_METHOD_UPDATE_BILLING_PERIOD_CHARGE_AMOUNT = "UpdateBillingPeriodChargeAmount";

    public static final String QUERY_ATTRIBUTE_BILLING_PERIOD_NUMBER = "BillingPeriodNumber";
    public static final String QUERY_ATTRIBUTE_CUSTOMER = "BillingSubscription";

    @Override
    public List<StateDef> getStates() {
        return null;
    }

    @Override
    public List<SignalMethodDef<?>> getSignalMethods() {
        return null;
    }

    @Override
    public List<SearchAttributeDef<?>> getSearchAttributes() {
        return null;
    }

    @Override
    public List<QueryAttributeDef<?>> getQueryAttributes() {
        return null;
    }
}
