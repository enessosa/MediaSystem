package de.mediasystem.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Spring Security laedt den CSRF-Token standardmaessig nur "on demand" (erst wenn ihn jemand
 * per {@code request.getAttribute(...)} tatsaechlich abruft, z.B. beim Rendern eines Formulars).
 * Bei einer reinen REST-API liest das nie jemand serverseitig, wodurch das XSRF-TOKEN-Cookie nie
 * gesetzt wuerde. Dieser Filter erzwingt den Zugriff bei jedem Request, damit das Cookie
 * zuverlaessig gesetzt wird und das Frontend es auslesen kann.
 */
public class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            csrfToken.getToken();
        }
        filterChain.doFilter(request, response);
    }
}
