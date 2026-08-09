package de.mediasystem.backend.db;

import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.SourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SourceRepository extends JpaRepository<Source, Long> {

    /** Duplikat-Prüfung Ebene 1: existiert dieses externe Medium global bereits? */
    Optional<Source> findBySourceTypeAndExternalId(SourceType sourceType, String externalId);

    boolean existsBySourceTypeAndExternalId(SourceType sourceType, String externalId);
}
