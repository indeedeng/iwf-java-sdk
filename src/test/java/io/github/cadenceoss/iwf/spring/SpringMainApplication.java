package io.github.cadenceoss.iwf.spring;

import com.fasterxml.jackson.databind.Module;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"io.github.cadenceoss.iwf.gen", "io.github.cadenceoss.iwf.gen.api", "io.github.cadenceoss.iwf.spring.controller"})
public class SpringMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMainApplication.class, args);
    }

    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }

}