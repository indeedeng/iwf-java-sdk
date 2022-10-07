package iwf.integ.basic;

import iwf.core.StateDef;
import iwf.core.Workflow;
import iwf.core.command.SignalMethodDef;

import java.util.Arrays;
import java.util.List;

public class BasicWorkflow implements Workflow {

    public static final String TEST_SIGNAL_NAME = "test-signal-name";

    @Override
    public List<SignalMethodDef<?>> getSignalMethods() {
        return Arrays.asList(SignalMethodDef.create(String.class, TEST_SIGNAL_NAME));
    }

    @Override
    public List<StateDef> getStates() {
        return Arrays.asList(
                StateDef.startingState(new BasicWorkflowS1()),
                StateDef.nonStartingState(new BasicWorkflowS2())
        );
    }
}
