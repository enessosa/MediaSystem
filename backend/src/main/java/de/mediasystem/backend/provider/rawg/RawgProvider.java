package de.mediasystem.backend.provider.rawg;

import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.SourceType;
import de.mediasystem.backend.provider.Provider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

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
        return null;
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
}
