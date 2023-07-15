package io.iworkflow.integ;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import io.iworkflow.core.Client;
import io.iworkflow.core.ClientNotFullyInitializedException;
import io.iworkflow.core.ClientOptions;
import io.iworkflow.core.aspect.RegistryValidationAspect;
import io.iworkflow.spring.controller.WorkflowRegistry;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AspectTest {
    private RegistryValidationAspect registryValidationAspect;

    @BeforeEach
    public void setup() {
        registryValidationAspect = new RegistryValidationAspect();
    }

    @Test
    public void testValidateRegistry_RegistryNotNull()
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        final JoinPoint joinPoint = mock(JoinPoint.class);

        final Client testClient = Client.builder(ClientOptions.dockerDefault);
        WorkflowRegistry.registry.initializeClient(testClient);

        when(joinPoint.getTarget()).thenReturn(testClient);

        assertDoesNotThrow(() -> registryValidationAspect.validateRegistry(joinPoint));
    }

    @Test
    public void testValidateRegistry_RegistryNull()
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        final JoinPoint joinPoint = mock(JoinPoint.class);

        final Client testClient = Client.builder(ClientOptions.dockerDefault);

        when(joinPoint.getTarget()).thenReturn(testClient);

        final ClientNotFullyInitializedException exception = assertThrows(
                ClientNotFullyInitializedException.class,
                () -> registryValidationAspect.validateRegistry(joinPoint));
        assertEquals("client was not fully initialized. Missing the initialization of registry.", exception.getMessage());
    }
}
