package io.iworkflow.integ.persistence;

import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.StateDef;
import io.iworkflow.core.persistence.DataAttributeDef;
import io.iworkflow.core.persistence.PersistenceFieldDef;
import io.iworkflow.core.persistence.SearchAttributeDef;
import io.iworkflow.gen.models.Context;
import io.iworkflow.gen.models.SearchAttributeValueType;
import io.iworkflow.integ.basic.FakContextImpl;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BasicPersistenceWorkflow implements ObjectWorkflow {
    public static final String TEST_INIT_DATA_OBJECT_KEY = "data-obj-0";
    public static final String TEST_DATA_OBJECT_KEY = "data-obj-1";
    public static final String TEST_DATA_OBJECT_MODEL_1 = "data-obj-2";

    public static final String TEST_DATA_OBJECT_MODEL_2 = "data-obj-3";
    public static final String TEST_DATA_OBJECT_PREFIX = "data-obj-prefix-";

    public static final String TEST_SEARCH_ATTRIBUTE_KEYWORD = "CustomKeywordField";
    public static final String TEST_SEARCH_ATTRIBUTE_INT = "CustomIntField";

    public static final String TEST_SEARCH_ATTRIBUTE_DATE_TIME = "CustomDatetimeField";

    @Override
    public List<StateDef> getWorkflowStates() {
        return Arrays.asList(StateDef.startingState(new BasicPersistenceWorkflowState1()));
    }

    @Override
    public List<PersistenceFieldDef> getPersistenceSchema() {
        return Arrays.asList(
                DataAttributeDef.create(String.class, TEST_INIT_DATA_OBJECT_KEY),
                DataAttributeDef.create(String.class, TEST_DATA_OBJECT_KEY),
                DataAttributeDef.create(Context.class, TEST_DATA_OBJECT_MODEL_1),
                DataAttributeDef.create(FakContextImpl.class, TEST_DATA_OBJECT_MODEL_2),
                DataAttributeDef.createByPrefix(Long.class, TEST_DATA_OBJECT_PREFIX),
                SearchAttributeDef.create(SearchAttributeValueType.INT, TEST_SEARCH_ATTRIBUTE_INT),
                SearchAttributeDef.create(SearchAttributeValueType.KEYWORD, TEST_SEARCH_ATTRIBUTE_KEYWORD),
                SearchAttributeDef.create(SearchAttributeValueType.DATETIME, TEST_SEARCH_ATTRIBUTE_DATE_TIME)
        );
    }
}
