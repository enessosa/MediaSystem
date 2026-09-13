package de.mediasystem.backend.provider;

import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.SourceType;
import de.mediasystem.backend.provider.tmdb.TmdbProvider;
import de.mediasystem.backend.provider.tmdb.TmdbSearchResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TmdbProviderTest {

    private final TmdbProvider tmdbProvider = new TmdbProvider(mock(RestClient.class));

    @Test
    void mapToMediaItems_mapsMovie() {
        TmdbSearchResponse.Result movie = new TmdbSearchResponse.Result(
                603L, "movie", "The Matrix", "A computer hacker learns...",
                "1999-03-30", "/abc123.jpg", null, null);
        TmdbSearchResponse response = new TmdbSearchResponse(List.of(movie));

        List<MediaItem> result = tmdbProvider.mapToMediaItems(response);

        assertThat(result).hasSize(1);
        MediaItem item = result.get(0);
        assertThat(item.getTitle()).isEqualTo("The Matrix");
        assertThat(item.getDescription()).isEqualTo("A computer hacker learns...");
        assertThat(item.getReleaseYear()).isEqualTo(1999);
        assertThat(item.getMediaType()).isEqualTo(MediaType.MOVIE);
        assertThat(item.getCoverUrl()).isEqualTo("https://image.tmdb.org/t/p/w500/abc123.jpg");

        assertThat(item.getSources()).hasSize(1);
        Source source = item.getSources().iterator().next();
        assertThat(source.getSourceType()).isEqualTo(SourceType.TMDB);
        assertThat(source.getExternalId()).isEqualTo("603");
    }

    @Test
    void mapToMediaItems_mapsSeries_usesNameAndFirstAirDate() {
        TmdbSearchResponse.Result series = new TmdbSearchResponse.Result(
                1396L, "tv", null, "A chemistry teacher...",
                null, "/xyz456.jpg", "Breaking Bad", "2008-01-20");
        TmdbSearchResponse response = new TmdbSearchResponse(List.of(series));

        List<MediaItem> result = tmdbProvider.mapToMediaItems(response);

        assertThat(result).hasSize(1);
        MediaItem item = result.get(0);
        assertThat(item.getTitle()).isEqualTo("Breaking Bad");
        assertThat(item.getReleaseYear()).isEqualTo(2008);
        assertThat(item.getMediaType()).isEqualTo(MediaType.SERIES);
    }

    @Test
    void mapToMediaItems_filtersOutPersonResults() {
        TmdbSearchResponse.Result person = new TmdbSearchResponse.Result(
                99L, "person", null, null, null, null, "Keanu Reeves", null);
        TmdbSearchResponse response = new TmdbSearchResponse(List.of(person));

        List<MediaItem> result = tmdbProvider.mapToMediaItems(response);

        assertThat(result).isEmpty();
    }

    @Test
    void mapToMediaItems_missingPosterAndReleaseDate_coverUrlAndReleaseYearAreNull() {
        TmdbSearchResponse.Result movie = new TmdbSearchResponse.Result(
                1L, "movie", "Unknown Movie", "", "", null, null, null);
        TmdbSearchResponse response = new TmdbSearchResponse(List.of(movie));

        List<MediaItem> result = tmdbProvider.mapToMediaItems(response);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCoverUrl()).isNull();
        assertThat(result.get(0).getReleaseYear()).isNull();
    }

    @Test
    void mapToMediaItems_nullResponse_returnsEmptyList() {
        List<MediaItem> result = tmdbProvider.mapToMediaItems(null);

        assertThat(result).isEmpty();
    }

    @Test
    void mapToMediaItems_noResults_returnsEmptyList() {
        TmdbSearchResponse response = new TmdbSearchResponse(List.of());

        List<MediaItem> result = tmdbProvider.mapToMediaItems(response);

        assertThat(result).isEmpty();
    }
}
