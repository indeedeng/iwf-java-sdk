package io.iworkflow.core;

import io.iworkflow.core.communication.CommunicationMethodDef;
import io.iworkflow.core.persistence.DataAttributeDef;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is used to store both the nameToTypeStore and prefixToTypeStore for the
 * data attribute, signal, and internal channel
 * in registry.
 */
public class TypeStore {
    public enum Type {
        DATA_ATTRIBUTE,
        SIGNAL_CHANNEL,
        INTERNAL_CHANNEL,

    }

    public final Type classType;
    public final Map<String, Class<?>> nameToTypeStore;
    public final Map<String, Class<?>> prefixToTypeStore;

    private TypeStore(final Type classType, final Map<String, Class<?>> nameToTypeStore, final Map<String, Class<?>> prefixToTypeStore) {
        this.classType = classType;
        this.nameToTypeStore = nameToTypeStore;
        this.prefixToTypeStore = prefixToTypeStore;
    }

    public static TypeStore defaultBuilder(final Type classType) {
        return new TypeStore(classType, new HashMap<>(), new HashMap<>());
    }

    public Boolean isValidNameOrPrefix(final String name) {
        final Class<?> type = doGetType(name);
        return type != null;
    }

    public Class<?> getType(final String name) {
        final Class<?> type = doGetType(name);

        if (type == null) {
            throw new IllegalArgumentException(
                    String.format(
                            "%s not registered: %s",
                            classType,
                            name
                    )
            );
        }

        return type;
    }

    public void addDataAttributeToStore(final DataAttributeDef def) {
        final Map<String, Class<?>> store;
        if (def.isPrefix()) {
            store = prefixToTypeStore;
        } else {
            store = nameToTypeStore;
        }

        if (store.containsKey(def.getKey())) {
            throw new WorkflowDefinitionException(
                    String.format(
                            "%s key/prefix %s already exists",
                            classType,
                            def.getKey())
            );
        }
        store.put(def.getKey(), def.getDataAttributeType());
    }

    public void addChannelToStore(final CommunicationMethodDef def) {
        final Map<String, Class<?>> store;
        if (def.isPrefix()) {
            store = prefixToTypeStore;
        } else {
            store = nameToTypeStore;
        }

        if (store.containsKey(def.getName())) {
            throw new WorkflowDefinitionException(
                    String.format(
                            "%s name/prefix %s already exists",
                            classType,
                            def.getName())
            );
        }
        store.put(def.getName(), def.getType());
    }

    private Class<?> doGetType(final String name) {
        if (nameToTypeStore.containsKey(name)) {
            return nameToTypeStore.get(name);
        }

        final Optional<String> first = prefixToTypeStore.keySet().stream()
                .filter(name::startsWith)
                .findFirst();

        return first.map(prefixToTypeStore::get).orElse(null);
    }
}
