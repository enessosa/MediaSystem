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
            customizer.ignoringRequestMatchers("/auth/**");
        });
        return http.build();
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

}
