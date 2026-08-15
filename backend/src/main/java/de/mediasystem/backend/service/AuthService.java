package de.mediasystem.backend.service;

import de.mediasystem.backend.api.dto.LoginRequest;
import de.mediasystem.backend.api.dto.RegisterRequest;
import de.mediasystem.backend.db.AppSettingRepository;
import de.mediasystem.backend.db.UserRepository;
import de.mediasystem.backend.model.AppSetting;
import de.mediasystem.backend.model.roles.User;
import de.mediasystem.backend.service.exception.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AppSettingRepository appSettingRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       AppSettingRepository appSettingRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.appSettingRepository = appSettingRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public User register(RegisterRequest request) {
        if (userRepository.count() >= 10) {
            AppSetting appSetting = appSettingRepository.findById("signup_codeword")
                    // Intellij meinte ich soll das mit :: machen anstatt lambda.
                    .orElseThrow(CodewordNotConfiguredException::new);
            if (!Objects.equals(request.codeword(),
                    appSetting.getValue())) {
                throw new InvalidCodewordException();
            }
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("E-Mail is already Registered");
        } else if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Username is already registered");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return user;
    }

    public Authentication login(LoginRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                request.identifier(),
                request.password());
        return authenticationManager.authenticate(token);
    }
}
