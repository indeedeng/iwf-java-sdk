package io.iworkflow.core.aspect;

import io.iworkflow.core.ClientNotFullyInitializedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Aspect
@Component
public class RegistryValidationAspect {
    @Before("execution(public * io.iworkflow.core.Client.*(..)) " +
            "&& !execution(public * io.iworkflow.core.Client.initializeRegistry(..))")
    public void validateRegistry(final JoinPoint joinPoint)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        final Object target = joinPoint.getTarget();
        final Field registryField = target.getClass().getDeclaredField("registry");
        registryField.setAccessible(true);
        final Object registry = registryField.get(target);

        if (registry == null) {
            throw new ClientNotFullyInitializedException("client was not fully initialized. Missing the initialization of registry.");
        }
    }
}
