package io.iworkflow.integ.basic;

import com.google.common.collect.ImmutableList;
import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.StateDef;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AbnormalExitWorkflow implements ObjectWorkflow {

    @Override
    public List<StateDef> getWorkflowStates() {
        return ImmutableList.of(StateDef.startingState(new AbnormalExitState1()));
    }
}
