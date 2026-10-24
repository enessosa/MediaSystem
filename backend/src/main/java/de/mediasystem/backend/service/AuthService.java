package de.mediasystem.backend.service;

import de.mediasystem.backend.api.dto.LoginRequest;
import de.mediasystem.backend.api.dto.RegisterRequest;
import de.mediasystem.backend.api.dto.RegisterResponse;
import de.mediasystem.backend.db.AppSettingRepository;
import de.mediasystem.backend.db.UserRepository;
import de.mediasystem.backend.model.AppSetting;
import de.mediasystem.backend.model.roles.Admin;
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

    /**
     * Registriert einen neuen Nutzer, inkl. Signup-Cap/Codewort-Prüfung und Duplikat-Prüfung.
     * Ab dem 11. Nutzer ist das Codewort Pflicht - aber nur, wenn ein Admin eins gesetzt hat. Ohne Codewort
     * ist die Registrierung offen (bewusst, für privates Hosting; der Admin kann es jederzeit wieder setzen).
     * Der allererste Nutzer wird automatisch Admin, damit jede neue Instanz einen Betreiber hat.
     * @param request die Registrierungsdaten
     * @return der neu angelegte Nutzer, ohne Passwort-Hash
     */
    public RegisterResponse register(RegisterRequest request) {
        long userCount = userRepository.count();
        if (userCount >= 10) {
            Optional<AppSetting> codeword = appSettingRepository.findById("signup_codeword");
            if (codeword.isPresent() && !Objects.equals(request.codeword(), codeword.get().getValue())) {
                throw new InvalidCodewordException();
            }
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Username is already registered");
        }

        User user = userCount == 0 ? new Admin() : new User();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return new RegisterResponse(user.getId(), user.getUsername(), user.getCreatedAt());
    }

    /**
     * Authentifiziert einen Nutzer per Username und Passwort.
     * @param request Login-Credentials
     * @return das Authentication-Objekt des eingeloggten Nutzers
     */
    public Authentication login(LoginRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                request.identifier(),
                request.password());
        return authenticationManager.authenticate(token);
    }
}
