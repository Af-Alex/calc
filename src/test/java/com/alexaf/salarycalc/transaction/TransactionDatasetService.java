package com.alexaf.salarycalc.transaction;

import com.alexaf.salarycalc.account.AccountDataSetService;
import com.alexaf.salarycalc.account.repository.AccountEntity;
import com.alexaf.salarycalc.transaction.repository.TransactionEntity;
import com.alexaf.salarycalc.transaction.repository.TransactionRepository;
import com.alexaf.salarycalc.user.UserDataSetService;
import com.alexaf.salarycalc.user.repository.TgUserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.alexaf.salarycalc.utils.RandomGenerator.bdGen;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionDatasetService {

    private final AccountDataSetService accountDataSetService;
    private final UserDataSetService userDataSetService;
    private final TransactionRepository repository;

    public TransactionEntity create() {
        return create(null);
    }

    public TransactionEntity create(AccountEntity account) {
        return create(null, account, null);
    }

    public TransactionEntity create(TgUserEntity initiator, AccountEntity account, BigDecimal amount) {
        if (initiator == null)
            initiator = userDataSetService.create();
        if (account == null)
            account = accountDataSetService.create();
        if (amount == null)
            amount = bdGen();

        return repository.save(new TransactionEntity(initiator, account, amount));
    }

}
