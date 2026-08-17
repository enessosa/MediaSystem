package de.mediasystem.backend.service;

import de.mediasystem.backend.api.dto.MediaSearchResult;
import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.provider.Provider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MediaService {

    private final Provider provider;


    public MediaService(Provider provider) {
        this.provider = provider;
    }

    public List<MediaSearchResult> searchMedia(String query) {
        List<MediaItem> results = provider.searchMedia(query);
        List<MediaSearchResult> converted = new ArrayList<MediaSearchResult>();
        for (MediaItem item : results) {
            Source source = item.getSources().iterator().next();
            MediaSearchResult searchResult = new MediaSearchResult(
                    item.getTitle(),
                    item.getDescription(),
                    item.getReleaseYear(),
                    item.getMediaType(),
                    item.getCreator(),
                    item.getCoverUrl(),
                    source.getSourceType(),
                    source.getExternalId()
            );
            converted.add(searchResult);
        }
        return converted;
    }
}
