package com.alexaf.salarycalc.user;

import com.alexaf.salarycalc.telegram.ChatState;
import com.alexaf.salarycalc.user.repository.TgUserEntity;
import com.alexaf.salarycalc.user.repository.TgUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static com.alexaf.salarycalc.utils.RandomGenerator.bdGen;
import static com.alexaf.salarycalc.utils.RandomGenerator.strGen;

@Service
@RequiredArgsConstructor
@Transactional
public class UserDataSetService {

    private final static AtomicLong id = new AtomicLong(0);

    private final TgUserRepository userRepository;

    public TgUserEntity create() {
        return create(null);
    }

    public TgUserEntity create(Consumer<TgUserEntity> consumer) {
        Long userId = id.getAndIncrement();
        String username = "USER"+ userId + strGen();
        TgUserEntity user = new TgUserEntity(
                userId,
                null,
                username,
                true,
                bdGen(),
                ChatState.WELCOME,
                "FIRSTNAME_" + username,
                "LASTNAME_" + username
        );

        if (consumer != null) {
            consumer.accept(user);
        }

        return userRepository.save(user);
    }
}
