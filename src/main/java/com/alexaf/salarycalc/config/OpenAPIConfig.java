package com.alexaf.salarycalc.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${server.port:8080}")
    public String serverPort;

    @Bean
    public OpenAPI myOpenAPI(List<Server> servers) {
        return new OpenAPI()
                .info(getInfo())
                .servers(servers)
                .addSecurityItem(getSecurityRequirement())
                .components(getComponents());
    }

    private Info getInfo() {
        Contact contact = new Contact();
        contact.setEmail("dev.af.alex@gmail.com");
        contact.setName("AlexAf");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        return new Info()
                .title("Money calc API")
                .version("1.0.1")
                .contact(contact)
                .description("Калькулятор для расчётов процентов от ЗП на разные нужны")
                .license(mitLicense);
    }

    @Profile("prod")
    @Bean
    public Server prodServer() {
        return new Server()
                .url("https://gitlab-cicd.freemyip.com:" + serverPort)
                .description("Server URL in Production environment");
    }

    @Profile("dev")
    @Bean
    public Server devServer() {
        return new Server()
                .url("http://localhost:" + serverPort)
                .description("Server URL in Development environment");
    }

    private SecurityRequirement getSecurityRequirement() {
        return new SecurityRequirement().addList("basicAuth");
    }

    private Components getComponents() {
        return new Components().addSecuritySchemes("basicAuth", getSecurityScheme());
    }

    private SecurityScheme getSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic");
    }

}
