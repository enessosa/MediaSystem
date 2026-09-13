package de.mediasystem.backend.provider.openlibrary;

import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.SourceType;
import de.mediasystem.backend.provider.Provider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class OpenLibraryProvider implements Provider {

    private final RestClient restClient;

    public OpenLibraryProvider(@Qualifier("openLibraryRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<MediaItem> searchMedia(String search) {
        OpenLibrarySearchResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search.json")
                        .queryParam("q", search)
                        .build())
                .retrieve()
                .body(OpenLibrarySearchResponse.class);
        return mapToMediaItems(response);
    }

    @Override
    public MediaItem fetchById(String id) {
        return null;
    }

    @Override
    public Set<MediaType> supportedMediaTypes() {
        return Set.of(MediaType.BOOK);
    }

    /**
     * map method.
     * @param response the raw response
     * @return the list of MediaItems
     */
    public List<MediaItem> mapToMediaItems(OpenLibrarySearchResponse response) {

        List<MediaItem> result = new ArrayList<>();

        if (response == null) {
            return result;
        }

        for (OpenLibrarySearchResponse.Doc doc : response.docs()) {
            MediaItem item = new MediaItem();
            item.setTitle(doc.title());
            // Der Such-Endpoint liefert keine Beschreibung, nur der Detail-Call pro Treffer hätte eine.
            item.setDescription(null);
            item.setReleaseYear(doc.firstPublishYear());
            item.setMediaType(MediaType.BOOK);
            item.setCreator(doc.authorName() != null && !doc.authorName().isEmpty()
                    ? doc.authorName().getFirst() : null);
            item.setCoverUrl(doc.coverId() != null
                    ? "https://covers.openlibrary.org/b/id/" + doc.coverId() + "-M.jpg" : null);

            Source source = new Source();
            source.setExternalId(doc.key());
            source.setMediaItem(item);
            source.setSourceType(SourceType.OPENLIBRARY);
            item.getSources().add(source);

            result.add(item);
        }
        return result;
    }
}
