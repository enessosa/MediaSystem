package de.mediasystem.backend.db;

import de.mediasystem.backend.model.UserEntry;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserEntryRepository extends JpaRepository<@NonNull UserEntry, @NonNull Long> {

    List<UserEntry> findByUserId(Long userId);

    /**
     * Duplikat-Prüfung Ebene 2: existiert für diesen Nutzer bereits ein Eintrag zu diesem Medium?
     * @param userId der Nutzer
     * @param mediaItemId das Medium
     * @return true, falls bereits ein Eintrag existiert
     */
    boolean existsByUserIdAndMediaItemId(Long userId, Long mediaItemId);
}
