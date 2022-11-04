package io.github.cadenceoss.iwf.core.attributes;

import io.github.cadenceoss.iwf.core.ObjectEncoder;
import io.github.cadenceoss.iwf.gen.models.EncodedObject;
import io.github.cadenceoss.iwf.gen.models.KeyValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryAttributesRW extends AbstractAttributeStoreRW {
    private final Map<String, EncodedObject> queryAttributesToUpsert;

    public QueryAttributesRW(
            final Map<String, Class<?>> queryAttributeNameToTypeMap,
            final Map<String, EncodedObject> queryAttributeNameToValueMap,
            final ObjectEncoder objectEncoder) {
        super(queryAttributeNameToTypeMap, queryAttributeNameToValueMap, objectEncoder);
        this.queryAttributesToUpsert = new HashMap<>();
    }

    @Override
    public void set(String key, Object value) {
        super.set(key, value);
        this.queryAttributesToUpsert.put(key, objectEncoder.encode(value));
    }

    public List<KeyValue> getUpsertQueryAttributes() {
        return queryAttributesToUpsert.entrySet().stream()
                .map(stringEncodedObjectEntry ->
                        new KeyValue()
                                .key(stringEncodedObjectEntry.getKey())
                                .value(stringEncodedObjectEntry.getValue()))
                .collect(Collectors.toList());
    }
}
