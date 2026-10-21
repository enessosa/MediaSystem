package de.mediasystem.backend.db;

import de.mediasystem.backend.model.roles.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<@NonNull User, @NonNull Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    /**
     * Anzahl Admins. Admin hat kein eigenes Feld, sondern ist eine eigene Entity-Klasse (Single-Table-Inheritance),
     * deshalb per JPQL über die Klasse statt als abgeleitete Methode wie countByRole.
     * @return Anzahl der Admins
     */
    @Query("select count(a) from Admin a")
    long countAdmins();
}
