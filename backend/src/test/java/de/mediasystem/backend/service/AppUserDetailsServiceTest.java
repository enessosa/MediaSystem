package de.mediasystem.backend.service;

import de.mediasystem.backend.db.UserRepository;
import de.mediasystem.backend.model.roles.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    @Test
    void loadUserByUsername_mapsExistingUserToUserDetails() {
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("hashed-password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails result = appUserDetailsService.loadUserByUsername("testuser");

        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("hashed-password");
        assertThat(result.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_USER");
    }

    @Test
    void loadUserByUsername_throwsWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> appUserDetailsService.loadUserByUsername("ghost"));
    }
}