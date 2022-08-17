package iwf.core.spring.controller;

import io.swagger.v3.oas.annotations.Parameter;
import iwf.gen.api.ApiApi;
import iwf.gen.models.WorkflowStateStartRequest;
import iwf.gen.models.WorkflowStateStartResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
public class ApiController implements ApiApi {

    @RequestMapping("/")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("Hello, world!");
    }

    @Override
    public ResponseEntity<WorkflowStateStartResponse> apiV1WorkflowStateStartPost(
             final WorkflowStateStartRequest workflowStateStartRequest
    ) {
        return null;
    }

}