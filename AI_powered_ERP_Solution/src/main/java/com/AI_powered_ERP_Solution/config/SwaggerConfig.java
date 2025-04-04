package com.AI_powered_ERP_Solution.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI-Powered ERP Solution API")
                        .version("1.0.0")
                        .description("API for AI-powered ERP job verification system with Dynamics 365 integration"))
                .addSecurityItem(new SecurityRequirement().addList("oauth2"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("oauth2", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .clientCredentials(new OAuthFlow()
                                                .tokenUrl("https://login.microsoftonline.com/your-tenant-id/oauth2/v2.0/token")
                                                .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                                        .addString("https://api.businesscentral.dynamics.com/.default", "Access Dynamics 365"))))));
    }
}