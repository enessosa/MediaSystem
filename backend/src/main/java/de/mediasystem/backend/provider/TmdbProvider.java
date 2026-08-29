package de.mediasystem.backend.provider;

import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.SourceType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
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

    private Integer extractYear(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }
        return Integer.parseInt(date.substring(0, 4));
    }

    public List<MediaItem> mapToMediaItems(TmdbSearchResponse response) {

        List<MediaItem> result = new ArrayList<>();

        if (response == null) {
            return result;
        }

        for (TmdbSearchResponse.Result r : response.results()) {
            if ("person".equals(r.mediaType())) {
                continue;
            }

            MediaItem item = new MediaItem();
            boolean isMovie = "movie".equals(r.mediaType());

            item.setTitle(isMovie ? r.title() : r.name());
            item.setDescription(r.overview());
            item.setReleaseYear(extractYear(isMovie ? r.releaseDate() : r.firstAirDate()));
            item.setMediaType(isMovie ? MediaType.MOVIE : MediaType.SERIES);
            item.setCoverUrl(r.posterPath() != null ? "https://image.tmdb.org/t/p/w500" + r.posterPath() : null);

            Source source = new Source();
            source.setExternalId(r.id().toString());
            source.setMediaItem(item);
            source.setSourceType(SourceType.TMDB);
            item.getSources().add(source);

            result.add(item);
        }
        return result;
    }
}
