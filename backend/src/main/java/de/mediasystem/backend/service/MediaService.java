package de.mediasystem.backend.service;

import de.mediasystem.backend.api.dto.MediaSearchResult;
import de.mediasystem.backend.db.MediaItemRepository;
import de.mediasystem.backend.db.SourceRepository;
import de.mediasystem.backend.db.UserEntryRepository;
import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.provider.Provider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MediaService {

    private final Provider provider;
    private final MediaItemRepository mediaItemRepository;
    private final SourceRepository sourceRepository;
    private final UserEntryRepository userEntryRepository;


    public MediaService(Provider provider,
                        MediaItemRepository mediaItemRepository,
                        SourceRepository sourceRepository,
                        UserEntryRepository userEntryRepository) {
        this.provider = provider;
        this.mediaItemRepository = mediaItemRepository;
        this.sourceRepository = sourceRepository;
        this.userEntryRepository = userEntryRepository;
    }

    /**
     * this method converts search results into a record class to prepare the data that is send to the frontend.
     * @param query is the query for the search
     * @return the converted Record class wrapped  in a list.
     */
    public List<MediaSearchResult> searchMedia(String query) {
        List<MediaItem> results = provider.searchMedia(query);
        List<MediaSearchResult> converted = new ArrayList<>();
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
