package io.iworkflow.spring;

import com.fasterxml.jackson.databind.Module;
import io.iworkflow.integ.rpc.RpcWorkflowWithFinalRpc;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@SpringBootApplication
@ComponentScan(
        basePackages = {"io.iworkflow.gen", "io.iworkflow.gen.api", "io.iworkflow.spring.controller", "io.iworkflow.integ"},
        excludeFilters={@ComponentScan.Filter(type=ASSIGNABLE_TYPE, value=RpcWorkflowWithFinalRpc.class)}
)
public class SpringMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMainApplication.class, args);
    }

    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }

}