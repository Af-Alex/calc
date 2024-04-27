package com.alexaf.salarycalc.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "AlexAf",
                        email = "dev.af.alex@gmail.com",
                        url = "https://github.com/Af-Alex"
                ),
                description = "Калькулятор для расчётов процентов от ЗП на разные нужны",
                title = "Money calc API",
                version = "${api.version:1}",
                license = @License(
                        name = "MIT License",
                        url = "https://choosealicense.com/licenses/mit/"
                ),
                termsOfService = "Terms of service"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:" + "${server.port:8080}"
                ),
                @Server(
                        description = "PROD ENV",
                        url = "https://gitlab-cicd.freemyip.com:" + "${server.port:8080}"
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
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenAPIConfig {

}
