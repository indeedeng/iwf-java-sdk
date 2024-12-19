package io.iworkflow.integ.stateoptionsoverride;

import io.iworkflow.core.Context;
import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.StateDef;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.WorkflowStateOptions;
import io.iworkflow.core.command.CommandRequest;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.gen.models.RetryPolicy;
import io.iworkflow.gen.models.WaitUntilApiFailurePolicy;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class StateOptionsOverrideWorkflow implements ObjectWorkflow {

    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(
                StateDef.startingState(new StateOptionsOverrideWorkflowState1()),
                StateDef.nonStartingState(new StateOptionsOverrideWorkflowState2())
        );
    }
}

class StateOptionsOverrideWorkflowState1 implements WorkflowState<String> {
    private String output = "";

    @Override
    public Class<String> getInputType() {
        return String.class;
    }

    @Override
    public CommandRequest waitUntil(Context context, String input, Persistence persistence, Communication communication) {
        output = input + "_state1_start";
        return CommandRequest.empty;
    }

    @Override
    public StateDecision execute(Context context, String input, CommandResults commandResults, Persistence persistence, Communication communication) {
        output = output + "_state1_decide";
        return StateDecision.singleNextState(
                StateOptionsOverrideWorkflowState2.class, output,
                new WorkflowStateOptions()
                        .setWaitUntilApiRetryPolicy(new RetryPolicy().maximumAttempts(2))
                        .setProceedToExecuteWhenWaitUntilRetryExhausted(true)
        );
    }
}

class StateOptionsOverrideWorkflowState2 implements WorkflowState<String> {
    private String output = "";

    @Override
    public Class<String> getInputType() {
        return String.class;
    }

    @Override
    public CommandRequest waitUntil(Context context, String input, Persistence persistence, Communication communication) {
        output = input + "_state2_start";
        throw new RuntimeException("");
    }

    @Override
    public StateDecision execute(Context context, String input, CommandResults commandResults, Persistence persistence, Communication communication) {
        output = output + "_state2_decide";
        return StateDecision.gracefulCompleteWorkflow(output);
    }

    @Override
    public WorkflowStateOptions getStateOptions() {
        return new WorkflowStateOptions()
                .setWaitUntilApiRetryPolicy(new RetryPolicy().maximumAttempts(1))
                .setProceedToExecuteWhenWaitUntilRetryExhausted(false);
    }
}