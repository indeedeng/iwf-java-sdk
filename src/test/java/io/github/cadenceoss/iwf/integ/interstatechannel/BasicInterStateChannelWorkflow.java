package io.github.cadenceoss.iwf.integ.interstatechannel;

import io.github.cadenceoss.iwf.core.StateDef;
import io.github.cadenceoss.iwf.core.Workflow;
import io.github.cadenceoss.iwf.core.communication.CommunicationMethodDef;
import io.github.cadenceoss.iwf.core.communication.InterStateChannelDef;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BasicInterStateChannelWorkflow implements Workflow {
    public static final String INTER_STATE_CHANNEL_NAME_1 = "test-inter-state-channel-1";

    public static final String INTER_STATE_CHANNEL_NAME_2 = "test-inter-state-channel-2";

    @Override
    public List<CommunicationMethodDef> getCommunicationSchema() {
        return Arrays.asList(
                InterStateChannelDef.create(Integer.class, INTER_STATE_CHANNEL_NAME_1),
                InterStateChannelDef.create(Integer.class, INTER_STATE_CHANNEL_NAME_2)
        );
    }

    @Override
    public List<StateDef> getStates() {
        return Arrays.asList(
                StateDef.startingState(new BasicInterStateChannelWorkflowState0()),
                StateDef.nonStartingState(new BasicInterStateChannelWorkflowState1()),
                StateDef.nonStartingState(new BasicInterStateChannelWorkflowState2())
        );
    }
}
