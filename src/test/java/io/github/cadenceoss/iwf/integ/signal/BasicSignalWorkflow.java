package io.github.cadenceoss.iwf.integ.signal;

import io.github.cadenceoss.iwf.core.StateDef;
import io.github.cadenceoss.iwf.core.Workflow;
import io.github.cadenceoss.iwf.core.communication.SignalChannel;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BasicSignalWorkflow implements Workflow {

    @Override
    public List<SignalChannel> getSignalChannels() {
        return Arrays.asList(
                SignalChannel.create(Integer.class, BasicSignalWorkflowState1.SIGNAL_CHANNEL_NAME_1),
                SignalChannel.create(Integer.class, BasicSignalWorkflowState1.SIGNAL_CHANNEL_NAME_2)
        );
    }

    @Override
    public List<StateDef> getStates() {
        return Arrays.asList(
                StateDef.startingState(new BasicSignalWorkflowState1())
        );
    }
}
