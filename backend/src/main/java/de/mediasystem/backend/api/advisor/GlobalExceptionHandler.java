package de.mediasystem.backend.api.advisor;

import de.mediasystem.backend.service.exception.*;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCodewordException.class)
    public ResponseEntity<@NonNull String> handleInvalidCodeword(InvalidCodewordException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(CodewordNotConfiguredException.class)
    public ResponseEntity<@NonNull String> handleCodewordNotConfigured(CodewordNotConfiguredException e) {
        return ResponseEntity.status(500).body(e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<@NonNull String> handleEmailAlreadyExists(EmailAlreadyExistsException e) {
        return ResponseEntity.status(409).body(e.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<@NonNull String> handleUsernameAlreadyExists(UsernameAlreadyExistsException e) {
        return ResponseEntity.status(409).body(e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<@NonNull String> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(401).body(e.getMessage());
    }

    @ExceptionHandler(AlreadyInListException.class)
    public ResponseEntity<@NonNull String> handleAlreadyInList(AlreadyInListException e) {
        return ResponseEntity.status(409).body(e.getMessage());
    }
}
