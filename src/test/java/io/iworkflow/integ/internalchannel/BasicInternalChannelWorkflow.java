package io.iworkflow.integ.internalchannel;

import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.StateDef;
import io.iworkflow.core.communication.CommunicationMethodDef;
import io.iworkflow.core.communication.InternalChannelDef;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BasicInternalChannelWorkflow implements ObjectWorkflow {
    public static final String INTER_STATE_CHANNEL_NAME_1 = "test-inter-state-channel-1";

    public static final String INTER_STATE_CHANNEL_NAME_2 = "test-inter-state-channel-2";
    public static final String INTER_STATE_CHANNEL_PREFIX_1 = "test-inter-state-channel-prefix-1-";

    @Override
    public List<CommunicationMethodDef> getCommunicationSchema() {
        return Arrays.asList(
                InternalChannelDef.create(Integer.class, INTER_STATE_CHANNEL_NAME_1),
                InternalChannelDef.create(Integer.class, INTER_STATE_CHANNEL_NAME_2),
                InternalChannelDef.createByPrefix(Integer.class, INTER_STATE_CHANNEL_PREFIX_1)
        );
    }

    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(
                StateDef.startingState(new BasicInternalChannelWorkflowState0()),
                StateDef.nonStartingState(new BasicInternalChannelWorkflowState1()),
                StateDef.nonStartingState(new BasicInternalChannelWorkflowState2())
        );
    }
}
