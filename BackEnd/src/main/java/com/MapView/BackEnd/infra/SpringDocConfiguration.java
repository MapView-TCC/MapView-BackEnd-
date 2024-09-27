package com.MapView.BackEnd.infra;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Api MapView")
                        .version("1.0.0")
                        .description("A API do MapView tem como objetivo controlar e rastrear os acessos de equipamentos por meio de uma estrutura RFID no ambiente da CaP/ETS.")
                        .termsOfService("https://exemplo.com/termos-de-servico")
                        .license(new License().name("Licença do MapView").url("https://exemplo.com/licenca"))
                )
                .components(new Components()
                        .addSecuritySchemes("bearer-key", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Documentação Completa do MapView")
                        .url("https://exemplo.com/documentacao-completa")
                );
    }
}
