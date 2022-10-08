package io.github.cadenceoss.iwf.core;

import io.github.cadenceoss.iwf.core.attributes.AttributeLoadingPolicy;
import io.github.cadenceoss.iwf.core.command.BaseCommand;
import io.github.cadenceoss.iwf.core.command.CommandCarryOverPolicy;
import io.github.cadenceoss.iwf.core.command.CommandCarryOverType;
import io.github.cadenceoss.iwf.core.command.CommandRequest;
import org.immutables.value.Value;

@Value.Immutable
public abstract class StateOptions {

    /**
     * when using {@link CommandRequest#forAnyCommandClosed or {@link CommandRequest#forAnyCommandsCompleted(BaseCommand...)}
     * there could be some unfinished commands left to this state. This policy decided whether and how to carry over those unfinished command to
     * future states. Default to {@link CommandCarryOverType#NONE} which means no carry over.
     */
    public abstract CommandCarryOverPolicy getCommandCarryOverPolicy();

    /**
     * this decides whether to load all the query attributes into {@link WorkflowState#decide} and {@link WorkflowState#start} method
     * default to true
     */
    public abstract AttributeLoadingPolicy getQueryAttributesLoadingPolicy();

    /**
     * this decides whether to load all the search attributes into {@link WorkflowState#decide} and {@link WorkflowState#start} method
     * default to true
     */
    public abstract AttributeLoadingPolicy getSearchAttributesLoadingPolicy();
}
