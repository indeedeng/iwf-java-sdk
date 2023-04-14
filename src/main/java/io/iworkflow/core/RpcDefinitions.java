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
        O execute(I input, Persistence persistence, Communication communication);
    }

    /**
     * RPC with output only
     *
     * @param <O> output type
     */
    @FunctionalInterface
    public interface RpcFunc0<O> extends Serializable {
        O execute(Persistence persistence, Communication communication);
    }

    /**
     * RPC with input only
     *
     * @param <I> input type
     */
    @FunctionalInterface
    public interface RpcProc1<I> extends Serializable {
        void execute(I input, Persistence persistence, Communication communication);
    }

    /**
     * RPC without input or output
     */
    @FunctionalInterface
    public interface RpcProc0 extends Serializable {
        void execute(Persistence persistence, Communication communication);
    }

    public static void validateRpcMethod(final Method method) {
        final Class<?>[] paramTypes = method.getParameterTypes();
        final Class<?> persistenceType, communicationType;
        if (paramTypes.length == 2) {
            persistenceType = paramTypes[0];
            communicationType = paramTypes[1];
        } else if (paramTypes.length == 3) {
            persistenceType = paramTypes[1];
            communicationType = paramTypes[2];
        } else {
            throw new WorkflowDefinitionException("An RPC method must be in the form of one of {@link RpcDefinitions}");
        }
        if (!persistenceType.equals(Persistence.class) || !communicationType.equals(Communication.class)) {
            throw new WorkflowDefinitionException("An RPC method must be in the form of one of {@link RpcDefinitions}");
        }
    }
}