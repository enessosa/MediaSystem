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

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
        LOG.error("Externer Provider-Call fehlgeschlagen", e);
        return ResponseEntity.status(502)
                .body("Ein externer Anbieter ist gerade nicht erreichbar. Versuch's später erneut.");
    }

    @ExceptionHandler(InvalidCodewordException.class)
    public ResponseEntity<@NonNull String> handleInvalidCodeword(InvalidCodewordException e) {
        return ResponseEntity.status(400).body(e.getMessage());
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

    /**
     * Gesucht wurde nur nach Medientypen, deren Provider nicht konfiguriert sind (z.B. Spiele ohne RAWG-Key).
     * 503, weil der Dienst dafür auf diesem Server nicht verfügbar ist - kein Fehler des Clients.
     * @param e die Exception
     * @return 503 mit Hinweis, dass die Suche deaktiviert ist
     */
    @ExceptionHandler(MediaTypeUnavailableException.class)
    public ResponseEntity<@NonNull String> handleMediaTypeUnavailable(MediaTypeUnavailableException e) {
        return ResponseEntity.status(503).body(e.getMessage());
    }

    /**
     * Falsches Passwort bei Passwort ändern / Account löschen. Bewusst 400 statt 401: der Nutzer ist
     * eingeloggt, nur seine Eingabe ist falsch (das Frontend wertet 401/403 als abgelaufene Sitzung).
     * @param e die Exception
     * @return 400 mit Meldung
     */
    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<@NonNull String> handleWrongPassword(WrongPasswordException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    /**
     * Der letzte Admin will seinen Account löschen - danach gäbe es niemanden mehr, der das Codewort verwaltet.
     * @param e die Exception
     * @return 409 mit Meldung
     */
    @ExceptionHandler(LastAdminException.class)
    public ResponseEntity<@NonNull String> handleLastAdmin(LastAdminException e) {
        return ResponseEntity.status(409).body(e.getMessage());
    }

    @ExceptionHandler(MediaNotFoundException.class)
    public ResponseEntity<@NonNull String> handleMediaNotFound(MediaNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }
}
