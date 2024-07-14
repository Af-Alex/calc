package com.alexaf.salarycalc.salary;

import com.alexaf.salarycalc.salary.repository.Salary;
import com.alexaf.salarycalc.salary.repository.SalaryRepository;
import com.alexaf.salarycalc.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final UserService userService;

    public Salary findActual(UUID userId) {
        return salaryRepository.findFirstByUser_IdOrderByEffectiveDateDesc(userId);
    }

    public Salary save(Salary salary) {
        return salaryRepository.save(salary);
    }

    public UUID create(UUID userId, BigDecimal salary) {
        var entity = new Salary(userService.getById(userId), salary, LocalDate.now());
        return salaryRepository.save(entity).getId();
    }
}
