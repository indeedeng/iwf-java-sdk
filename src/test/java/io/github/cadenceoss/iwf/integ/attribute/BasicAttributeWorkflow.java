package io.github.cadenceoss.iwf.integ.attribute;

import io.github.cadenceoss.iwf.core.StateDef;
import io.github.cadenceoss.iwf.core.Workflow;
import io.github.cadenceoss.iwf.core.attributes.QueryAttributeDef;
import io.github.cadenceoss.iwf.core.attributes.SearchAttributeDef;
import io.github.cadenceoss.iwf.core.attributes.SearchAttributeType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BasicAttributeWorkflow implements Workflow {
    public static final String TEST_QUERY_ATTRIBUTE_KEY = "query-attr-1";

    public static final String TEST_SEARCH_ATTRIBUTE_KEYWORD = "CustomKeywordField";
    public static final String TEST_SEARCH_ATTRIBUTE_INT = "CustomIntField";

    @Override
    public List<StateDef> getStates() {
        return Arrays.asList(StateDef.startingState(new BasicAttributeWorkflowState1()));
    }

    @Override
    public List<QueryAttributeDef> getQueryAttributes() {
        return Arrays.asList(QueryAttributeDef.create(String.class, TEST_QUERY_ATTRIBUTE_KEY));
    }

    @Override
    public List<SearchAttributeDef> getSearchAttributes() {
        return Arrays.asList(
                SearchAttributeDef.create(SearchAttributeType.INT_64, TEST_SEARCH_ATTRIBUTE_INT),
                SearchAttributeDef.create(SearchAttributeType.KEYWORD, TEST_SEARCH_ATTRIBUTE_KEYWORD)
        );
    }
}
