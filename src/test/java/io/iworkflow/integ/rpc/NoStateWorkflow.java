package io.iworkflow.integ.rpc;

import io.iworkflow.core.Context;
import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.RPC;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;
import org.springframework.stereotype.Component;

import static io.iworkflow.integ.RpcTest.RPC_OUTPUT;

@Component
public class NoStateWorkflow implements ObjectWorkflow {

    @RPC
    public Long testRpcFunc1(Context context, String input, Persistence persistence, Communication communication) {
        if (context.getWorkflowId().isEmpty() || context.getWorkflowRunId().isEmpty()) {
            throw new RuntimeException("invalid context");
        }
        return RPC_OUTPUT;
    }

    @RPC
    public Long testRpcFunc1Error(Context context, String input, Persistence persistence, Communication communication) {
        throw new RuntimeException("this is an error");
    }
}
