package iwf.spring.controller;

import iwf.core.Registry;
import iwf.core.WorkerService;
import iwf.gen.models.WorkflowStateDecideRequest;
import iwf.gen.models.WorkflowStateDecideResponse;
import iwf.gen.models.WorkflowStateStartRequest;
import iwf.gen.models.WorkflowStateStartResponse;
import iwf.integ.BasicWorkflow;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApiController {

    private WorkerService workerService;

    public ApiController() {
        final Registry registry = new Registry();
        registry.addWorkflow(new BasicWorkflow());
        workerService = new WorkerService(registry);
    }

    @RequestMapping("/")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("Hello, world!");
    }

    @PostMapping("/api/v1/workflowState/start")
    public ResponseEntity<WorkflowStateStartResponse> apiV1WorkflowStateStartPost(
            final WorkflowStateStartRequest request
    ) {
        return ResponseEntity.ok(workerService.handleWorkflowStateStart(request));
    }

    @PostMapping("/api/v1/workflowState/decide")
    public ResponseEntity<WorkflowStateDecideResponse> apiV1WorkflowStateDecidePost(
            final WorkflowStateDecideRequest request
    ) {
        return ResponseEntity.ok(workerService.handleWorkflowStateDecide(request));
    }

}