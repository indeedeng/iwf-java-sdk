package io.github.cadenceoss.iwf.integ.attribute;

import io.github.cadenceoss.iwf.core.StateDef;
import io.github.cadenceoss.iwf.core.Workflow;
import io.github.cadenceoss.iwf.core.persistence.DataObjectFieldDef;
import io.github.cadenceoss.iwf.core.persistence.PersistenceFieldDef;
import io.github.cadenceoss.iwf.core.persistence.SearchAttributeFieldDef;
import io.github.cadenceoss.iwf.core.persistence.SearchAttributeType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BasicAttributeWorkflow implements Workflow {
    public static final String TEST_DATA_OBJECT_KEY = "data-obj-1";

    public static final String TEST_SEARCH_ATTRIBUTE_KEYWORD = "CustomKeywordField";
    public static final String TEST_SEARCH_ATTRIBUTE_INT = "CustomIntField";

    @Override
    public List<StateDef> getStates() {
        return Arrays.asList(StateDef.startingState(new BasicAttributeWorkflowState1()));
    }

    @Override
    public List<PersistenceFieldDef> getPersistenceSchema() {
        return Arrays.asList(
                DataObjectFieldDef.create(String.class, TEST_DATA_OBJECT_KEY),
                SearchAttributeFieldDef.create(SearchAttributeType.INT_64, TEST_SEARCH_ATTRIBUTE_INT),
                SearchAttributeFieldDef.create(SearchAttributeType.KEYWORD, TEST_SEARCH_ATTRIBUTE_KEYWORD)
        );
    }
}
