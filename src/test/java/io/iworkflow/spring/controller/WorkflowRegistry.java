package io.iworkflow.spring.controller;

import io.iworkflow.core.ObjectWorkflow;
import io.iworkflow.core.Registry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkflowRegistry {
    // NOTE: using static field so that the test doesn't need to run with springboot
    public static final Registry registry = new Registry();

    public WorkflowRegistry(List<ObjectWorkflow> workflows) {
        registry.addWorkflows(workflows);
    }

    public Registry getRegistry() {
        return registry;
    }
}