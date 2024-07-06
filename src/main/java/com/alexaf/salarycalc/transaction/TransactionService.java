package com.alexaf.salarycalc.transaction;

import com.alexaf.salarycalc.account.repository.AccountEntity;
import com.alexaf.salarycalc.account.repository.AccountRepository;
import com.alexaf.salarycalc.exception.EntityNotFoundException;
import com.alexaf.salarycalc.transaction.dto.Transaction;
import com.alexaf.salarycalc.transaction.repository.TransactionEntity;
import com.alexaf.salarycalc.transaction.repository.TransactionRepository;
import com.alexaf.salarycalc.user.repository.TgUserEntity;
import com.alexaf.salarycalc.user.repository.TgUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@Valid
@Transactional
@Validated
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final TgUserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper mapper;

    public Transaction getById(UUID transactionId) {
        return repository.findById(transactionId).map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(TransactionEntity.class, transactionId));
    }

    public UUID create(@Valid Transaction transaction) {
        var entity = mapper.toEntity(transaction);

        entity.setInitiator(userRepository.findById(transaction.getInitiatorId())
                .orElseThrow(() -> new EntityNotFoundException(TgUserEntity.class, transaction.getInitiatorId())));
        entity.setAccrualAccount(accountRepository.findById(transaction.getAccrualAccountId())
                .orElseThrow(() -> new EntityNotFoundException(AccountEntity.class, transaction.getAccrualAccountId())));

        return repository.save(entity).getId();
    }

    public void invokeTransaction(UUID transactionId) {
        var entity = repository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(TransactionEntity.class, transactionId));


    }

}
