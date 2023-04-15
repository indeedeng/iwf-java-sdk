package io.iworkflow.integ.signal;

import io.iworkflow.core.DEObject;
import io.iworkflow.core.StateDef;
import io.iworkflow.core.communication.CommunicationMethodDef;
import io.iworkflow.core.communication.SignalChannelDef;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BasicSignalWorkflow implements DEObject {

    public static final String SIGNAL_CHANNEL_NAME_1 = "test-signal-1";

    public static final String SIGNAL_CHANNEL_NAME_2 = "test-signal-2";

    public static final String SIGNAL_CHANNEL_NAME_3 = "test-signal-3";

    @Override
    public List<CommunicationMethodDef> getCommunicationSchema() {
        return Arrays.asList(
                SignalChannelDef.create(Integer.class, SIGNAL_CHANNEL_NAME_1),
                SignalChannelDef.create(Integer.class, SIGNAL_CHANNEL_NAME_2),
                SignalChannelDef.create(Void.class, SIGNAL_CHANNEL_NAME_3)
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
