package de.mediasystem.backend.api.advisor;

import de.mediasystem.backend.service.exception.LastAdminException;
import de.mediasystem.backend.service.exception.EmailAlreadyExistsException;
import de.mediasystem.backend.service.exception.InvalidCodewordException;
import de.mediasystem.backend.service.exception.MediaTypeUnavailableException;
import de.mediasystem.backend.service.exception.UserEntryNotFoundException;
import de.mediasystem.backend.service.exception.UsernameAlreadyExistsException;
import de.mediasystem.backend.service.exception.WrongPasswordException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.client.ResourceAccessException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler advice = new GlobalExceptionHandler();

    @Test
    void handleInvalidCodewordReturns400() {
        ResponseEntity<?> response = advice.handleInvalidCodeword(new InvalidCodewordException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleLastAdminReturns409() {
        ResponseEntity<?> response = advice.handleLastAdmin(new LastAdminException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handleUsernameAlreadyExistsReturns409() {
        ResponseEntity<?> response = advice.handleUsernameAlreadyExists(
                new UsernameAlreadyExistsException("Username is already registered"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handleEmailAlreadyExistsReturns409() {
        ResponseEntity<?> response =
                advice.handleEmailAlreadyExists(new EmailAlreadyExistsException("E-Mail is already Registered"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handleBadCredentialsReturns401() {
        ResponseEntity<?> response = advice.handleBadCredentials(new BadCredentialsException("Bad credentials"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void userEntryNotFoundReturns404() {
        ResponseEntity<?> response = advice.userEntryNotFound(new UserEntryNotFoundException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void handleProviderFailureReturns502WithCleanMessageNotRawException() {
        ResourceAccessException providerException = new ResourceAccessException("Connection refused");

        ResponseEntity<?> response = advice.handleProviderFailure(providerException);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).isNotEqualTo(providerException.getMessage());
        assertThat(response.getBody()).asString().doesNotContain("Connection refused");
    }

    @Test
    void handleMediaTypeUnavailableReturns503WithMessage() {
        ResponseEntity<?> response = advice.handleMediaTypeUnavailable(new MediaTypeUnavailableException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).asString().contains("deaktiviert");
    }

    @Test
    void handleWrongPasswordReturns400WithMessage() {
        ResponseEntity<?> response = advice.handleWrongPassword(new WrongPasswordException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Das aktuelle Passwort ist falsch.");
    }
}
