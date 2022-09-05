package iwf.core;

import iwf.core.attributes.AttributeLoadingPolicy;
import iwf.core.attributes.QueryAttributesRW;
import iwf.core.attributes.SearchAttributesRW;
import iwf.core.attributes.StateLocalAttributesR;
import iwf.core.attributes.StateLocalAttributesW;
import iwf.core.command.CommandCarryOverPolicy;
import iwf.core.command.CommandRequest;
import iwf.core.command.CommandResults;

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
        return new StateOptions(AttributeLoadingPolicy.getLoadAllWithoutLocking(),
                AttributeLoadingPolicy.getLoadAllWithoutLocking(),
                CommandCarryOverPolicy.none());
    }

    /**
     * This input type is needed for deserializing data back into Java object
     */
    Class<I> getInputType();

    /**
     * Implement this method to execute the commands set up for this state.
     *
     * @param input            the state input which is deserialized by dataConverter with {@link #getInputType}
     * @param queryAttributes  the query attributes that can be used as readOnly
     * @param searchAttributes the search attributes that can be used as readOnly
     * @return the requested commands for this step
     * NOTE: it's readonly here for simplifying the implementation(execute can be reverted in some edge cases),
     *       We could change to support R+W if necessary.
     */
    CommandRequest start(
            final Context context, I input,
            final StateLocalAttributesW stateLocals, final SearchAttributesRW searchAttributes, final QueryAttributesRW queryAttributes);

    /**
     * Implement this method to decide what to do next when requested commands are ready
     *
     * @param input            the state input which is deserialized by dataConverter with {@link #getInputType}
     * @param commandResults   the results of the command that executed by {@link #start}
     * @param queryAttributes  the query attributes that can be used as Read+Write
     * @param searchAttributes the search attributes that can be used as Read+Write
     * @return the decision of what to do next(e.g. transition to next states)
     */
    StateDecision decide(
            final Context context, final I input, final CommandResults commandResults,
            final StateLocalAttributesR stateLocals, final SearchAttributesRW searchAttributes, final QueryAttributesRW queryAttributes);
}


