package io.iworkflow.core;

import io.iworkflow.gen.models.PersistenceLoadingPolicy;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.lang.reflect.Method;
import java.util.Arrays;

import static io.iworkflow.core.RpcDefinitions.INDEX_OF_INPUT_PARAMETER;
import static io.iworkflow.core.RpcDefinitions.PARAMETERS_WITH_INPUT;
import static io.iworkflow.core.RpcDefinitions.validateRpcMethod;

public class RpcInvocationHandler {

    private final String workflowId;
    private final String workflowRunId;

    final UnregisteredClient unregisteredClient;

    public RpcInvocationHandler(final UnregisteredClient unregisteredClient, final String workflowId, final String workflowRunId) {
        this.unregisteredClient = unregisteredClient;
        this.workflowId = workflowId;
        this.workflowRunId = workflowRunId;
    }

    @RuntimeType
    public Object intercept(@AllArguments Object[] allArguments,
                            @Origin Method method) {
        final RPC rpcAnno = method.getAnnotation(RPC.class);
        if (rpcAnno == null) {
            throw new ObjectDefinitionException("An RPC method must be annotated by RPC annotation");
        }
        validateRpcMethod(method);
        Object input = null;
        if (method.getParameterTypes().length == PARAMETERS_WITH_INPUT) {
            input = allArguments[INDEX_OF_INPUT_PARAMETER];
        }
        
        final Class<?> outputType = method.getReturnType();

        final Object output = unregisteredClient.invokeRpc(outputType, input, workflowId, workflowRunId, method.getName(), rpcAnno.timeoutSeconds(),
                new PersistenceLoadingPolicy()
                        .persistenceLoadingType(rpcAnno.dataAttributesLoadingType())
                        .partialLoadingKeys(Arrays.asList(rpcAnno.dataAttributesPartialLoadingKeys())),
                new PersistenceLoadingPolicy()
                        .persistenceLoadingType(rpcAnno.searchAttributesLoadingType())
                        .partialLoadingKeys(Arrays.asList(rpcAnno.searchAttributesPartialLoadingKeys())));
        return output;
    }
}