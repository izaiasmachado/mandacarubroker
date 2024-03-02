package com.mandacarubroker.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Especificação OpenApi - Mandacaru Broker API",
                description = "Documentação da API do Mandacaru Broker | Mandacaru.dev - #Time 10 do Módulo Jandaia",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Ambiente Padrão",
                        url = "/"
                ),
                @Server(
                        description = "Ambiente de Produção",
                        url = "https://api.mandacarubroker.com.br"
                ),
                @Server(
                        description = "Ambiente de Local",
                        url = "http://localhost:8080"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Token JWT para autenticação",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)

public class OpenApiConfig {
}