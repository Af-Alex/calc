package com.alexaf.salarycalc.account;

import com.alexaf.salarycalc.AbstractIntegrationTest;
import com.alexaf.salarycalc.account.dto.Account;
import com.alexaf.salarycalc.account.repository.AccountEntity;
import com.alexaf.salarycalc.account.repository.AccountRepository;
import com.alexaf.salarycalc.exception.EntityNotFoundException;
import com.alexaf.salarycalc.user.UserDataSetService;
import com.alexaf.salarycalc.user.repository.TgUserEntity;
import com.alexaf.salarycalc.user.repository.TgUserRepository;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.alexaf.salarycalc.utils.HamcrestUtils.isNullable;
import static com.alexaf.salarycalc.utils.RandomGenerator.bdGen;
import static com.alexaf.salarycalc.utils.RandomGenerator.longGen;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ContextConfiguration(
        classes = {
                AccountService.class,
                AccountMapper.class,
                AccountRepository.class,
                TgUserRepository.class
        })
class AccountServiceTest extends AbstractIntegrationTest {

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserDataSetService userDataSetService;
    @Autowired
    private AccountDataSetService accountDataSetService;

    private TgUserEntity user;

    @BeforeEach
    public void setUp() {
        user = userDataSetService.create();
    }

    @Test
    @DisplayName("Успешное создание счёта")
    void createAccount_success() {
        var account = Account.builder()
                .balance(bdGen())
                .ownerId(user.getId())
                .build();

        var actual = accountRepository.findById(accountService.create(account)).orElseThrow();
        assertThat(actual, allOf(
                        is(notNullValue()),
                        hasProperty("id", is(notNullValue())),
                        hasProperty("created", is(notNullValue())),
                        hasProperty("balance", isNullable(account.getBalance())),
                        hasProperty("owner", hasProperty("id", isNullable(account.getOwnerId())))
                )
        );
    }

    @Test
    @DisplayName("Создание счёта - не найден указанный владелец")
    void createAccount_ownerNotFound() {
        var account = Account.builder()
                .balance(bdGen())
                .ownerId(longGen())
                .build();

        assertThrows(EntityNotFoundException.class, () -> accountService.create(account));
    }

    @Test
    @DisplayName("Успешное получение счёта")
    void getAccountById_success() {
        var expected = accountDataSetService.create(user, null);
        var actual = accountService.getById(expected.getId());

        assertThat(actual, matchAccount(expected));
    }

    @Test
    @DisplayName("Получение счёта - несуществующий ID")
    void getAccountById_notFound() {
        assertThrows(EntityNotFoundException.class, () -> accountService.getById(randomUUID()));
    }

    private Matcher<Account> matchAccount(AccountEntity expected) {
        return allOf(
                hasProperty("id", is(expected.getId())),
                hasProperty("created", is(expected.getCreated())),
                hasProperty("balance", is(expected.getBalance())),
                hasProperty("ownerId", is(expected.getOwner().getId()))
        );
    }

}
