package de.mediasystem.backend.api.advisor;

import de.mediasystem.backend.service.exception.CodewordNotConfiguredException;
import de.mediasystem.backend.service.exception.EmailAlreadyExistsException;
import de.mediasystem.backend.service.exception.InvalidCodewordException;
import de.mediasystem.backend.service.exception.UsernameAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler advice = new GlobalExceptionHandler();

    @Test
    void handleInvalidCodeword_returns400() {
        ResponseEntity<?> response = advice.handleInvalidCodeword(new InvalidCodewordException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleCodewordNotConfigured_returns500() {
        ResponseEntity<?> response = advice.handleCodewordNotConfigured(new CodewordNotConfiguredException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void handleUsernameAlreadyExists_returns409() {
        ResponseEntity<?> response =
                advice.handleUsernameAlreadyExists(new UsernameAlreadyExistsException("Username is already registered"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handleEmailAlreadyExists_returns409() {
        ResponseEntity<?> response =
                advice.handleEmailAlreadyExists(new EmailAlreadyExistsException("E-Mail is already Registered"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handleBadCredentials_returns401() {
        ResponseEntity<?> response = advice.handleBadCredentials(new BadCredentialsException("Bad credentials"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}