package de.mediasystem.backend.provider;

import de.mediasystem.backend.model.MediaItem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class TmdbProvider implements Provider {

    private final RestClient restClient;

    public TmdbProvider(@Qualifier("tmdbRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<MediaItem> searchMedia(String search) {
        TmdbSearchResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search/multi").queryParam("query", search)
                        .build())
                .retrieve()
                .body(TmdbSearchResponse.class);
        return mapToMediaItems(response);
    }

    @Override
    public MediaItem fetchById(String id) {
        return null;
    }

}
