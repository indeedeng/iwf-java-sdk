package io.iworkflow.core;

import io.iworkflow.core.communication.CommunicationMethodDef;
import io.iworkflow.core.persistence.PersistenceFieldDef;

import java.util.Collections;
import java.util.List;

/**
 * This is the interface to define an object workflow definition.
 * ObjectWorkflow is a top level concept in iWF. Any object that is long-lasting(at least a few seconds) can be modeled as an "ObjectWorkflow".
 */
public interface ObjectWorkflow {
    /**
     * defines the states of the workflow. A state represents a step of the workflow state machine.
     * A state can execute some commands (signal/timer) and wait for result
     * See more details in the {@link WorkflowState} definition.
     *
     * @return all the state definitions
     */
    List<StateDef> getWorkflowStates();

    /**
     * defines all the persistence fields for this workflow, this includes:
     * 1. Data attributes
     * 2. Search attributes
     * <p>
     * Data attributes can be read/upsert in WorkflowState start/decide API
     * Data attributes  can also be read by getDataObjects API by external applications using {@link Client}
     * <p>
     * Search attributes can be read/upsert in WorkflowState start/decide API
     * Search attributes can also be read by GetSearchAttributes Client API by external applications
     * External applications can also use "SearchWorkflow" API to find workflows by SQL-like query
     *
     * @return the persistence schema
     */
    default List<PersistenceFieldDef> getPersistenceSchema() {
        return Collections.emptyList();
    }

    /**
     * defines all the communication methods for this workflow, this includes
     * 1. Signal channel
     * 2. Interstate channel
     * <p>
     * Signal channel is for external applications to send signal to workflow execution.
     * Workflow execution can listen on the signal in the WorkflowState start API and receive in
     * the WorkflowState decide API
     * <p>
     * InterStateChannel is for synchronization communications between WorkflowStates.
     * E.g. WorkflowStateA will continue after receiving a value from WorkflowStateB
     * @return the communication schema
     */
    default List<CommunicationMethodDef> getCommunicationSchema() {
        return Collections.emptyList();
    }

    /**
     * Define the workflowType of this workflow definition. By default(when return empty string), it's the simple name of the workflow instance,
     * which should be the case for most scenarios.
     * <p>
     * In case of dynamic workflow implementation, return customized values based on constructor input.
     * @return the workflow type
     */
    default String getWorkflowType() {
        return "";
    }
}

