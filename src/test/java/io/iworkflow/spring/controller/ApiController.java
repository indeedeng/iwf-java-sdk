package io.iworkflow.spring.controller;

import io.iworkflow.core.WorkerOptions;
import io.iworkflow.core.WorkerService;
import io.iworkflow.gen.models.WorkerErrorResponse;
import io.iworkflow.gen.models.WorkflowStateExecuteRequest;
import io.iworkflow.gen.models.WorkflowStateWaitUntilRequest;
import io.iworkflow.gen.models.WorkflowWorkerRpcRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.PrintWriter;
import java.io.StringWriter;

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

    @PostMapping(WorkerService.WORKFLOW_STATE_WAIT_UNTIL_API_PATH)
    public ResponseEntity<?> apiV1WorkflowStateStartPost(
            final @RequestBody WorkflowStateWaitUntilRequest request
    ) {
        try {
            return ResponseEntity.ok(workerService.handleWorkflowStateWaitUntil(request));
        } catch (RuntimeException e) {
            return processWorkerException(request.getContext().getWorkflowId(), e, "WaitUntil");
        }
    }

    @PostMapping(WorkerService.WORKFLOW_STATE_EXECUTE_API_PATH)
    public ResponseEntity<?> apiV1WorkflowStateDecidePost(
            final @RequestBody WorkflowStateExecuteRequest request
    ) {
        try {
            return ResponseEntity.ok(workerService.handleWorkflowStateExecute(request));
        } catch (RuntimeException e) {
            return processWorkerException(request.getContext().getWorkflowId(), e, "Execute");
        }
    }

    @PostMapping(WorkerService.WORKFLOW_WORKER_RPC_API_PATH)
    public ResponseEntity<?> apiV1WorkflowWorkerRpcPost(
            final @RequestBody WorkflowWorkerRpcRequest request
    ) {
        try {
            return ResponseEntity.ok(workerService.handleWorkflowWorkerRpc(request));
        } catch (RuntimeException e) {
            return processWorkerException(request.getContext().getWorkflowId(), e, "RPC");
        }
    }

    private ResponseEntity<?> processWorkerException(final String workflowId, final RuntimeException e, final String methodType) {
        // This info log is for troubleshooting because the default exception handler may not log it properly
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String sStackTrace = sw.toString(); // stack trace as a string
        if(sStackTrace.length()>500){
            // make sure NOT exceeding 4K limit in Temporal https://github.com/indeedeng/iwf/issues/272
            sStackTrace = sStackTrace.substring(0, 500) + "...(TRUNCATED)";
        }
        String msg = e.getMessage();
        if (msg.length() > 50) {
            msg = msg.substring(0, 50) + "...(TRUNCATED)";
        }

        final WorkerErrorResponse errResp = new WorkerErrorResponse()
                .detail(msg+"\n STACKTRACE: \n"+sStackTrace)
                .errorType(e.getClass().getName());
        return ResponseEntity.status(501).body(errResp);
    }


}