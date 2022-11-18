package io.github.cadenceoss.iwf.core.mapper;

import io.github.cadenceoss.iwf.core.options.WorkflowIdReusePolicy;
import io.github.cadenceoss.iwf.gen.models.WorkflowStartOptions;

import java.util.Optional;

public class WorkflowIdReusePolicyMapper {
    public static WorkflowStartOptions.WorkflowIDReusePolicyEnum toGenerated(
            Optional<WorkflowIdReusePolicy> workflowIdReusePolicy
    ) {
        if (workflowIdReusePolicy.isEmpty()) {
            return WorkflowStartOptions.WorkflowIDReusePolicyEnum.ALLOW_DUPLICATE_FAILED_ONLY;
        }
        switch (workflowIdReusePolicy.get()) {
            case ALLOW_DUPLICATE_FAILED_ONLY:
                return WorkflowStartOptions.WorkflowIDReusePolicyEnum.ALLOW_DUPLICATE_FAILED_ONLY;
            case ALLOW_DUPLICATE:
                return WorkflowStartOptions.WorkflowIDReusePolicyEnum.ALLOW_DUPLICATE;
            case REJECT_DUPLICATE:
                return WorkflowStartOptions.WorkflowIDReusePolicyEnum.REJECT_DUPLICATE;
            case TERMINATE_IF_RUNNING:
                return WorkflowStartOptions.WorkflowIDReusePolicyEnum.TERMINATE_IF_RUNNING;
            default:
                throw new RuntimeException("Unexpected error, no such workflow id reuse policy");
        }
    }
}
