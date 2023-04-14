package io.iworkflow.spring.controller;

import io.iworkflow.core.WorkerOptions;
import io.iworkflow.core.WorkerService;
import io.iworkflow.gen.models.WorkflowStateExecuteRequest;
import io.iworkflow.gen.models.WorkflowStateExecuteResponse;
import io.iworkflow.gen.models.WorkflowStateWaitUntilRequest;
import io.iworkflow.gen.models.WorkflowStateWaitUntilResponse;
import io.iworkflow.gen.models.WorkflowWorkerRpcRequest;
import io.iworkflow.gen.models.WorkflowWorkerRpcResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApiController {

    private WorkerService workerService;

    public ApiController(WorkflowRegistry registry) {
        workerService = new WorkerService(registry.getRegistry(), WorkerOptions.defaultOptions);
    }

    @RequestMapping("/")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("Hello, world!");
    }

    @PostMapping(WorkerService.WORKFLOW_STATE_START_API_PATH)
    public ResponseEntity<WorkflowStateWaitUntilResponse> apiV1WorkflowStateStartPost(
            final @RequestBody WorkflowStateWaitUntilRequest request
    ) {
        return ResponseEntity.ok(workerService.handleWorkflowStateStart(request));
    }

    @PostMapping(WorkerService.WORKFLOW_STATE_DECIDE_API_PATH)
    public ResponseEntity<WorkflowStateExecuteResponse> apiV1WorkflowStateDecidePost(
            final @RequestBody WorkflowStateExecuteRequest request
    ) {
        return ResponseEntity.ok(workerService.handleWorkflowStateDecide(request));
    }

    @PostMapping(WorkerService.WORKFLOW_WORKER_RPC_API_PATH)
    public ResponseEntity<WorkflowWorkerRpcResponse> apiV1WorkflowWorkerRpcPost(
            final @RequestBody WorkflowWorkerRpcRequest request
    ) {
        return ResponseEntity.ok(workerService.handleWorkflowWorkerRpc(request));
    }

}