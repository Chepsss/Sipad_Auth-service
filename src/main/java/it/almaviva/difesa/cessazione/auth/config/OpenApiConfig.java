package it.almaviva.difesa.cessazione.auth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import it.almaviva.difesa.cessazione.auth.constant.Constant;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI () {
        return new OpenAPI()
                .info(new Info().title("Auth-service API doc").version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("auth-token", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(Constant.AUTH_HEADER)
                                .description("Add this prefix \"Bearer \" before the token for the Bearer Authentication")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("auth-token"));
    }
}
