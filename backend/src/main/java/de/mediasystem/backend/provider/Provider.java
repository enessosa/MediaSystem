package de.mediasystem.backend.provider;

import de.mediasystem.backend.model.MediaItem;
import java.util.List;

public interface Provider {

    List<MediaItem> searchMedia(String search);

    MediaItem fetchById(String id);
}
