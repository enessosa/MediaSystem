package de.mediasystem.backend.provider.openlibrary;

import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.provider.Provider;

import java.util.List;

public class OpenLibraryProvider implements Provider {


    @Override
    public List<MediaItem> searchMedia(String search) {
        return List.of();
    }

    @Override
    public MediaItem fetchById(String id) {
        return null;
    }
}
