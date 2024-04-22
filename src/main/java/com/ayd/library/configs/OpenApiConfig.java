package com.ayd.library.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@OpenAPIDefinition(
        info = @Info(
                title = "Library",
                version = "1.0.0",
                description = "Project AYD2"
        )
)
public class OpenApiConfig {



}
