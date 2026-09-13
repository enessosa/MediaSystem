package de.mediasystem.backend.provider;

import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.MediaType;
import java.util.List;
import java.util.Set;

public interface Provider {

    List<MediaItem> searchMedia(String search);

    MediaItem fetchById(String id);

    /**
     * Welche MediaTypes dieser Provider liefern kann. Erlaubt MediaService, bei einer
     * Suche mit MediaType-Filter nur die relevanten Provider anzufragen statt alle.
     * @return die abgedeckten MediaTypes
     */
    Set<MediaType> supportedMediaTypes();
}
