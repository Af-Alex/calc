package com.alexaf.salarycalc.transaction.event;

import com.alexaf.salarycalc.account.repository.AccountRepository;
import com.alexaf.salarycalc.transaction.repository.TransactionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventListener {

    private final AccountRepository accountRepository;

    @EventListener
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void onTransactionCreated(InvokeTransactionEvent event) {
        TransactionEntity transaction = event.transaction();

        log.debug("Пользователь {} создал транзакцию", transaction.getInitiator().getUserName());

        if (nonNull(transaction.getCompletedAt())) {
            log.debug("Создана уже выполненная транзакция [{}]", transaction.getId());
            return;
        }

        transaction.apply();

        log.debug("Баланс обновлён");
    }

}
