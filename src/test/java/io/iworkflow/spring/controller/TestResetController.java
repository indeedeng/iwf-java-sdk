package io.iworkflow.spring.controller;
import io.iworkflow.core.Client;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.ResetWorkflowTypeAndOptions;
import io.iworkflow.integ.reset.RpcLockingWorkflowReset;
import io.iworkflow.integ.reset.RpcLockingWorkflowResetWithStateExecutionId;
import io.iworkflow.integ.reset.RpcLockingWorkflowStateReset2;
import io.iworkflow.integ.reset.RpcLockingWorkflowStateResetWithStateExecutionId2;
import io.iworkflow.integ.rpc.RpcLockingWorkflow;
import io.iworkflow.integ.rpc.RpcLockingWorkflowState2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/private/reset/workflow")
public class TestResetController {

    private Client client;

    public TestResetController(final WorkflowRegistry registry) {
        this.client = new Client(registry.getRegistry(), ClientOptions.localDefault);
    }

    @PostMapping(value="/start", produces = "application/json")
    public ResponseEntity<String> start(
            @RequestParam(name="workflowId") final String workflowId,
            @RequestParam(name="stateExecutionIdWf", defaultValue = "false") final boolean stateExecutionIdWf
    ) {
        System.out.println("start");
        final String runId;
        if (stateExecutionIdWf) {
            RpcLockingWorkflowStateResetWithStateExecutionId2.resetCounter();
            runId = client.startWorkflow(
                    RpcLockingWorkflowResetWithStateExecutionId.class, workflowId, 60000);
        } else {
            RpcLockingWorkflowStateReset2.resetCounter();
            runId = client.startWorkflow(
                    RpcLockingWorkflowReset.class, workflowId, 60000);
        }
        return ResponseEntity.ok(runId);
    }

    @PostMapping(value = "/locking/rpc", produces = "application/json")
    public ResponseEntity<String> lockingRpc(
            @RequestParam(name="workflowId") final String workflowId,
            @RequestParam(name="stateExecutionIdWf", defaultValue = "false") final boolean stateExecutionIdWf) {
        System.out.println("locking rpc");
        if (stateExecutionIdWf) {
            final RpcLockingWorkflowResetWithStateExecutionId rpcStub = client.newRpcStub(RpcLockingWorkflowResetWithStateExecutionId.class, workflowId);
            client.invokeRPC(rpcStub::testRpcWithLocking);
        } else {
            final RpcLockingWorkflowReset rpcStub = client.newRpcStub(RpcLockingWorkflowReset.class, workflowId);
            client.invokeRPC(rpcStub::testRpcWithLocking);
        }
        return ResponseEntity.ok("Locking RPC completed");
    }

    @PostMapping(value = "/nonlocking/rpc", produces = "application/json")
    public ResponseEntity<String> nonLockingRpc(
            @RequestParam(name="workflowId") final String workflowId,
            @RequestParam(name="stateExecutionIdWf", defaultValue = "false") final boolean stateExecutionIdWf) {
        System.out.println("non-locking rpc");
        if (stateExecutionIdWf) {
            final RpcLockingWorkflowResetWithStateExecutionId rpcStub = client.newRpcStub(RpcLockingWorkflowResetWithStateExecutionId.class, workflowId);
            client.invokeRPC(rpcStub::testRpcWithoutLocking);
        } else {
            final RpcLockingWorkflowReset rpcStub = client.newRpcStub(RpcLockingWorkflowReset.class, workflowId);
            client.invokeRPC(rpcStub::testRpcWithoutLocking);
        }
        return ResponseEntity.ok("Non-locking RPC completed");
    }

    @PostMapping(value = "/reset", produces = "application/json")
    public ResponseEntity<String> reset(
            @RequestParam(name="workflowId") final String workflowId,
            @RequestBody final TestResetRequest request
    ) {
        System.out.println("reset");
        final String currentWorkflowRunId = client.resetWorkflow(workflowId, request.getRunId(), ResetWorkflowTypeAndOptions.builder()
                .resetType(request.getResetType())
                .reason(request.getReason())
                .skipUpdateReapply(request.getSkipUpdateReapply())
                .skipSignalReapply(request.getSkipSignalReapply())
                .historyEventId(request.getHistoryEventId())
                .stateId(request.getStateId())
                .stateExecutionId(request.getStateExecutionId())
                .build());
        return ResponseEntity.ok(currentWorkflowRunId);
    }
}
