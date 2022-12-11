package io.github.cadenceoss.iwf.core.communication;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class CommunicationSchemaDef {
    public abstract List<CommunicationMethod> getCommunicationMethods();

    public static CommunicationSchemaDef create(CommunicationMethod... methods) {
        return ImmutableCommunicationSchemaDef.builder()
                .addCommunicationMethods(methods)
                .build();
    }
}
