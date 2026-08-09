package de.mediasystem.backend.db;

import de.mediasystem.backend.model.UserEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserEntryRepository extends JpaRepository<UserEntry, Long> {

    List<UserEntry> findByUserId(Long userId);

    /** Duplikat-Prüfung Ebene 2: existiert für diesen Nutzer bereits ein Eintrag zu diesem Medium? */
    boolean existsByUserIdAndMediaItemId(Long userId, Long mediaItemId);
}
