package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.ChangePasswordRequest;
import de.mediasystem.backend.api.dto.DeleteAccountRequest;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
        MeResponse me = new MeResponse(7L, "enes", false);
        when(userService.getCurrentUser("enes")).thenReturn(me);

        ResponseEntity<@NonNull MeResponse> response = userController.getCurrentUser(authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(me);
    }

    @Test
    void changePasswordReturnsNoContentAndPassesUsernameToService() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("enes");
        ChangePasswordRequest request = new ChangePasswordRequest("alt", "neu");

        ResponseEntity<@NonNull Void> response = userController.changePassword(request, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userService).changePassword("enes", request);
    }

    @Test
    void deleteAccountReturnsNoContentAndInvalidatesSession() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("enes");
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setSession(session);

        ResponseEntity<@NonNull Void> response = userController.deleteAccount(
                new DeleteAccountRequest("passwort"), authentication, httpRequest, new MockHttpServletResponse());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userService).deleteAccount("enes", "passwort");
        assertThat(session.isInvalid()).isTrue();
    }
}
