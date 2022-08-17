package iwf.core.spring.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {

    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("WorkflowState APIs")
                                .description("This APIs for iwf-server to invoke user workflow code defined in WorkflowState using any iwf SDKs")
                                .license(
                                        new License()
                                                .name("Apache-2.0")
                                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                                )
                                .version("1.0.0")
                )
        ;
    }
}