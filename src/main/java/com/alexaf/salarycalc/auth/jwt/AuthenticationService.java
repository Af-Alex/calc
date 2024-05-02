package com.alexaf.salarycalc.auth.jwt;

import com.alexaf.salarycalc.auth.jwt.dto.JwtAuthenticationResponse;
import com.alexaf.salarycalc.auth.jwt.dto.SignInRequest;
import com.alexaf.salarycalc.auth.jwt.dto.SignUpRequest;
import com.alexaf.salarycalc.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import static com.alexaf.salarycalc.user.dto.Role.USER;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        userService.create(request.getUsername(), request.getPassword(), USER, null);
        String jwt = jwtService.generateToken(userDetailsService.loadUserByUsername(request.getUsername()));
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());

        return new JwtAuthenticationResponse(jwtService.generateToken(user));
    }
}
