package de.mediasystem.backend.provider.tmdb;

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
import java.util.Set;

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
        try {
            TmdbDetailResponse movie = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/movie/{id}").build(id))
                    .retrieve()
                    .body(TmdbDetailResponse.class);
            return buildMediaItem(movie.id(), movie.title(), movie.overview(),
                    movie.releaseDate(), movie.posterPath(), true);
        } catch (HttpClientErrorException.NotFound movieNotFound) {
            try {
                TmdbDetailResponse series = restClient.get()
                        .uri(uriBuilder -> uriBuilder.path("/tv/{id}").build(id))
                        .retrieve()
                        .body(TmdbDetailResponse.class);
                return buildMediaItem(series.id(), series.name(), series.overview(),
                        series.firstAirDate(), series.posterPath(), false);
            } catch (HttpClientErrorException.NotFound seriesNotFound) {
                return null;
            }
        }
    }

    @Override
    public Set<MediaType> supportedMediaTypes() {
        return Set.of(MediaType.MOVIE, MediaType.SERIES);
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
    public List<MediaItem> mapToMediaItems(TmdbSearchResponse response) {

        List<MediaItem> result = new ArrayList<>();

        if (response == null) {
            return result;
        }

        for (TmdbSearchResponse.Result r : response.results()) {
            if ("person".equals(r.mediaType())) {
                continue;
            }

            boolean isMovie = "movie".equals(r.mediaType());
            result.add(buildMediaItem(r.id(), isMovie ? r.title() : r.name(), r.overview(),
                    isMovie ? r.releaseDate() : r.firstAirDate(), r.posterPath(), isMovie));
        }
        return result;
    }

    /**
     * baut ein MediaItem aus den TMDB-Feldern, die Such- und Detail-Antwort gemeinsam haben -
     * genutzt sowohl von mapToMediaItems (mehrere Treffer) als auch von fetchById (ein Treffer).
     * @param id die TMDB-ID
     * @param title Titel (Movie: "title", Serie: "name")
     * @param overview Beschreibung
     * @param date Datum (Movie: "release_date", Serie: "first_air_date")
     * @param posterPath relativer Pfad zum Cover
     * @param isMovie true für Movie, false für Serie
     * @return das gemappte MediaItem
     */
    public MediaItem buildMediaItem(Long id, String title, String overview, String date,
                                      String posterPath, boolean isMovie) {
        MediaItem item = new MediaItem();
        item.setTitle(title);
        item.setDescription(overview);
        item.setReleaseYear(extractYear(date));
        item.setMediaType(isMovie ? MediaType.MOVIE : MediaType.SERIES);
        item.setCoverUrl(posterPath != null ? "https://image.tmdb.org/t/p/w500" + posterPath : null);

        Source source = new Source();
        source.setExternalId(id.toString());
        source.setMediaItem(item);
        source.setSourceType(SourceType.TMDB);
        item.getSources().add(source);
        return item;
    }

    @Override
    public SourceType supportedSourceType() {
        return SourceType.TMDB;
    }
}
