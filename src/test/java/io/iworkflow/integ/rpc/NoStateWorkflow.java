package io.iworkflow.integ.rpc;

import io.iworkflow.core.Context;
import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.RPC;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.DataAttributeDef;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.core.persistence.PersistenceFieldDef;
import io.iworkflow.gen.models.PersistenceLoadingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static io.iworkflow.integ.RpcTest.RPC_OUTPUT;

@Component
public class NoStateWorkflow implements ObjectWorkflow {

    public static final String DA_COUNTER = "counter";
    private RpcWorkflow rpcWorkflow;
    private NoStartStateWorkflow noStartStateWorkflow;

    @Autowired
    public NoStateWorkflow(RpcWorkflow rpcWorkflow, NoStartStateWorkflow noStartStateWorkflow) {
        this.rpcWorkflow = rpcWorkflow;
        this.noStartStateWorkflow = noStartStateWorkflow;
    }

    @Override
    public List<PersistenceFieldDef> getPersistenceSchema() {
        return Arrays.asList(
                DataAttributeDef.create(Integer.class, DA_COUNTER)
        );
    }

    @RPC(
            dataAttributesLoadingType = PersistenceLoadingType.PARTIAL_WITH_EXCLUSIVE_LOCK,
            dataAttributesPartialLoadingKeys = {DA_COUNTER},
            dataAttributesLockingKeys = {DA_COUNTER}
    )
    public String increaseCounter(Context context, Persistence persistence, Communication communication) {
        Integer current = persistence.getDataAttribute(DA_COUNTER, Integer.class);
        if (current == null) {
            current = 0;
        }
        current++;
        persistence.setDataAttribute(DA_COUNTER, current);
        return "done";
    }

    @RPC
    public String testWrite(Context context, Persistence persistence, Communication communication) {
        persistence.setDataAttribute(DA_COUNTER, 123);
        return "done";
    }

    @RPC
    public Integer getCounter(Context context, Persistence persistence, Communication communication) {
        return persistence.getDataAttribute(DA_COUNTER, Integer.class);
    }

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
