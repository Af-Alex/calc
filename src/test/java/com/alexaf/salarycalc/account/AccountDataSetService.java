package com.alexaf.salarycalc.account;

import com.alexaf.salarycalc.account.repository.AccountEntity;
import com.alexaf.salarycalc.account.repository.AccountRepository;
import com.alexaf.salarycalc.user.UserDataSetService;
import com.alexaf.salarycalc.user.repository.TgUserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

import static com.alexaf.salarycalc.utils.RandomGenerator.bdGen;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountDataSetService {

    private final UserDataSetService userDataSetService;
    private final AccountRepository accountRepository;

    public AccountEntity create() {
        return create(userDataSetService.create(), null);
    }

    public AccountEntity create(TgUserEntity owner) {
        return create(owner, null);
    }

    public AccountEntity create(TgUserEntity owner, Consumer<AccountEntity> consumer) {
        AccountEntity account = new AccountEntity(
                bdGen(),
                owner
        );

        if (consumer != null)
            consumer.accept(account);

        return accountRepository.save(account);
    }

}
