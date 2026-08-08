package de.mediasystem.backend.provider;

import de.mediasystem.backend.model.MediaItem;
import java.util.List;

public class TmdbProvider implements Provider {
    @Override
    public List<MediaItem> searchMedia(String search) {
        return List.of();
    }

    @Override
    public MediaItem fetchById(String id) {
        return null;
    }
}
