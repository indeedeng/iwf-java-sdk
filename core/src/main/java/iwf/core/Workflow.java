package iwf.core;

import iwf.core.attributes.QueryAttributeDef;
import iwf.core.attributes.SearchAttributeDef;
import iwf.core.command.LongRunningActivityDef;
import iwf.core.command.SignalMethodDef;

import java.util.Collections;
import java.util.List;

/**
 * This is a simplified Cadence/Temporal workflow. All the complexity of
 * history replay and decision task processing are hidden. No matter how you modify the workflow code, it will never run
 * into non-deterministic errors. The signal/timer/activities will be defined into a way that you don't need to understand
 * what is a workflow decision task and how it's executed.
 * It preserves the capabilities of executed activities, setting timers, processing signals, upsert search attributes, and
 * setting query methods. So basically, you still have the full power of using Cadence/Temporal, without needing to understand
 * the complex technology.
 * The workflow is still defined as code but in a different way. Instead of having a whole piece of workflow method to define the
 * workflow code, you will have to split the logic and place into different states.
 */
public interface Workflow {
    /**
     * defines the states of the workflow. A state represents a step of the workflow state machine.
     * A state can execute some commands (activities/signal/timer) and wait for result
     * See more details in the state definition.
     */
    List<StateDef> getStates();

    /**
     * defines all the signal methods supported by this workflow.
     */
    List<SignalMethodDef<?>> getSignalMethods();

    /**
     * defines all the search attributes supported by this workflow.
     */
    List<SearchAttributeDef<?>> getSearchAttributes();

    /**
     * defines all the query attributes supported by this workflow.
     */
    List<QueryAttributeDef<?>> getQueryAttributes();

    /**
     * defines all the long running activity types supported by this workflow.
     * NOTE that there is NO regular activities in iwf. For non-long-running activities, you just implement them
     * in the workflow state APIs(start/decide).
     */
    default List<LongRunningActivityDef<?>> getLongRunningActivityTypes(){
        return Collections.emptyList();
    }
}

