package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.MeResponse;
import de.mediasystem.backend.service.UserService;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getCurrentUserReturnsOkWithDataOfLoggedInUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("enes");
        MeResponse me = new MeResponse(7L, "enes", "enes@example.org", false);
        when(userService.getCurrentUser("enes")).thenReturn(me);

        ResponseEntity<@NonNull MeResponse> response = userController.getCurrentUser(authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(me);
    }
}
