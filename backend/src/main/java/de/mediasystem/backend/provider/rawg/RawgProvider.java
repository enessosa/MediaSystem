package de.mediasystem.backend.provider.rawg;

import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.SourceType;
import de.mediasystem.backend.provider.Provider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RawgProvider implements Provider {

    private final String apiKey;
    private final RestClient restClient;

    public RawgProvider(@Qualifier("rawgRestClient") RestClient restClient,
                         @Value("${rawg.api-key}") String apiKey) {
        this.restClient = restClient;
        this.apiKey = apiKey;
    }

    @Override
    public List<MediaItem> searchMedia(String search) {
        RawgSearchResult response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/games")
                        .queryParam("search", search)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .body(RawgSearchResult.class);
        return mapToMediaItems(response);
    }

    @Override
    public MediaItem fetchById(String id) {
        try {
            RawgGameDetail detail = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/games/{id}")
                            .queryParam("key", apiKey)
                            .build(id))
                    .retrieve()
                    .body(RawgGameDetail.class);
            return mapDetail(detail);
        } catch (HttpClientErrorException.NotFound notFound) {
            // RAWG antwortet bei unbekannter ID mit echtem HTTP 404.
            return null;
        }
    }

    /**
     * map method für den Detail-Call.
     * @param detail die rohe Antwort von /games/{id}
     * @return das gemappte MediaItem, oder null falls detail null ist
     */
    public MediaItem mapDetail(RawgGameDetail detail) {
        if (detail == null) {
            return null;
        }

        MediaItem item = new MediaItem();
        item.setTitle(detail.name());
        item.setDescription(detail.descriptionRaw());
        item.setReleaseYear(extractYear(detail.released()));
        item.setMediaType(MediaType.GAME);
        item.setCoverUrl(detail.backgroundImage());

        Source source = new Source();
        source.setExternalId(detail.id().toString());
        source.setMediaItem(item);
        source.setSourceType(SourceType.RAWG);
        item.getSources().add(source);

        return item;
    }

    @Override
    public Set<MediaType> supportedMediaTypes() {
        return Set.of(MediaType.GAME);
    }

    private Integer extractYear(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }
        return Integer.parseInt(date.substring(0, 4));
    }

    /**
     * map method.
     * @param response the raw response
     * @return the list of Mediaitems
     */
    public List<MediaItem> mapToMediaItems(RawgSearchResult response) {

        List<MediaItem> result = new ArrayList<>();

        if (response == null) {
            return result;
        }

        for (RawgSearchResult.Result r : response.results()) {
            MediaItem item = new MediaItem();
            item.setTitle(r.name());
            // RAWG liefert im Such-Endpoint keine Beschreibung, nur der Detail-Call pro Treffer hätte eine
            // (ähnliche Lücke wie fehlender creator bei TMDB).
            item.setDescription(null);
            item.setReleaseYear(extractYear(r.released()));
            item.setMediaType(MediaType.GAME);
            item.setCoverUrl(r.backgroundImage());

            Source source = new Source();
            source.setExternalId(r.id().toString());
            source.setMediaItem(item);
            source.setSourceType(SourceType.RAWG);
            item.getSources().add(source);

            result.add(item);
        }
        return result;
    }

    @Override
    public SourceType supportedSourceType() {
        return SourceType.RAWG;
    }
}
