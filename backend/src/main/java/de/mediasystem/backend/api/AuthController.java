package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.LoginRequest;
import de.mediasystem.backend.api.dto.RegisterRequest;
import de.mediasystem.backend.api.dto.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import de.mediasystem.backend.service.AuthService;

@RestController
public class AuthController {

    private final AuthService authService;
    private final SecurityContextRepository repository;

    public AuthController(AuthService authService, SecurityContextRepository repository) {
        this.authService = authService;
        this.repository = repository;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<@NonNull RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(201).body(authService.register(request));
    }

    /**
     * Loggt einen Nutzer per Username ein und persistiert die Server-Side Session.
     * @param request Login-Credentials
     * @param httpServletRequest die eingehende HTTP-Anfrage, für die die Session gespeichert wird
     * @param httpServletResponse die HTTP-Antwort, in die das Session-Cookie geschrieben wird
     * @return das Authentication-Objekt des eingeloggten Nutzers
     */
    @PostMapping("/auth/login")
    public ResponseEntity<@NonNull Authentication> login(@RequestBody @Valid LoginRequest request,
                                                         HttpServletRequest httpServletRequest,
                                                         HttpServletResponse httpServletResponse) {
        Authentication auth = authService.login(request);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        repository.saveContext(context, httpServletRequest, httpServletResponse);
        return ResponseEntity.ok(auth);
    }

}
