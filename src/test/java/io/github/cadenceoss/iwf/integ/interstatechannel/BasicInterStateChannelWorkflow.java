package io.github.cadenceoss.iwf.integ.interstatechannel;

import io.github.cadenceoss.iwf.core.StateDef;
import io.github.cadenceoss.iwf.core.Workflow;
import io.github.cadenceoss.iwf.core.command.SignalChannelDef;

import java.util.Arrays;
import java.util.List;

public class BasicInterStateChannelWorkflow implements Workflow {

    public static final String INTER_STATE_CHANNEL_NAME_1 = "test-signal-1";

    public static final String INTER_STATE_CHANNEL_NAME_2 = "test-signal-2";

    @Override
    public List<SignalChannelDef> getSignalChannels() {
        return Arrays.asList(
                SignalChannelDef.create(Integer.class, INTER_STATE_CHANNEL_NAME_1),
                SignalChannelDef.create(Integer.class, INTER_STATE_CHANNEL_NAME_2)
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
