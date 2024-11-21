package io.iworkflow.integ.rpc;

import io.iworkflow.core.Context;
import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.RPC;
import io.iworkflow.core.StateDef;
import io.iworkflow.core.StateMovement;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.communication.CommunicationMethodDef;
import io.iworkflow.core.communication.InternalChannelDef;
import io.iworkflow.core.persistence.DataAttributeDef;
import io.iworkflow.core.persistence.Persistence;
import io.iworkflow.core.persistence.PersistenceFieldDef;
import io.iworkflow.core.persistence.SearchAttributeDef;
import io.iworkflow.gen.models.PersistenceLoadingType;
import io.iworkflow.gen.models.SearchAttributeValueType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static io.iworkflow.integ.RpcTest.HARDCODED_STR;
import static io.iworkflow.integ.RpcTest.RPC_OUTPUT;

@Component
public class RpcLockingWorkflow implements ObjectWorkflow {

    public static final String RPC_INTERNAL_CHANNEL_NAME = "rpc-channel-1";
    public static final String TEST_DATA_OBJECT_KEY = "data-obj-1";
    public static final String TEST_SEARCH_ATTRIBUTE_KEYWORD = "CustomKeywordField";
    public static final String TEST_SEARCH_ATTRIBUTE_INT = "CustomIntField";

    @Override
    public List<CommunicationMethodDef> getCommunicationSchema() {
        return Arrays.asList(
                InternalChannelDef.create(Void.class, RPC_INTERNAL_CHANNEL_NAME)
        );
    }

    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(
                StateDef.startingState(new RpcLockingWorkflowState1()),
                StateDef.nonStartingState(new RpcLockingWorkflowState2())
        );
    }

    @Override
    public List<PersistenceFieldDef> getPersistenceSchema() {
        return Arrays.asList(
                DataAttributeDef.create(String.class, TEST_DATA_OBJECT_KEY),
                SearchAttributeDef.create(SearchAttributeValueType.INT, TEST_SEARCH_ATTRIBUTE_INT),
                SearchAttributeDef.create(SearchAttributeValueType.KEYWORD, TEST_SEARCH_ATTRIBUTE_KEYWORD)
        );
    }

    @RPC(
            dataAttributesLoadingType = PersistenceLoadingType.PARTIAL_WITH_EXCLUSIVE_LOCK,
            dataAttributesPartialLoadingKeys = {TEST_DATA_OBJECT_KEY}
    )
    public void testRpcWithLocking(Context context, Persistence persistence, Communication communication) {
        if (context.getWorkflowId().isEmpty() || context.getWorkflowRunId().isEmpty()) {
            throw new RuntimeException("invalid context");
        }
        persistence.setDataAttribute(TEST_DATA_OBJECT_KEY, HARDCODED_STR);
        persistence.setSearchAttributeKeyword(TEST_SEARCH_ATTRIBUTE_KEYWORD, HARDCODED_STR);
        persistence.setSearchAttributeInt64(TEST_SEARCH_ATTRIBUTE_INT, RPC_OUTPUT);
        communication.publishInternalChannel(RPC_INTERNAL_CHANNEL_NAME, null);
        communication.triggerStateMovements(StateMovement.create(RpcLockingWorkflowState2.class));
    }

    @RPC
    public void testRpcWithoutLocking(Context context, Persistence persistence, Communication communication) {
        if (context.getWorkflowId().isEmpty() || context.getWorkflowRunId().isEmpty()) {
            throw new RuntimeException("invalid context");
        }
        persistence.setDataAttribute(TEST_DATA_OBJECT_KEY, HARDCODED_STR);
        persistence.setSearchAttributeKeyword(TEST_SEARCH_ATTRIBUTE_KEYWORD, HARDCODED_STR);
        persistence.setSearchAttributeInt64(TEST_SEARCH_ATTRIBUTE_INT, RPC_OUTPUT);
        communication.publishInternalChannel(RPC_INTERNAL_CHANNEL_NAME, null);
        communication.triggerStateMovements(StateMovement.create(RpcLockingWorkflowState2.class));
    }
}
