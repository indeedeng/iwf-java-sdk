package io.iworkflow.core;

import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

import java.io.Serializable;
import java.lang.reflect.Method;

public final class RpcDefinitions {
    private RpcDefinitions() {
    }

    /**
     * RPC with input and output
     *
     * @param <I> input type
     * @param <O> output type
     */
    @FunctionalInterface
    public interface RpcFunc1<I, O> extends Serializable {
        O execute(Context context, I input, Persistence persistence, Communication communication);
    }

    /**
     * RPC with output only
     *
     * @param <O> output type
     */
    @FunctionalInterface
    public interface RpcFunc0<O> extends Serializable {
        O execute(Context context, Persistence persistence, Communication communication);
    }

    /**
     * RPC with input only
     *
     * @param <I> input type
     */
    @FunctionalInterface
    public interface RpcProc1<I> extends Serializable {
        void execute(Context context, I input, Persistence persistence, Communication communication);
    }

    /**
     * RPC without input or output
     */
    @FunctionalInterface
    public interface RpcProc0 extends Serializable {
        void execute(Context context, Persistence persistence, Communication communication);
    }

    public static final int PARAMETERS_WITH_INPUT = 4;
    public static final int PARAMETERS_NO_INPUT = 3;

    public static final int INDEX_OF_INPUT_PARAMETER = 1;

    public static void validateRpcMethod(final Method method) {
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
            throw new ObjectDefinitionException("An RPC method must be in the form of one of {@link RpcDefinitions}");
        }
        if (!persistenceType.equals(Persistence.class) || !communicationType.equals(Communication.class)) {
            throw new ObjectDefinitionException("An RPC method must be in the form of one of {@link RpcDefinitions}");
        }
    }
}