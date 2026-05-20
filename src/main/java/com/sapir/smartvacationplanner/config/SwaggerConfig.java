package com.sapir.smartvacationplanner.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        // internal name for the security configuration
        final String securitySchemeName = "basicAuth";

        return new OpenAPI()

                // swagger api information
                .info(new Info()
                        .title("Smart Vacation Planner API")
                        .version("1.0"))

                // tells swagger that the api requires authentication
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))

                // defines the authentication type used by the api
                .schemaRequirement(securitySchemeName,
                        new SecurityScheme()

                                // security configuration name
                                .name(securitySchemeName)

                                // authentication type = HTTP
                                .type(SecurityScheme.Type.HTTP)

                                // HTTP Basic Authentication
                                .scheme("basic"));
    }
}