package io.iworkflow.core;

import io.iworkflow.gen.models.WorkflowResetRequest;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class ResetWorkflowTypeAndOptions {

    public abstract WorkflowResetRequest.ResetTypeEnum getResetType();

    public abstract Optional<Integer> getHistoryEventId();

    public abstract String getReason();

    public abstract Optional<String> getHistoryEventTime();

    public abstract Optional<Boolean> getSkipSignalReapply();

    public static ResetWorkflowTypeAndOptions resetToBeginning(final String reason) {
        return builder()
                .resetType(WorkflowResetRequest.ResetTypeEnum.BEGINNING)
                .reason(reason)
                .build();
    }

    public static ResetWorkflowTypeAndOptions resetToHistoryEventId(final int historyEventId, final String reason) {
        return builder()
                .resetType(WorkflowResetRequest.ResetTypeEnum.HISTORY_EVENT_ID)
                .historyEventId(historyEventId)
                .reason(reason)
                .build();
    }

    public static ResetWorkflowTypeAndOptions resetToHistoryEventId(final String historyEventTime, final String reason) {
        return builder()
                .resetType(WorkflowResetRequest.ResetTypeEnum.HISTORY_EVENT_ID)
                .historyEventTime(historyEventTime)
                .reason(reason)
                .build();
    }

    public static ImmutableResetWorkflowTypeAndOptions.Builder builder() {
        return ImmutableResetWorkflowTypeAndOptions.builder();
    }
}
