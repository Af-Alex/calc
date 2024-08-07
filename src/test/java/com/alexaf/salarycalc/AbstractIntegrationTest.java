package com.alexaf.salarycalc;

import com.alexaf.salarycalc.telegram.service.SilentSender;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SalaryCalculatorApplication.class)
@ActiveProfiles("tc")
public abstract class AbstractIntegrationTest {

    private static final String imageId = "postgres:alpine3.19";
    private final static String TEST_DB = "calc";
    private final static String TEST_DB_USER = "test_user";
    private final static String TEST_DB_USER_PASSWORD = "test_password";

    @MockBean
    protected SilentSender silentSender;

    @MockBean
    protected TelegramClient telegramClient;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(imageId)
            .withUsername(TEST_DB_USER)
            .withPassword(TEST_DB_USER_PASSWORD)
            .withDatabaseName(TEST_DB)
            .withInitScript("db/init.sql")
            .waitingFor(Wait.defaultWaitStrategy());

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        postgres.start();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.contexts", () -> "!prod");
    }

}
