package io.iworkflow.integ.persistence;

import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.StateDef;
import io.iworkflow.core.persistence.DataAttributeDef;
import io.iworkflow.core.persistence.PersistenceFieldDef;
import io.iworkflow.gen.models.Context;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SetDataObjectWorkflow implements ObjectWorkflow {
    public static final String DATA_OBJECT_KEY = "data-obj-key-1";
    public static final String DATA_OBJECT_MODEL_KEY = "data-obj-1";
    public static final String DATA_OBJECT_KEY_PREFIX = "data-obj-key-prefix-";

    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(StateDef.startingState(new SetDataObjectWorkflowState1()));
    }

    @Override
    public List<PersistenceFieldDef> getPersistenceSchema() {
        return Arrays.asList(
                DataAttributeDef.create(String.class, DATA_OBJECT_KEY),
                DataAttributeDef.create(Context.class, DATA_OBJECT_MODEL_KEY),
                DataAttributeDef.createByPrefix(Long.class, DATA_OBJECT_KEY_PREFIX)
        );
    }
}
