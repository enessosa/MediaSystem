package de.mediasystem.backend.db;

import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.SourceType;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SourceRepository extends JpaRepository<@NonNull Source, @NonNull Long> {

    /**
     * Duplikat-Prüfung Ebene 1: existiert dieses externe Medium global bereits?
     * @param sourceType die Herkunfts-API
     * @param externalId die externe ID bei dieser Herkunfts-API
     * @return der bestehende Source-Eintrag, falls vorhanden
     */
    Optional<Source> findBySourceTypeAndExternalId(SourceType sourceType, String externalId);

    // boolean existsBySourceTypeAndExternalId(SourceType sourceType, String externalId);
}
