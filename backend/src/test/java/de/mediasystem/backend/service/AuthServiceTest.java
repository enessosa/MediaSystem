package de.mediasystem.backend.service;

import de.mediasystem.backend.api.dto.LoginRequest;
import de.mediasystem.backend.api.dto.RegisterRequest;
import de.mediasystem.backend.api.dto.RegisterResponse;
import de.mediasystem.backend.db.AppSettingRepository;
import de.mediasystem.backend.db.UserRepository;
import de.mediasystem.backend.model.AppSetting;
import de.mediasystem.backend.model.roles.Admin;
import de.mediasystem.backend.model.roles.User;
import de.mediasystem.backend.service.exception.EmailAlreadyExistsException;
import de.mediasystem.backend.service.exception.InvalidCodewordException;
import de.mediasystem.backend.service.exception.UsernameAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String CODEWORD_SETTING_KEY = "signup_codeword";

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppSettingRepository appSettingRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerBelowCapCreatesUserWithoutCodewordCheck() {
        when(userRepository.count()).thenReturn(5L);
        when(passwordEncoder.encode("rawPassword")).thenReturn("hashed-password");
        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(savedUser.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterRequest request = new RegisterRequest("newuser", "new@test.de", "rawPassword", null);

        RegisterResponse result = authService.register(request);

        assertThat(result.username()).isEqualTo("newuser");
        assertThat(result.email()).isEqualTo("new@test.de");
        assertThat(savedUser.getValue().getPasswordHash()).isEqualTo("hashed-password");
        verify(appSettingRepository, never()).findById(any());
    }

    @Test
    void registerAtCapWithCorrectCodewordCreatesUser() {
        when(userRepository.count()).thenReturn(10L);
        AppSetting codewordSetting = new AppSetting();
        codewordSetting.setKey(CODEWORD_SETTING_KEY);
        codewordSetting.setValue("geheim");
        when(appSettingRepository.findById(CODEWORD_SETTING_KEY)).thenReturn(Optional.of(codewordSetting));
        when(passwordEncoder.encode(any())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterRequest request = new RegisterRequest("newuser", "new@test.de", "rawPassword", "geheim");

        RegisterResponse result = authService.register(request);

        assertThat(result.username()).isEqualTo("newuser");
    }

    @Test
    void registerAtCapWithWrongCodewordThrowsAndDoesNotSave() {
        when(userRepository.count()).thenReturn(10L);
        AppSetting codewordSetting = new AppSetting();
        codewordSetting.setKey(CODEWORD_SETTING_KEY);
        codewordSetting.setValue("geheim");
        when(appSettingRepository.findById(CODEWORD_SETTING_KEY)).thenReturn(Optional.of(codewordSetting));

        RegisterRequest request = new RegisterRequest("newuser", "new@test.de", "rawPassword", "falsch");

        assertThatExceptionOfType(InvalidCodewordException.class)
                .isThrownBy(() -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerAtCapWithMissingCodewordThrowsAndDoesNotSave() {
        when(userRepository.count()).thenReturn(10L);
        AppSetting codewordSetting = new AppSetting();
        codewordSetting.setKey(CODEWORD_SETTING_KEY);
        codewordSetting.setValue("geheim");
        when(appSettingRepository.findById(CODEWORD_SETTING_KEY)).thenReturn(Optional.of(codewordSetting));

        RegisterRequest request = new RegisterRequest("newuser", "new@test.de", "rawPassword", null);

        assertThatExceptionOfType(InvalidCodewordException.class)
                .isThrownBy(() -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerAtCapWithNoCodewordConfiguredIsOpenAndCreatesUser() {
        when(userRepository.count()).thenReturn(10L);
        when(appSettingRepository.findById(CODEWORD_SETTING_KEY)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterRequest request = new RegisterRequest("newuser", "new@test.de", "rawPassword", null);

        RegisterResponse result = authService.register(request);

        assertThat(result.username()).isEqualTo("newuser");
    }

    @Test
    void registerFirstUserBecomesAdmin() {
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(any())).thenReturn("hashed-password");
        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(savedUser.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        authService.register(new RegisterRequest("betreiber", "admin@test.de", "rawPassword", null));

        assertThat(savedUser.getValue()).isInstanceOf(Admin.class);
    }

    @Test
    void registerLaterUserIsNoAdmin() {
        when(userRepository.count()).thenReturn(1L);
        when(passwordEncoder.encode(any())).thenReturn("hashed-password");
        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(savedUser.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        authService.register(new RegisterRequest("zweiter", "user@test.de", "rawPassword", null));

        assertThat(savedUser.getValue()).isNotInstanceOf(Admin.class);
    }

    @Test
    void registerUsernameAlreadyTakenThrowsAndDoesNotSave() {
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        RegisterRequest request = new RegisterRequest("newuser", "new@test.de", "rawPassword", null);

        assertThatExceptionOfType(UsernameAlreadyExistsException.class)
                .isThrownBy(() -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerEmailAlreadyTakenThrowsAndDoesNotSave() {
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.existsByEmail("new@test.de")).thenReturn(true);

        RegisterRequest request = new RegisterRequest("newuser", "new@test.de", "rawPassword", null);

        assertThatExceptionOfType(EmailAlreadyExistsException.class)
                .isThrownBy(() -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerNeverStoresPlaintextPassword() {
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode("rawPassword")).thenReturn("hashed-password");
        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(savedUser.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterRequest request = new RegisterRequest("newuser", "new@test.de", "rawPassword", null);

        authService.register(request);

        assertThat(savedUser.getValue().getPasswordHash())
                .isEqualTo("hashed-password")
                .isNotEqualTo("rawPassword");
    }

    @Test
    void loginDelegatesToAuthenticationManagerAndReturnsItsResult() {
        Authentication expectedAuthentication = mock(Authentication.class);
        ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        when(authenticationManager.authenticate(tokenCaptor.capture())).thenReturn(expectedAuthentication);

        LoginRequest request = new LoginRequest("testuser", "rawPassword");

        Authentication result = authService.login(request);

        assertThat(result).isEqualTo(expectedAuthentication);
        assertThat(tokenCaptor.getValue().getPrincipal()).isEqualTo("testuser");
        assertThat(tokenCaptor.getValue().getCredentials()).isEqualTo("rawPassword");
    }
}