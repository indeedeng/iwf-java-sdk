package io.github.cadenceoss.iwf.integ.query;

import io.github.cadenceoss.iwf.core.StateDef;
import io.github.cadenceoss.iwf.core.Workflow;
import io.github.cadenceoss.iwf.core.attributes.QueryAttributeDef;

import java.util.Arrays;
import java.util.List;

public class BasicQueryWorkflow implements Workflow {
    public static final String ATTRIBUTE_KEY = "text";
    @Override
    public List<StateDef> getStates() {
        return Arrays.asList(StateDef.startingState(new BasicQueryWorkflowState1()));
    }

    @Override
    public List<QueryAttributeDef> getQueryAttributes() {
        return Arrays.asList(QueryAttributeDef.create(String.class, ATTRIBUTE_KEY));
    }
}
