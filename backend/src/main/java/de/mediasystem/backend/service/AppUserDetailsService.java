package de.mediasystem.backend.service;

import de.mediasystem.backend.db.UserRepository;
import de.mediasystem.backend.model.roles.Admin;
import de.mediasystem.backend.model.roles.User;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class AppUserDetailsService implements UserDetailsService {

    /**
     * das ist die verbindung zum UserRepository.
     */
    private final UserRepository userRepository;

    /**
     * Konstruktor Injection.
     * @param userRepository als Bean von Springboot eingesetzt.
     */
    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Lädt den Nutzer zum Username aus der DB und mappt ihn auf.
     * Aufgerufen von Spring Security beim Login.
     *
     * @param username der eingegebene Username
     * @return UserDetails des Nutzers
     * @throws UsernameNotFoundException wenn kein solcher Nutzer existiert
     */
    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException(username));
        // Hibernate liefert dank @Inheritance(SINGLE_TABLE) je nach "role"-Discriminator-Spalte
        // tatsächlich eine Admin- oder eine User-Instanz zurück - instanceof funktioniert also echt.
        String[] roles = user instanceof Admin ? new String[] {"USER", "ADMIN"} : new String[] {"USER"};
        // hier ganzer pfad um klar zu machen dass es nicht das gleiceh ist  wie roles.User
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .roles(roles)
                .build();
    }

}
