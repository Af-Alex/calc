package com.alexaf.salarycalc.user;

import com.alexaf.salarycalc.goal.GoalService;
import com.alexaf.salarycalc.user.repository.User;
import com.alexaf.salarycalc.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.alexaf.salarycalc.utils.RandomGenerator.emailGen;
import static com.alexaf.salarycalc.utils.RandomGenerator.strGen;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDatasetService {

    private final UserRepository userRepository;
    private final GoalService goalService;

    public User create() {
        var user = userFromAPI();
        return userRepository.save(user);
    }

    public User createWithDefaultGoals() {
        var user = userRepository.save(userFromAPI());

        goalService.createDefaultGoals(user.getId());
        return user;
    }

    private User userFromAPI() {
        return new User(
                emailGen(),
                strGen(),
                strGen(),
                strGen()
        );
    }
}
