package io.iworkflow.core;

import io.iworkflow.core.command.CommandCarryOverPolicy;
import io.iworkflow.core.command.CommandCarryOverType;
import io.iworkflow.core.persistence.PersistenceLoadingPolicy;
import org.immutables.value.Value;

@Value.Immutable
public abstract class StateOptions {

    /**
     * when using forAnyCommandClosed or forAnyCommandsCompleted
     * there could be some unfinished commands left to this state. This policy decided whether and how to carry over those unfinished command to
     * future states. Default to {@link CommandCarryOverType#NONE} which means no carry over.
     */
    public abstract CommandCarryOverPolicy getCommandCarryOverPolicy();

    /**
     * this decides whether to load all the query attributes into {@link WorkflowState#decide} and {@link WorkflowState#start} method
     * default to true
     */
    public abstract PersistenceLoadingPolicy getQueryAttributesLoadingPolicy();

    /**
     * this decides whether to load all the search attributes into {@link WorkflowState#decide} and {@link WorkflowState#start} method
     * default to true
     */
    public abstract PersistenceLoadingPolicy getSearchAttributesLoadingPolicy();
}
