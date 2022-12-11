package io.github.cadenceoss.iwf.core;

import io.github.cadenceoss.iwf.core.command.CommandCarryOverPolicy;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import io.github.cadenceoss.iwf.core.command.CommandResults;
import io.github.cadenceoss.iwf.core.communication.Communication;
import io.github.cadenceoss.iwf.core.persistence.DataObjectsRW;
import io.github.cadenceoss.iwf.core.persistence.PersistenceLoadingPolicy;
import io.github.cadenceoss.iwf.core.persistence.SearchAttributesRW;
import io.github.cadenceoss.iwf.core.persistence.StateLocals;

public interface WorkflowState<I> {
    /**
     * a unique identifier of the state
     */
    String getStateId();

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

    /**
     * This input type is needed for deserializing data back into Java object
     */
    Class<I> getInputType();

    /**
     * Implement this method to execute the commands set up for this state.
     *
     * @param input            the state input which is deserialized by dataConverter with {@link #getInputType}
     * @param stateLocals      read/write the local attributes in the state. Mostly for recording an action into this history for tracking, or passing a value from Start API to Decide API.
     * @param queryAttributes  read or write to the query attributes as the API response
     * @param searchAttributes read or write to the search attributes as the API response
     * @return the requested commands for this step
     */
    CommandRequest start(
            final Context context, I input,
            final StateLocals stateLocals,
            final SearchAttributesRW searchAttributes,
            final DataObjectsRW queryAttributes,
            final Communication communication);

    /**
     * Implement this method to decide what to do next when requested commands are ready
     *
     * @param input            the state input which is deserialized by dataConverter with {@link #getInputType}
     * @param commandResults   the results of the command that executed by {@link #start}
     * @param stateLocals      read/write the local attributes in the state. Mostly for recording an action into this history for tracking, or passing a value from Start API to Decide API.
     * @param queryAttributes  the query attributes that can be used as Read+Write
     * @param searchAttributes the search attributes that can be used as Read+Write
     * @return the decision of what to do next(e.g. transition to next states)
     */
    StateDecision decide(
            final Context context,
            final I input,
            final CommandResults commandResults,
            final StateLocals stateLocals,
            final SearchAttributesRW searchAttributes,
            final DataObjectsRW queryAttributes,
            final Communication communication);
}


