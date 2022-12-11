package io.github.cadenceoss.iwf.integ.signal;

import io.github.cadenceoss.iwf.core.StateDef;
import io.github.cadenceoss.iwf.core.Workflow;
import io.github.cadenceoss.iwf.core.communication.CommunicationMethodDef;
import io.github.cadenceoss.iwf.core.communication.SignalChannelDef;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BasicSignalWorkflow implements Workflow {

    @Override
    public List<CommunicationMethodDef> getCommunicationSchema() {
        return Arrays.asList(
                SignalChannelDef.create(Integer.class, BasicSignalWorkflowState1.SIGNAL_CHANNEL_NAME_1),
                SignalChannelDef.create(Integer.class, BasicSignalWorkflowState1.SIGNAL_CHANNEL_NAME_2)
        );
    }

    @Override
    public List<StateDef> getStates() {
        return Arrays.asList(
                StateDef.startingState(new BasicSignalWorkflowState1())
        );
    }
}
