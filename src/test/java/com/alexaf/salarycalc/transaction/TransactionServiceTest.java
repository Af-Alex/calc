package com.alexaf.salarycalc.transaction;

import com.alexaf.salarycalc.AbstractIntegrationTest;
import com.alexaf.salarycalc.account.AccountDataSetService;
import com.alexaf.salarycalc.account.AccountService;
import com.alexaf.salarycalc.account.repository.AccountEntity;
import com.alexaf.salarycalc.exception.EntityNotFoundException;
import com.alexaf.salarycalc.transaction.dto.Transaction;
import com.alexaf.salarycalc.transaction.event.TransactionEventListener;
import com.alexaf.salarycalc.transaction.repository.TransactionEntity;
import com.alexaf.salarycalc.transaction.repository.TransactionRepository;
import com.alexaf.salarycalc.user.UserDataSetService;
import com.alexaf.salarycalc.user.repository.TgUserEntity;
import jakarta.validation.ConstraintViolationException;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

import static com.alexaf.salarycalc.utils.RandomGenerator.bdGen;
import static com.alexaf.salarycalc.utils.RandomGenerator.longGen;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

class TransactionServiceTest extends AbstractIntegrationTest {

    @Autowired
    UserDataSetService userDataSetService;
    @Autowired
    AccountDataSetService accountDataSetService;
    @Autowired
    TransactionDatasetService transactionDatasetService;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountService accountService;

    @SpyBean
    TransactionEventListener eventListener;

    @Autowired
    TransactionService transactionService;

    private TgUserEntity user;
    private AccountEntity account;

    @BeforeEach
    public void setUp() {
        user = userDataSetService.create();
        account = accountDataSetService.create(user);
    }

    @Test
    @DisplayName("Успешное получение транзакции по ID")
    void getById_success() {
        var expected = transactionDatasetService.create();
        assertThat(transactionService.getById(expected.getId()), allOf(
                is(notNullValue()),
                hasProperty("id", is(notNullValue())),
                hasProperty("created", is(expected.getCreated())),
                hasProperty("initiatorId", is(expected.getInitiator().getId())),
                hasProperty("accrualAccountId", is(expected.getAccrualAccount().getId())),
                hasProperty("amount", is(expected.getAmount()))
        ));
    }

    @Test
    @DisplayName("Получение транзакции по ID - несуществующий ID")
    void getById_notFound() {
        assertThrows(EntityNotFoundException.class, () -> transactionService.getById(randomUUID()));
    }

    @Test
    @DisplayName("Успешное создание транзакции")
    void create_success() {
        var transaction = new Transaction(
                user.getId(),
                account.getId(),
                bdGen());

        var actual = transactionRepository.findById(transactionService.create(transaction)).orElseThrow();

        assertThat(actual, matchTransaction(transaction));
    }

    @Test
    @DisplayName("Создание транзакции - несуществующий пользователь")
    void create_userNotFound() {
        var transaction = new Transaction(
                longGen(),
                account.getId(),
                bdGen());

        assertThrows(EntityNotFoundException.class, () -> transactionService.create(transaction));
    }

    @Test
    @DisplayName("Создание транзакции - несуществующий аккаунт")
    void create_accountNotFound() {
        var transaction = new Transaction(
                user.getId(),
                randomUUID(),
                bdGen());

        assertThrows(EntityNotFoundException.class, () -> transactionService.create(transaction));
    }

    @Test
    @DisplayName("Создание транзакции - сумма не указана")
    void create_amountIsNull() {
        var transaction = new Transaction(
                user.getId(),
                account.getId(),
                null);

        assertThrows(ConstraintViolationException.class, () -> transactionService.create(transaction));
    }

    @Test
    @DisplayName("Создание транзакции - корректное пополнение счёта")
    @SuppressWarnings("nullable")
    void testTransactionCreatedEvent() {
        BigDecimal oldBalance = account.getBalance();
        BigDecimal transactionAmount = bdGen();
        BigDecimal expectedNewBalance = oldBalance.add(transactionAmount).setScale(2, RoundingMode.HALF_UP);

        assertNotNull(oldBalance);

        UUID transactionId = transactionService.create(new Transaction(user.getId(), account.getId(), transactionAmount));

        verify(eventListener).onTransactionCreated(argThat(event -> Objects.equals(event.transaction().getId(), transactionId)));
        assertEquals(accountService.getById(account.getId()).getBalance(), expectedNewBalance);
        assertNotNull(transactionRepository.findById(transactionId).orElseThrow().getCompletedAt());

    }


    private Matcher<? super TransactionEntity> matchTransaction(Transaction transaction) {
        return allOf(
                is(notNullValue()),
                hasProperty("id", is(notNullValue())),
                hasProperty("initiator", hasProperty("id", is(transaction.getInitiatorId()))),
                hasProperty("accrualAccount", hasProperty("id", is(transaction.getAccrualAccountId()))),
                hasProperty("amount", is(transaction.getAmount()))
        );
    }

}