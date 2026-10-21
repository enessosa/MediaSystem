package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.ChangePasswordRequest;
import de.mediasystem.backend.api.dto.DeleteAccountRequest;
import de.mediasystem.backend.api.dto.MeResponse;
import de.mediasystem.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * Ändert das Passwort des eingeloggten Nutzers. Die aktuelle Session bleibt gültig.
     * @param request aktuelles und neues Passwort
     * @param authentication der eingeloggte Nutzer
     * @return 204 ohne Body
     */
    @PutMapping("/users/me/password")
    public ResponseEntity<@NonNull Void> changePassword(@RequestBody @Valid ChangePasswordRequest request,
                                                        Authentication authentication) {
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Löscht den Account des eingeloggten Nutzers und beendet danach die Session - sonst hinge eine
     * Session an einem User, den es nicht mehr gibt.
     * @param request das Passwort zur Bestätigung
     * @param authentication der eingeloggte Nutzer
     * @param httpServletRequest die Anfrage, deren Session beendet wird
     * @param httpServletResponse die Antwort
     * @return 204 ohne Body
     */
    @DeleteMapping("/users/me")
    public ResponseEntity<@NonNull Void> deleteAccount(@RequestBody @Valid DeleteAccountRequest request,
                                                       Authentication authentication,
                                                       HttpServletRequest httpServletRequest,
                                                       HttpServletResponse httpServletResponse) {
        userService.deleteAccount(authentication.getName(), request.password());
        new SecurityContextLogoutHandler().logout(httpServletRequest, httpServletResponse, authentication);
        return ResponseEntity.noContent().build();
    }
}
