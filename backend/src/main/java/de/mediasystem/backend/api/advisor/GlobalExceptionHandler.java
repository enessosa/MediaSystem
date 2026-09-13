package de.mediasystem.backend.api.advisor;

import de.mediasystem.backend.service.exception.*;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Fängt Fehler von externen Provider-Calls ab (RestClient), z.B. fehlender/ungültiger API-Key,
     * Rate-Limit oder die externe API ist down. Bewusst kein Try/Catch pro Provider in MediaService -
     * schlägt ein Provider fehl, soll die komplette Suche fehlschlagen (Qualität vor Vollständigkeit),
     * aber sauber und ohne Stacktrace-Leak statt als roher 500.
     * @param e die RestClient-Exception (HttpClientErrorException, HttpServerErrorException,
     *          ResourceAccessException bei Timeout/Verbindungsproblem, ...)
     * @return 502 mit einer für den Client verständlichen Meldung
     */
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<@NonNull String> handleProviderFailure(RestClientException e) {
        log.error("Externer Provider-Call fehlgeschlagen", e);
        return ResponseEntity.status(502).body("Ein externer Anbieter ist gerade nicht erreichbar. Versuch's später erneut.");
    }

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

    @ExceptionHandler(UserEntryNotFoundException.class)
    public ResponseEntity<@NonNull String> userEntryNotFound(UserEntryNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(MediaNotFoundException.class)
    public ResponseEntity<@NonNull String> handleMediaNotFound(MediaNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }
}
