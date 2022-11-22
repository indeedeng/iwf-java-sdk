package io.github.cadenceoss.iwf.spring.controller;

import io.github.cadenceoss.iwf.core.Registry;
import io.github.cadenceoss.iwf.core.Workflow;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkflowRegistry {
    // NOTE: using static field so that the test doesn't need to run with springboot
    public static final Registry registry = new Registry();

    public WorkflowRegistry(List<Workflow> workflows) {
        for (Workflow wf : workflows) {
            registry.addWorkflow(wf);
        }
    }

    public Registry getRegistry() {
        return registry;
    }
}