package de.mediasystem.backend.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig {


    /**
     * hier kommen die Security Regeln rein.
     * @param http wird von Spring boot erstellt und der methode ohne aufruf übergeben.
     * @return gibt eine liste an Filtern zurück, die angewandt werden soll
     * @throws Exception wegen der API ist es nötig ohne weitere Konkretisierung eine generelle Exception zu werfen.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/auth/**", "/error").permitAll();
            auth.anyRequest().authenticated();
        });
        http.csrf(customizer -> {
            customizer.csrfTokenRepository(csrfTokenRepository());
            customizer.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler());
            customizer.ignoringRequestMatchers("/auth/**");
        });
        http.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);
        http.cors(customizer -> customizer.configurationSource(corsConfigurationSource()));
        return http.build();
    }

    /**
     * Cookie-basiertes CSRF fuer die SPA: der Token liegt in einem fuer JavaScript lesbaren
     * Cookie (XSRF-TOKEN, kein HttpOnly), das Frontend liest ihn aus und schickt ihn bei
     * state-veraendernden Requests als Header (X-XSRF-TOKEN) mit.
     * @return das CSRF-Token-Repository
     */
    private CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    /**
     * Erlaubt Cross-Origin-Anfragen vom Frontend (Vite-Dev-Server, anderer Origin/Port als das Backend).
     * allowCredentials ist nötig, damit das Session-Cookie (ADR-005) bei den Anfragen mitgeschickt wird.
     * @return die CORS-Regeln für alle Endpunkte
     */
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * diese Methode stellt den zentralen Password-Hasher als bean bereit. wird dann zu mHashen beim registrieren  und
     * login benutzt.
     * @return das hasher objekt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

}
