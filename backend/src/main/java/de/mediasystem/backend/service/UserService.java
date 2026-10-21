package de.mediasystem.backend.service;


import de.mediasystem.backend.api.dto.ChangePasswordRequest;
import de.mediasystem.backend.api.dto.MeResponse;
import de.mediasystem.backend.db.UserRepository;
import de.mediasystem.backend.model.roles.Admin;
import de.mediasystem.backend.model.roles.User;
import de.mediasystem.backend.service.exception.LastAdminException;
import de.mediasystem.backend.service.exception.WrongPasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * gets userid  by usename.
     * @param username the username
     * @return the id
     */
    public Long getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return user.getId();
    }

    /**
     * this method gets the user with only the username.
     * @param username the username
     * @return user data
     */
    public MeResponse getCurrentUser(String username) {
        User user  = userRepository.findByUsername(username).orElseThrow();
        boolean isAdmin = user instanceof Admin;
        return new MeResponse(user.getId(), user.getUsername(), user.getEmail(), isAdmin);
    }

    /**
     * Ändert das Passwort, nachdem das aktuelle Passwort bestätigt wurde.
     * BCrypt-Hashes enthalten einen zufälligen Salt, deshalb wird mit matches() verglichen statt mit equals().
     * @param username der eingeloggte Nutzer
     * @param request aktuelles und neues Passwort
     * @throws WrongPasswordException wenn das aktuelle Passwort nicht stimmt
     */
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow();
        checkPassword(user, request.currentPassword());
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    /**
     * Löscht den Account nach Passwort-Bestätigung (DSGVO Art. 17). Die UserEntries des Nutzers löscht die
     * Datenbank selbst (user_entry.user_id ... ON DELETE CASCADE, siehe V1__init.sql). Die MediaItems bleiben,
     * da sie nutzerübergreifend geteilt sind.
     * @param username der eingeloggte Nutzer
     * @param password das Passwort zur Bestätigung
     * @throws WrongPasswordException wenn das Passwort nicht stimmt
     * @throws LastAdminException wenn der Nutzer der einzige Admin ist
     */
    public void deleteAccount(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow();
        checkPassword(user, password);
        if (user instanceof Admin && userRepository.countAdmins() <= 1) {
            throw new LastAdminException();
        }
        userRepository.delete(user);
    }

    private void checkPassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new WrongPasswordException();
        }
    }
}
