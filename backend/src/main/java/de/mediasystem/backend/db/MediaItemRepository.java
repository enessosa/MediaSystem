package de.mediasystem.backend.db;

import de.mediasystem.backend.model.MediaItem;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaItemRepository extends JpaRepository<@NonNull MediaItem, @NonNull Long> {
}
