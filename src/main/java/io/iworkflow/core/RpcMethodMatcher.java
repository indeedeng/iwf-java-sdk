package io.iworkflow.core;

import com.google.common.collect.ImmutableMap;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

import java.lang.reflect.Method;
import java.util.Map;

class RpcMethodMatcher {
    public static final Map<Integer, Class<?>> RPC_INPUT_PERSISTENCE_PARAM_TYPES =
            new ImmutableMap.Builder<Integer, Class<?>>()
                    .put(0, Context.class)
                    .put(2, Persistence.class)
                    .put(3, Communication.class)
                    .build();
    public static final RpcMethodMetadata INPUT_PERSISTENCE_RPC_METADATA =
            ImmutableRpcMethodMetadata.builder()
                    .hasInput(true)
                    .inputIndex(1)
                    .usesPersistence(true)
                    .build();

    public static final Map<Integer, Class<?>> RPC_INPUT_PARAM_TYPES =
            new ImmutableMap.Builder<Integer, Class<?>>()
                    .put(0, Context.class)
                    .put(2, Communication.class)
                    .build();
    public static final RpcMethodMetadata INPUT_RPC_METADATA =
            ImmutableRpcMethodMetadata.builder()
                    .hasInput(true)
                    .inputIndex(1)
                    .usesPersistence(false)
                    .build();


    public static final Map<Integer, Class<?>> RPC_PERSISTENCE_PARAM_TYPES =
            new ImmutableMap.Builder<Integer, Class<?>>()
                    .put(0, Context.class)
                    .put(1, Persistence.class)
                    .put(2, Communication.class)
                    .build();
    public static final RpcMethodMetadata PERSISTENCE_RPC_METADATA =
            ImmutableRpcMethodMetadata.builder()
                    .hasInput(false)
                    .inputIndex(-1)
                    .usesPersistence(true)
                    .build();

    public static final Map<Integer, Class<?>> RPC_PARAM_TYPES =
            new ImmutableMap.Builder<Integer, Class<?>>()
                    .put(0, Context.class)
                    .put(1, Communication.class)
                    .build();
    public static final RpcMethodMetadata RPC_METADATA =
            ImmutableRpcMethodMetadata.builder()
                    .hasInput(false)
                    .inputIndex(-1)
                    .usesPersistence(false)
                    .build();

    private static final int RPC_PARAM_COUNT_MAX = 4;
    private static final int RPC_PARAM_COUNT_MIN = 2;

    public static RpcMethodMetadata match(Method method) {
        final Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length < RPC_PARAM_COUNT_MIN || paramTypes.length > RPC_PARAM_COUNT_MAX) {
            return null;
        }

        switch (paramTypes.length) {
            case 2:
                if (validateInputParameters(paramTypes, RPC_PARAM_TYPES)) {
                    return RPC_METADATA;
                } else {
                    return null;
                }
            case 3:
                if (validateInputParameters(paramTypes, RPC_PERSISTENCE_PARAM_TYPES)) {
                    return PERSISTENCE_RPC_METADATA;
                } else if (validateInputParameters(paramTypes, RPC_INPUT_PARAM_TYPES)) {
                    return INPUT_RPC_METADATA;
                } else {
                    return null;
                }
            case 4:
                if (validateInputParameters(paramTypes, RPC_INPUT_PERSISTENCE_PARAM_TYPES)) {
                    return INPUT_PERSISTENCE_RPC_METADATA;
                } else {
                    return null;
                }
        }

        return null;
    }

    private static boolean validateInputParameters(Class<?>[] paramTypes, Map<Integer, Class<?>> expectedInputParamTypes) {
        for (Map.Entry<Integer, Class<?>> entry: expectedInputParamTypes.entrySet()) {
            if (entry.getKey() >= paramTypes.length) {
                return false;
            }

            if (!paramTypes[entry.getKey()].equals(entry.getValue())) {
                return false;
            }
        }

        return true;
    }
}
