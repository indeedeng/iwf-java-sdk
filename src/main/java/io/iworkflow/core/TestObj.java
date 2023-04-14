package io.iworkflow.core;

import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.communication.CommunicationMethodDef;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.core.persistence.PersistenceFieldDef;
import io.iworkflow.gen.models.PersistenceLoadingType;

import java.util.List;

public class TestObj implements ObjectWorkflow {

    public static void main(String[] args) {
        Registry registry = new Registry();
        final Client client = new Client(registry, ClientOptions.localDefault);
        final TestObj stub = client.newRpcStub(TestObj.class, "test-wf-id", "");
        Integer out = client.invokeRPC(stub::testRpc, "123");
        System.out.println("out:" + out);

        out = client.invokeRPC(stub::testWrongRpc, "123");
        System.out.println("out:" + out);
    }

    @RPC(
            timeoutSeconds = 10,
            dataAttributesLoadingType = PersistenceLoadingType.PARTIAL_WITHOUT_LOCKING,
            dataAttributesPartialLoadingKeys = {"123"}
    )
    public Integer testRpc(String input, Persistence persistence, Communication communication) {
        throw new IllegalStateException("should not be called");
    }

    public Integer testWrongRpc(String input, Persistence persistence, Communication communication) {
        throw new IllegalStateException("should not be called");
    }

    @Override
    public List<StateDef> getWorkflowStates() {
        return null;
    }

    @Override
    public List<PersistenceFieldDef> getPersistenceSchema() {
        return ObjectWorkflow.super.getPersistenceSchema();
    }

    @Override
    public List<CommunicationMethodDef> getCommunicationSchema() {
        return ObjectWorkflow.super.getCommunicationSchema();
    }

    @Override
    public String getWorkflowType() {
        return ObjectWorkflow.super.getWorkflowType();
    }
}
