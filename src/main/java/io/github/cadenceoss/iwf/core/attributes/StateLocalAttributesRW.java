package io.github.cadenceoss.iwf.core.attributes;

import io.github.cadenceoss.iwf.core.ObjectEncoder;
import io.github.cadenceoss.iwf.gen.models.EncodedObject;

import java.util.Map;

public class StateLocalAttributesRW extends AbstractAttributeStoreRW {
    public StateLocalAttributesRW(final Map<String, Class<?>> queryAttributeNameToTypeMap, final Map<String, EncodedObject> queryAttributeNameToValueMap, final ObjectEncoder objectEncoder) {
        super(queryAttributeNameToTypeMap, queryAttributeNameToValueMap, objectEncoder);
    }
}
