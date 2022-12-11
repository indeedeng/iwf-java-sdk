package io.github.cadenceoss.iwf.core;

import io.github.cadenceoss.iwf.core.communication.InterStateChannel;
import io.github.cadenceoss.iwf.core.communication.SignalChannel;
import io.github.cadenceoss.iwf.core.persistence.DataObjectField;
import io.github.cadenceoss.iwf.core.persistence.SearchAttributeField;

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
     * defines all the signal channels supported by this workflow.
     * Signal channel is for external applications to send signal to workflow execution.
     * Workflow execution can listen on the signal in the WorkflowState start API and receive in
     * the WorkflowState decide API
     */
    default List<SignalChannel> getSignalChannels() {
        return Collections.emptyList();
    }

    /**
     * defines all the search attributes supported by this workflow.
     * Search attributes can be read/upsert in WorkflowState start/decide API
     * Search attributes can also be read by GetSearchAttributes Client API by external applications
     * External applications can also use "SearchWorkflow" API to find workflows by SQL-like query
     */
    default List<SearchAttributeField> getSearchAttributes() {
        return Collections.emptyList();
    }

    /**
     * defines all the query attributes supported by this workflow.
     * Query attributes can be read/upsert in WorkflowState start/decide API
     * Query attributes can also be read by GetQueryAttributes Client API by external applications
     */
    default List<DataObjectField> getQueryAttributes() {
        return Collections.emptyList();
    }

    /**
     * defines all the interStateChannels supported by this workflow
     * InterStateChannel are for synchronization communications between WorkflowStates.
     * E.g. WorkflowStateA will continue after receiving a value from WorkflowStateB
     */
    default List<InterStateChannel> getInterStateChannels() {
        return Collections.emptyList();
    }

    /**
     * The workflowType that will be used to registered a workflow. By default is the simpleName of the class(when providing empty string).
     */
    default String getWorkflowType() {
        return "";
    }
    
    /**
     * defines all the long running activity types supported by this workflow.
     * NOTE that there is NO regular activities in iwf. For non-long-running activities, you just implement them
     * in the workflow state APIs(start/decide).
     */
    // TODO: need server to support this feature
//    default List<LongRunningActivityDef<?>> getLongRunningActivityTypes(){
//        return Collections.emptyList();
//    }
}

