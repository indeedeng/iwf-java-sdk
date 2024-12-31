package io.iworkflow.core;

import io.iworkflow.core.communication.Communication;
import io.iworkflow.core.persistence.Persistence;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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

    public static final String DEFINITION_ERROR_MESSAGE = "An RPC method must be in the form of one of {@link RpcDefinitions}";

    public static final String FINAL_MODIFIER_ERROR_MESSAGE = "An RPC method must not be final";

    public static void validateRpcMethod(final Method method) {
        RpcMethodMetadata methodMetadata = RpcMethodMatcher.match(method);
        final boolean isFinal = Modifier.isFinal(method.getModifiers());

        if (isFinal) {
            throw new ImplementationException(FINAL_MODIFIER_ERROR_MESSAGE);
        }

        if (methodMetadata == null) {
            throw new WorkflowDefinitionException(DEFINITION_ERROR_MESSAGE);
        }
    }
}
