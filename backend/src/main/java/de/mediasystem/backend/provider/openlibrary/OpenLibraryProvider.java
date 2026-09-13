package de.mediasystem.backend.provider.openlibrary;

import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.SourceType;
import de.mediasystem.backend.provider.Provider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        try {
            OpenLibraryWorkDetail detail = restClient.get()
                    .uri(id + ".json")
                    .retrieve()
                    .body(OpenLibraryWorkDetail.class);
            return mapDetail(detail);
        } catch (HttpClientErrorException.NotFound notFound) {
            // OpenLibrary antwortet bei unbekanntem Key mit echtem HTTP 404.
            return null;
        }
    }

    /**
     * map method für den Detail-Call.
     * @param detail die rohe Antwort von /works/{key}.json
     * @return das gemappte MediaItem, oder null falls detail null ist
     */
    public MediaItem mapDetail(OpenLibraryWorkDetail detail) {
        if (detail == null) {
            return null;
        }

        MediaItem item = new MediaItem();
        item.setTitle(detail.title());
        item.setDescription(extractDescription(detail.description()));
        // Erscheinungsjahr gibt's bei OpenLibrary nur auf Editions-Ebene, nicht auf dem Work selbst.
        item.setReleaseYear(null);
        item.setMediaType(MediaType.BOOK);
        // Autor-Namen brauchen einen zusätzlichen Call gegen /authors/{id}.json (Work liefert nur Autor-Keys) -
        // bewusst ausgelassen, um die Detail-Ansicht nicht mit einem zweiten externen Call zu verlangsamen.
        item.setCreator(null);
        item.setCoverUrl(detail.covers() != null && !detail.covers().isEmpty()
                ? "https://covers.openlibrary.org/b/id/" + detail.covers().getFirst() + "-M.jpg" : null);

        Source source = new Source();
        source.setExternalId(detail.key());
        source.setMediaItem(item);
        source.setSourceType(SourceType.OPENLIBRARY);
        item.getSources().add(source);

        return item;
    }

    private String extractDescription(Object description) {
        if (description instanceof String text) {
            return text;
        }
        if (description instanceof Map<?, ?> map && map.get("value") instanceof String text) {
            return text;
        }
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


    @Override
    public SourceType supportedSourceType() {
        return SourceType.OPENLIBRARY;
    }
}
