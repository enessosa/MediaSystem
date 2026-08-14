package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.RegisterRequest;
import de.mediasystem.backend.model.roles.User;
import de.mediasystem.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

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
}