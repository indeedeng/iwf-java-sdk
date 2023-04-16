package io.iworkflow.core;

import io.iworkflow.core.communication.CommunicationMethodDef;
import io.iworkflow.core.persistence.PersistenceFieldDef;

import java.util.Collections;
import java.util.List;

/**
 * This is the interface to define a durable execution object.
 * DEObject is a top level concept in iWF. Anything that is long-lasting(at least a few seconds) can be modeled as an "DEObject".
 */
public interface DEObject {
    /**
     * defines the workflow states of the DEObject. A state represents a step of a workflow state machine.
     * A state can execute some commands (signal/timer) and wait for result
     * See more details in the {@link WorkflowState} definition.
     *
     * @return all the state definitions
     */
    List<StateDef> getWorkflowStates();

    /**
     * defines all the persistence fields for this object, this includes:
     * 1. Data attributes
     * 2. Search attributes
     * <p>
     * Data attributes can be read/upsert in WorkflowState start/decide API
     * Data attributes  can also be read by getDataObjects API by external applications using {@link Client}
     * <p>
     * Search attributes can be read/upsert in WorkflowState start/decide API
     * Search attributes can also be read by GetSearchAttributes Client API by external applications
     * External applications can also use "SearchObject" API to find objects by SQL-like query
     *
     * @return the persistence schema
     */
    default List<PersistenceFieldDef> getPersistenceSchema() {
        return Collections.emptyList();
    }

    /**
     * defines all the communication methods for this object, this includes
     * 1. Signal channel
     * 2. Interstate channel
     * <p>
     * Signal channel is for external applications to send signal to object execution.
     * Object execution can listen on the signal in the WorkflowState start API and receive in
     * the WorkflowState decide API
     * <p>
     * InternalChannel is for synchronization communications within the object execution internally.
     * E.g. WorkflowStateA will continue after receiving a value from WorkflowStateB, or from an RPC
     *
     * @return the communication schema
     */
    default List<CommunicationMethodDef> getCommunicationSchema() {
        return Collections.emptyList();
    }

    /**
     * Define the ObjectType of this object definition. By default(when return empty string), it's the simple name of the DEObject class instance,
     * which should be the case for most scenarios.
     *
     * @return the object type
     */
    default String getObjectType() {
        return "";
    }
}

