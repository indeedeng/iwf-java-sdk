package io.github.cadenceoss.iwf.integ.basic;

import io.github.cadenceoss.iwf.core.StateDef;
import io.github.cadenceoss.iwf.core.Workflow;
import io.github.cadenceoss.iwf.core.command.SignalMethodDef;

import java.util.Arrays;
import java.util.List;

public class BasicSignalWorkflow implements Workflow {
    @Override
    public List<SignalMethodDef<?>> getSignalMethods() {
        return Arrays.asList(SignalMethodDef.create(Integer.class, BasicSignalWorkflowState1.SIGNAL_NAME));
    }

    @Override
    public List<StateDef> getStates() {
        return Arrays.asList(
                StateDef.startingState(new BasicSignalWorkflowState1())
        );
    }
}
