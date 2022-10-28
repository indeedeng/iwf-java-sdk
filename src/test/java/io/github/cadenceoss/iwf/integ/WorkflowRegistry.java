package io.github.cadenceoss.iwf.integ;

import io.github.cadenceoss.iwf.core.Registry;
import io.github.cadenceoss.iwf.integ.basic.BasicWorkflow;
import io.github.cadenceoss.iwf.integ.query.BasicQueryWorkflow;
import io.github.cadenceoss.iwf.integ.signal.BasicSignalWorkflow;
import io.github.cadenceoss.iwf.integ.timer.BasicTimerWorkflow;

public class WorkflowRegistry {
    public static final Registry registry = new Registry();

    static {
        registry.addWorkflow(new BasicWorkflow());
        registry.addWorkflow(new BasicSignalWorkflow());
        registry.addWorkflow(new BasicQueryWorkflow());
        registry.addWorkflow(new BasicTimerWorkflow());
    }
}