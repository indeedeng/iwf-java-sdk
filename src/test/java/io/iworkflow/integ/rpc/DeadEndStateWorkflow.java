package io.iworkflow.integ.rpc;

import io.iworkflow.core.Context;
import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.RPC;
import io.iworkflow.core.StateDecision;
import io.iworkflow.core.StateDef;
import io.iworkflow.core.StateMovement;
import io.iworkflow.core.WorkflowState;
import io.iworkflow.core.command.CommandResults;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.communication.CommunicationMethodDef;
import io.iworkflow.core.communication.InternalChannelDef;
import io.iworkflow.core.communication.SignalChannelDef;
import io.iworkflow.core.persistence.Persistence;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static io.iworkflow.integ.RpcTest.RPC_OUTPUT;

@Component
public class DeadEndStateWorkflow implements ObjectWorkflow {

    public static final String IDLE_INTERNAL_CHANNEL = "ideal-internal-channel";
    public static final String IDLE_SIGNAL_CHANNEL = "ideal-signal-channel";
    @Override
    public List<CommunicationMethodDef> getCommunicationSchema() {
        return Arrays.asList(
                InternalChannelDef.create(Void.class, IDLE_INTERNAL_CHANNEL),
                SignalChannelDef.create(Void.class, IDLE_SIGNAL_CHANNEL)
        );
    }

    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(
                StateDef.startingState(new DeadEndState()),
                StateDef.nonStartingState(new RpcWorkflowState2())
        );
    }

    @RPC
    public int getSignalChannelSize(Context context, Persistence persistence, Communication communication) {
        return communication.getSignalChannelSize(IDLE_SIGNAL_CHANNEL);
    }

    @RPC
    public int sendAndGetInternalChannelSize(Context context,  Persistence persistence, Communication communication) {
        communication.publishInternalChannel(IDLE_INTERNAL_CHANNEL, null);
        return communication.getInternalChannelSize(IDLE_INTERNAL_CHANNEL);
    }
    @RPC
    public Long testRpcFunc1(Context context, String input, Persistence persistence, Communication communication) {
        if (context.getWorkflowId().isEmpty() || context.getWorkflowRunId().isEmpty() ||
                context.getWorkflowType().isEmpty() || !context.getWorkflowType().equals("DeadEndStateWorkflow") ) {
            throw new RuntimeException("invalid context");
        }
        communication.triggerStateMovements(StateMovement.create(RpcWorkflowState2.class));
        return RPC_OUTPUT;
    }
}

class DeadEndState implements WorkflowState<Void>{

    @Override
    public Class<Void> getInputType() {
        return Void.class;
    }

    @Override
    public StateDecision execute(final Context context, final Void input, final CommandResults commandResults, final Persistence persistence, final Communication communication) {
        return StateDecision.deadEnd();
    }
}
