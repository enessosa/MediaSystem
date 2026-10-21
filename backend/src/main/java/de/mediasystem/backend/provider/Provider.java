package de.mediasystem.backend.provider;

import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.SourceType;

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


    /**
     * vorbereitung zur implementierung von "fetchByID".
     * @return sourcetype
     */
    SourceType supportedSourceType();

    /**
     * Ob der Provider einsatzbereit ist, z.B. weil sein API-Key konfiguriert ist. Nicht verfügbare
     * Provider überspringt MediaService, statt sie anzufragen und mit 401 die ganze Suche scheitern zu lassen.
     * @return true, wenn der Provider angefragt werden kann
     */
    default boolean isAvailable() {
        return true;
    }
}
