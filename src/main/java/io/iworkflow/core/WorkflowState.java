package io.iworkflow.core;

import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.gen.models.WorkflowStateOptions;

public interface WorkflowState<I> {
    /**
     * a unique identifier of the state
     */
    String getStateId();

    /**
     * This input type is needed for deserializing data back into Java object
     */
    Class<I> getInputType();

    /**
     * Implement this method to execute the commands set up for this state.
     *
     * @param context       the context info of this API invocation, like workflow start time, workflowId, etc
     * @param input         the state input which is deserialized by {@link ObjectEncoder} with {@link #getInputType}
     * @param persistence   persistence API for 1) data objects, 2) search attributes and 3) stateLocals 4) recordEvent
     *                      DataObjects and SearchAttributes are defined by {@link Workflow} interface.
     *                      StateLocals are for passing data within the state execution from this start API to {@link #decide} API
     *                      RecordEvent is for storing some tracking info(e.g. RPC call input/output) when executing the API.
     *                      Note that any write API will be recorded to server after the whole start API response is accepted.
     * @param communication communication API, right now only for publishing value to interstate channel
     *                      Note that any write API will be recorded to server after the whole start API response is accepted.
     * @return the requested commands for this step
     */
    CommandRequest start(
            final Context context, I input,
            final Persistence persistence,
            final Communication communication);

    /**
     * Implement this method to decide what to do next when requested commands are ready
     *
     * @param context        the context info of this API invocation, like workflow start time, workflowId, etc
     * @param input          the state input which is deserialized by {@link ObjectEncoder} with {@link #getInputType}
     * @param commandResults the results of the command that executed by {@link #start}
     * @param persistence    persistence API for 1) data objects, 2) search attributes and 3) stateLocals 4) recordEvent
     *                       DataObjects and SearchAttributes are defined by {@link Workflow} interface.
     *                       StateLocals are for passing data within the state execution from this start API to {@link #decide} API
     *                       RecordEvent is for storing some tracking info(e.g. RPC call input/output) when executing the API.
     *                       Note that the write API will be recorded to server after the whole start API response is accepted.
     * @param communication  communication API, right now only for publishing value to interstate channel
     *                       Note that the write API will be recorded to server after the whole decide API response is accepted.
     * @return the decision of what to do next(e.g. transition to next states)
     */
    StateDecision decide(
            final Context context,
            final I input,
            final CommandResults commandResults,
            final Persistence persistence,
            final Communication communication);

    /**
     * Optional configuration to adjust the state behaviors
     * Default:
     * LOAD_ALL_WITHOUT_LOCKING for dataObjects/searchAttributes,
     * start/decide API:
     * timeout:10s
     * retryPolicy:
     * InitialIntervalSeconds: 1
     * MaxInternalSeconds:100
     * MaximumAttempts: 0
     * BackoffCoefficient: 2
     */
    default WorkflowStateOptions getStateOptions() {
        return null;
    }
}


