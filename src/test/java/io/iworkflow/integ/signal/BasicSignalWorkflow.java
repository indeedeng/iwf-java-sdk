package io.iworkflow.integ.signal;

import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.StateDef;
import io.iworkflow.core.communication.CommunicationMethodDef;
import io.iworkflow.core.communication.SignalChannelDef;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BasicSignalWorkflow implements ObjectWorkflow {

    public static final String SIGNAL_CHANNEL_NAME_1 = "test-signal-1";

    public static final String SIGNAL_CHANNEL_NAME_2 = "test-signal-2";

    public static final String SIGNAL_CHANNEL_NAME_3 = "test-signal-3";
    public static final String SIGNAL_CHANNEL_PREFIX_1 = "test-signal-prefix-1";

    @Override
    public List<CommunicationMethodDef> getCommunicationSchema() {
        return Arrays.asList(
                SignalChannelDef.create(Integer.class, SIGNAL_CHANNEL_NAME_1),
                SignalChannelDef.create(Integer.class, SIGNAL_CHANNEL_NAME_2),
                SignalChannelDef.create(Void.class, SIGNAL_CHANNEL_NAME_3),
                SignalChannelDef.createByPrefix(Integer.class, SIGNAL_CHANNEL_PREFIX_1)
        );
    }

    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(
                StateDef.startingState(new BasicSignalWorkflowState1()),
                StateDef.nonStartingState(new BasicSignalWorkflowState2())
        );
    }
}
