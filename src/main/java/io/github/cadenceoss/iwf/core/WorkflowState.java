package io.github.cadenceoss.iwf.core;

import io.github.cadenceoss.iwf.core.command.CommandCarryOverPolicy;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.CommandResults;
import io.github.cadenceoss.iwf.core.communication.Communication;
import io.github.cadenceoss.iwf.core.persistence.Persistence;
import io.github.cadenceoss.iwf.core.persistence.PersistenceLoadingPolicy;

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
     * @param input         the state input which is deserialized by dataConverter with {@link #getInputType}
     * @param persistence   persistence API for 1) data objects, 2) search attributes and 3) stateLocals
     * @param communication communication API, right now only for publishing value to interstate channel
     * @return the requested commands for this step
     */
    CommandRequest start(
            final Context context, I input,
            final Persistence persistence,
            final Communication communication);

    /**
     * Implement this method to decide what to do next when requested commands are ready
     *
     * @param context          the context info of this API invocation, like workflow start time, workflowId, etc
     * @param input            the state input which is deserialized by dataConverter with {@link #getInputType}
     * @param commandResults   the results of the command that executed by {@link #start}
     * @param persistence      persistence API for 1) data objects, 2) search attributes and 3) stateLocals
     * @param communication    communication API, right now only for publishing value to interstate channel
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
     * Default options should work well for most cases
     */
    default StateOptions getStateOptions() {
        return ImmutableStateOptions.builder()
                .queryAttributesLoadingPolicy(PersistenceLoadingPolicy.LoadAllWithoutLocking)
                .searchAttributesLoadingPolicy(PersistenceLoadingPolicy.LoadAllWithoutLocking)
                .commandCarryOverPolicy(CommandCarryOverPolicy.none)
                .build();
    }
}


