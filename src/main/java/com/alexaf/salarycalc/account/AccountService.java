package com.alexaf.salarycalc.account;

import com.alexaf.salarycalc.account.dto.Account;
import com.alexaf.salarycalc.account.repository.AccountEntity;
import com.alexaf.salarycalc.account.repository.AccountRepository;
import com.alexaf.salarycalc.exception.EntityNotFoundException;
import com.alexaf.salarycalc.user.repository.TgUserEntity;
import com.alexaf.salarycalc.user.repository.TgUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final AccountMapper mapper;
    private final TgUserRepository userRepository;

    @Transactional
    public UUID create(@Valid Account dto) {
        AccountEntity entity = mapper.createFromDto(dto);
        entity.setOwner(userRepository.findById(dto.getOwnerId()).orElseThrow(() -> new EntityNotFoundException(TgUserEntity.class, dto.getOwnerId())));
        repository.save(entity);
        return entity.getId();
    }

    public Account getById(UUID accountId) {
        return mapper.toDto(
                repository.findById(accountId)
                        .orElseThrow(() -> new EntityNotFoundException(AccountEntity.class, accountId))
        );
    }

}
