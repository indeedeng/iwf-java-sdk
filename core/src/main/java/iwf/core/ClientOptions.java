package iwf.core;

import org.immutables.value.Value;

@Value.Immutable
public interface ClientOptions {
    String getServerUrl();

    String getWorkerUrl();
    
}
