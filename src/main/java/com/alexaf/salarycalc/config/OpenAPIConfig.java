package com.alexaf.salarycalc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI myOpenAPI(@Value("${server.port:8080}") String serverPort) {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:" + serverPort);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl("http://gitlab-cicd.freemyip.com:" + serverPort);
        prodServer.setDescription("Server URL in Production environment");


        Server prodServerTest = new Server();
        prodServerTest.setUrl("https://gitlab-cicd.freemyip.com:" + serverPort);
        prodServerTest.setDescription("Server URL in ProductionTest environment");

        return new OpenAPI().info(getInfo()).servers(List.of(devServer, prodServer, prodServerTest));
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

}
