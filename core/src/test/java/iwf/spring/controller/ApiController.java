package iwf.spring.controller;

import iwf.gen.models.WorkflowStateStartRequest;
import iwf.gen.models.WorkflowStateStartResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ApiController {

    @RequestMapping("/")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("Hello, world!");
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/api/v1/workflowState/start",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<WorkflowStateStartResponse> apiV1WorkflowStateStartPost(
            final WorkflowStateStartRequest workflowStateStartRequest
    ) {
        System.out.println("this is a test log" + workflowStateStartRequest.toString());
        return null;
    }

}