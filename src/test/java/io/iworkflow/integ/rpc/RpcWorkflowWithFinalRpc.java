package io.iworkflow.integ.rpc;

import io.iworkflow.core.Context;
import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.RPC;
import io.iworkflow.core.StateDef;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RpcWorkflowWithFinalRpc implements ObjectWorkflow {

    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(
                StateDef.startingState(new RpcWorkflowState1()),
                StateDef.nonStartingState(new RpcWorkflowState2())
        );
    }

    @RPC
    public final void testRpc(Context context, String input, Persistence persistence, Communication communication) {
        return;
    }
}