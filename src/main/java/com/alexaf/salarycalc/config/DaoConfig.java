package com.alexaf.salarycalc.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories(
        basePackages = {
                "com.alexaf.salarycalc.**.repository",
                "com.alexaf.salarycalc.utils.entity"
        }
)
@EnableTransactionManagement
public class DaoConfig {


}
