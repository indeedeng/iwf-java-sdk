package io.iworkflow.core;

import io.iworkflow.core.persistence.PersistenceSchemaOptions;
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

    final PersistenceSchemaOptions schemaOptions;

    public RpcInvocationHandler(final UnregisteredClient unregisteredClient, final String workflowId, final String workflowRunId, final PersistenceSchemaOptions schemaOptions) {
        this.unregisteredClient = unregisteredClient;
        this.workflowId = workflowId;
        this.workflowRunId = workflowRunId;
        this.schemaOptions = schemaOptions;
    }

    @RuntimeType
    public Object intercept(@AllArguments Object[] allArguments,
                            @Origin Method method) {
        final RPC rpcAnno = method.getAnnotation(RPC.class);
        if (rpcAnno == null) {
            throw new WorkflowDefinitionException("An RPC method must be annotated by RPC annotation");
        }
        validateRpcMethod(method);
        Object input = null;
        if (method.getParameterTypes().length == PARAMETERS_WITH_INPUT) {
            input = allArguments[INDEX_OF_INPUT_PARAMETER];
        }

        final Class<?> outputType = method.getReturnType();

        boolean useMemo = schemaOptions.getUsingMemoForCachingDataAttributes();
        if (rpcAnno.strongConsistencyReadWithCaching()) {
            useMemo = false;
        }
        final Object output = unregisteredClient.invokeRpc(outputType, input, workflowId, workflowRunId, method.getName(), rpcAnno.timeoutSeconds(),
                new PersistenceLoadingPolicy()
                        .persistenceLoadingType(rpcAnno.dataAttributesLoadingType())
                        .partialLoadingKeys(Arrays.asList(rpcAnno.dataAttributesPartialLoadingKeys())),
                new PersistenceLoadingPolicy()
                        .persistenceLoadingType(rpcAnno.searchAttributesLoadingType())
                        .partialLoadingKeys(Arrays.asList(rpcAnno.searchAttributesPartialLoadingKeys())),
                useMemo
        );
        return output;
    }
}