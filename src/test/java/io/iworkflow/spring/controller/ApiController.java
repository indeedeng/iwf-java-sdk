package io.iworkflow.spring.controller;

import io.iworkflow.core.WorkerOptions;
import io.iworkflow.core.WorkerService;
import io.iworkflow.gen.models.WorkflowStateDecideRequest;
import io.iworkflow.gen.models.WorkflowStateDecideResponse;
import io.iworkflow.gen.models.WorkflowStateStartRequest;
import io.iworkflow.gen.models.WorkflowStateStartResponse;
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
    public ResponseEntity<WorkflowStateStartResponse> apiV1WorkflowStateStartPost(
            final @RequestBody WorkflowStateStartRequest request
    ) {
        return ResponseEntity.ok(workerService.handleWorkflowStateStart(request));
    }

    @PostMapping(WorkerService.WORKFLOW_STATE_DECIDE_API_PATH)
    public ResponseEntity<WorkflowStateDecideResponse> apiV1WorkflowStateDecidePost(
            final @RequestBody WorkflowStateDecideRequest request
    ) {
        return ResponseEntity.ok(workerService.handleWorkflowStateDecide(request));
    }

}