package com.alexaf.salarycalc.service;

import com.alexaf.salarycalc.AbstractIntegrationTest;
import com.alexaf.salarycalc.salary.SalaryService;
import com.alexaf.salarycalc.user.UserDatasetService;
import com.alexaf.salarycalc.user.repository.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;

class DistributionServiceTest extends AbstractIntegrationTest {

    @Autowired
    UserDatasetService userDatasetService;
    @Autowired
    DistributionService distributionService;
    @Autowired
    SalaryService salaryService;

    @Test
    void distributeIncome() {
        User user = userDatasetService.createWithDefaultGoals();
        BigDecimal salary = new BigDecimal(100_000);
        salaryService.create(user.getId(), salary);
        Map<String, BigDecimal> result = distributionService.distributeIncome(user.getId(), salary);
        result.forEach((goalName, contributionValue) -> System.out.println(goalName + ": " + contributionValue));
    }

}