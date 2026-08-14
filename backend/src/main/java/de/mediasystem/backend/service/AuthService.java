package de.mediasystem.backend.service;

import de.mediasystem.backend.api.dto.RegisterRequest;
import de.mediasystem.backend.db.AppSettingRepository;
import de.mediasystem.backend.db.UserRepository;
import de.mediasystem.backend.model.AppSetting;
import de.mediasystem.backend.model.roles.User;
import de.mediasystem.backend.service.exception.EmailAlreadyExistsException;
import de.mediasystem.backend.service.exception.InvalidCodewordException;
import de.mediasystem.backend.service.exception.UsernameAlreadyExistsException;
import jakarta.validation.constraints.Email;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AppSettingRepository appSettingRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       AppSettingRepository appSettingRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.appSettingRepository = appSettingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {
        if (userRepository.count() >= 10) {
            AppSetting appSetting = appSettingRepository.findById("signup_codeword")
                    // Intellij meinte ich soll das mit :: machen anstatt lambda.
                    .orElseThrow(InvalidCodewordException::new);
            if (!Objects.equals(request.codeword(),
                    appSetting.getValue())) {
                throw new InvalidCodewordException();
            }
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        } else if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException();
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return user;
    }
}
