package iwf.core.spring.controller;

import iwf.gen.models.WorkflowStateStartRequest;
import iwf.gen.models.WorkflowStateStartResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApiController {

    @RequestMapping("/")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("Hello, world!");
    }

    public ResponseEntity<WorkflowStateStartResponse> apiV1WorkflowStateStartPost(
            final WorkflowStateStartRequest workflowStateStartRequest
    ) {
        System.out.println("this is a test log" + workflowStateStartRequest.toString());
        return null;
    }

}