package de.mediasystem.backend.service;

import de.mediasystem.backend.api.dto.MeResponse;
import de.mediasystem.backend.db.UserRepository;
import de.mediasystem.backend.model.roles.Admin;
import de.mediasystem.backend.model.roles.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

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
}
