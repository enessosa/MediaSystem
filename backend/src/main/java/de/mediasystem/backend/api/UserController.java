package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.MeResponse;
import de.mediasystem.backend.service.UserService;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Liefert die Daten des aktuell eingeloggten Nutzers aus der Session.
     * @param authentication der eingeloggte Nutzer, von Spring Security aus der Session eingesetzt
     * @return id, Username, E-Mail und ob der Nutzer Admin ist
     */
    @GetMapping("/users/me")
    public ResponseEntity<@NonNull MeResponse> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getCurrentUser(username));
    }
}
