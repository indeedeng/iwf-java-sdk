package io.iworkflow.core;

import com.google.common.collect.ImmutableMap;
import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

public final class RpcDefinitions {
    private RpcDefinitions() {
    }

    /**
     * RPC definition
     * with: input, output, persistence, communication
     * without: NA
     * @param <I> input type
     * @param <O> output type
     */
    @FunctionalInterface
    public interface RpcFunc1<I, O> extends Serializable {
        O execute(Context context, I input, Persistence persistence, Communication communication);
    }

    /**
     * RPC definition
     * with: input, output, communication
     * without: persistence
     * @param <I> input type
     * @param <O> output type
     */
    @FunctionalInterface
    public interface RpcFunc1NoPersistence<I, O> extends Serializable {
        O execute(Context context, I input, Communication communication);
    }

    /**
     * RPC definition
     * with: output, persistence, communication
     * without: input
     * @param <O> output type
     */
    @FunctionalInterface
    public interface RpcFunc0<O> extends Serializable {
        O execute(Context context, Persistence persistence, Communication communication);
    }

    /**
     * RPC definition
     * with: output, communication
     * without: input, persistence
     * @param <O> output type
     */
    @FunctionalInterface
    public interface RpcFunc0NoPersistence<O> extends Serializable {
        O execute(Context context, Communication communication);
    }

    /**
     * RPC definition
     *  with: input, persistence, communication
     *  without: output
     * @param <I> input type
     */
    @FunctionalInterface
    public interface RpcProc1<I> extends Serializable {
        void execute(Context context, I input, Persistence persistence, Communication communication);
    }

    /**
     * RPC definition
     * with: input, communication
     * without: output, persistence
     * @param <I> input type
     */
    @FunctionalInterface
    public interface RpcProc1NoPersistence<I> extends Serializable {
        void execute(Context context, I input, Communication communication);
    }

    /**
     * RPC definition
     * with: persistence, communication
     * without: input, output
     */
    @FunctionalInterface
    public interface RpcProc0 extends Serializable {
        void execute(Context context, Persistence persistence, Communication communication);
    }

    /**
     * RPC definition
     * with: communication
     * without: input, output, persistence
     */
    @FunctionalInterface
    public interface RpcProc0NoPersistence extends Serializable {
        void execute(Context context, Communication communication);
    }

    public static final int PARAMETERS_WITH_INPUT = 4;
    public static final int PARAMETERS_NO_INPUT = 3;

    public static final int INDEX_OF_INPUT_PARAMETER = 1;

    public static final String ERROR_MESSAGE = "An RPC method must be in the form of one of {@link RpcDefinitions}";

    public static final Map<Integer, Class<?>> RPC_INPUT_PERSISTENCE_PARAM_TYPES =
            new ImmutableMap.Builder<Integer, Class<?>>()
                    .put(0, Context.class)
                    .put(2, Persistence.class)
                    .put(3, Communication.class)
                    .build();
    public static final Map<Integer, Class<?>> RPC_INPUT_PARAM_TYPES =
            new ImmutableMap.Builder<Integer, Class<?>>()
                    .put(0, Context.class)
                    .put(2, Communication.class)
                    .build();
    public static final Map<Integer, Class<?>> RPC_PERSISTENCE_PARAM_TYPES =
            new ImmutableMap.Builder<Integer, Class<?>>()
                    .put(0, Context.class)
                    .put(1, Persistence.class)
                    .put(2, Communication.class)
                    .build();
    public static final Map<Integer, Class<?>> RPC_PARAM_TYPES =
            new ImmutableMap.Builder<Integer, Class<?>>()
                    .put(0, Context.class)
                    .put(1, Communication.class)
                    .build();

    public static void validateRpcMethod(final Method method) {
        final Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length < 2 || paramTypes.length > 4) {
            throw new WorkflowDefinitionException(ERROR_MESSAGE);
        }

        switch (paramTypes.length) {
            case 2:
                if (!validateInputParameters(paramTypes, RPC_PARAM_TYPES)) {
                    throw new WorkflowDefinitionException(ERROR_MESSAGE);
                }
                break;
            case 3:
                if (!validateInputParameters(paramTypes, RPC_PERSISTENCE_PARAM_TYPES)
                        || !validateInputParameters(paramTypes, RPC_INPUT_PARAM_TYPES)) {
                    throw new WorkflowDefinitionException(ERROR_MESSAGE);
                }
                break;
            case 4:
                if (!validateInputParameters(paramTypes, RPC_INPUT_PERSISTENCE_PARAM_TYPES)) {
                    throw new WorkflowDefinitionException(ERROR_MESSAGE);
                }
                break;
            default:
                throw new WorkflowDefinitionException(ERROR_MESSAGE);
        }
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

    public static void validateRpcMethodTest(final Method method) {
        final Class<?>[] paramTypes = method.getParameterTypes();
        final Class<?> persistenceType, communicationType, contextType;
        if (paramTypes.length == PARAMETERS_NO_INPUT) {
            contextType = paramTypes[0];
            persistenceType = paramTypes[1];
            communicationType = paramTypes[2];
        } else if (paramTypes.length == PARAMETERS_WITH_INPUT) {
            contextType = paramTypes[0];
            persistenceType = paramTypes[2];
            communicationType = paramTypes[3];
        } else {
            throw new WorkflowDefinitionException(ERROR_MESSAGE);
        }
        if (!persistenceType.equals(Persistence.class) || !communicationType.equals(Communication.class)) {
            throw new WorkflowDefinitionException(ERROR_MESSAGE);
        }
    }
}