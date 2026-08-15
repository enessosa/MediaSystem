package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.LoginRequest;
import de.mediasystem.backend.api.dto.RegisterRequest;
import de.mediasystem.backend.model.roles.User;
import de.mediasystem.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.SecurityContextRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private SecurityContextRepository securityContextRepository;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_validRequest_returnsCreatedAndCallsService() {
        User savedUser = new User();
        savedUser.setUsername("newuser");
        savedUser.setEmail("new@test.de");
        when(authService.register(any(RegisterRequest.class))).thenReturn(savedUser);

        RegisterRequest request = new RegisterRequest("newuser", "new@test.de", "rawPassword", null);

        ResponseEntity<?> response = authController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(authService).register(request);
    }

    @Test
    void login_validCredentials_returnsOkAndPersistsSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        when(authService.login(any(LoginRequest.class))).thenReturn(authentication);

        LoginRequest request = new LoginRequest("testuser", "rawPassword");
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        ResponseEntity<?> response = authController.login(request, httpRequest, httpResponse);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(authentication);
        verify(securityContextRepository)
                .saveContext(any(SecurityContext.class), eq(httpRequest), eq(httpResponse));
    }
}