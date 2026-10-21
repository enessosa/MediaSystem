package de.mediasystem.backend.service;

import de.mediasystem.backend.api.dto.ChangePasswordRequest;
import de.mediasystem.backend.api.dto.MeResponse;
import de.mediasystem.backend.db.UserRepository;
import de.mediasystem.backend.model.roles.Admin;
import de.mediasystem.backend.model.roles.User;
import de.mediasystem.backend.service.exception.LastAdminException;
import de.mediasystem.backend.service.exception.WrongPasswordException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getCurrentUserReturnsUserDataWithoutAdminFlag() {
        User user = new User();
        user.setId(7L);
        user.setUsername("enes");
        user.setEmail("enes@example.org");
        when(userRepository.findByUsername("enes")).thenReturn(Optional.of(user));

        MeResponse response = userService.getCurrentUser("enes");

        assertThat(response).isEqualTo(new MeResponse(7L, "enes", "enes@example.org", false));
    }

    @Test
    void getCurrentUserSetsAdminFlagForAdmin() {
        // Hibernate liefert für Zeilen mit role = 'ADMIN' eine Admin-Instanz - das simulieren wir hier
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setUsername("chef");
        admin.setEmail("chef@example.org");
        when(userRepository.findByUsername("chef")).thenReturn(Optional.of(admin));

        MeResponse response = userService.getCurrentUser("chef");

        assertThat(response.admin()).isTrue();
    }

    @Test
    void getCurrentUserThrowsWhenUserDoesNotExist() {
        when(userRepository.findByUsername("geloescht")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser("geloescht"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void changePasswordWithCorrectCurrentPasswordStoresNewHash() {
        User user = userWithHash("alter-hash");
        when(userRepository.findByUsername("enes")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("altesPasswort", "alter-hash")).thenReturn(true);
        when(passwordEncoder.encode("neuesPasswort")).thenReturn("neuer-hash");

        userService.changePassword("enes", new ChangePasswordRequest("altesPasswort", "neuesPasswort"));

        assertThat(user.getPasswordHash()).isEqualTo("neuer-hash");
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordWithWrongCurrentPasswordThrowsAndSavesNothing() {
        User user = userWithHash("alter-hash");
        when(userRepository.findByUsername("enes")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("falsch", "alter-hash")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword("enes",
                new ChangePasswordRequest("falsch", "neuesPasswort")))
                .isInstanceOf(WrongPasswordException.class);
        assertThat(user.getPasswordHash()).isEqualTo("alter-hash");
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteAccountWithCorrectPasswordDeletesUser() {
        User user = userWithHash("hash");
        when(userRepository.findByUsername("enes")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("passwort", "hash")).thenReturn(true);

        userService.deleteAccount("enes", "passwort");

        verify(userRepository).delete(user);
    }

    @Test
    void deleteAccountWithWrongPasswordThrowsAndDeletesNothing() {
        User user = userWithHash("hash");
        when(userRepository.findByUsername("enes")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("falsch", "hash")).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteAccount("enes", "falsch"))
                .isInstanceOf(WrongPasswordException.class);
        verify(userRepository, never()).delete(any());
    }

    private User userWithHash(String passwordHash) {
        User user = new User();
        user.setUsername("enes");
        user.setPasswordHash(passwordHash);
        return user;
    }

    @Test
    void deleteAccountOfLastAdminThrowsAndDeletesNothing() {
        Admin admin = new Admin();
        admin.setUsername("chef");
        admin.setPasswordHash("hash");
        when(userRepository.findByUsername("chef")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("passwort", "hash")).thenReturn(true);
        when(userRepository.countAdmins()).thenReturn(1L);

        assertThatThrownBy(() -> userService.deleteAccount("chef", "passwort"))
                .isInstanceOf(LastAdminException.class);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteAccountOfAdminWithOtherAdminsDeletesUser() {
        Admin admin = new Admin();
        admin.setUsername("chef");
        admin.setPasswordHash("hash");
        when(userRepository.findByUsername("chef")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("passwort", "hash")).thenReturn(true);
        when(userRepository.countAdmins()).thenReturn(2L);

        userService.deleteAccount("chef", "passwort");

        verify(userRepository).delete(admin);
    }
}
