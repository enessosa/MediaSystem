package de.mediasystem.backend.provider;

import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.SourceType;
import de.mediasystem.backend.provider.rawg.RawgGameDetail;
import de.mediasystem.backend.provider.rawg.RawgProvider;
import de.mediasystem.backend.provider.rawg.RawgSearchResult;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RawgProviderTest {

    private final RawgProvider rawgProvider = new RawgProvider(mock(RestClient.class), "test-key");

    @Test
    void mapToMediaItems_mapsSingleGame() {
        RawgSearchResult.Result game = new RawgSearchResult.Result(
                3498L, "Grand Theft Auto V", "2013-09-17", "https://example.org/gtav.jpg");
        RawgSearchResult response = new RawgSearchResult(List.of(game));

        List<MediaItem> result = rawgProvider.mapToMediaItems(response);

        assertThat(result).hasSize(1);
        MediaItem item = result.get(0);
        assertThat(item.getTitle()).isEqualTo("Grand Theft Auto V");
        assertThat(item.getDescription()).isNull();
        assertThat(item.getReleaseYear()).isEqualTo(2013);
        assertThat(item.getMediaType()).isEqualTo(MediaType.GAME);
        assertThat(item.getCoverUrl()).isEqualTo("https://example.org/gtav.jpg");

        assertThat(item.getSources()).hasSize(1);
        Source source = item.getSources().iterator().next();
        assertThat(source.getSourceType()).isEqualTo(SourceType.RAWG);
        assertThat(source.getExternalId()).isEqualTo("3498");
    }

    @Test
    void mapToMediaItems_missingReleasedDate_releaseYearIsNull() {
        RawgSearchResult.Result game = new RawgSearchResult.Result(
                1L, "Unbekanntes Spiel", null, null);
        RawgSearchResult response = new RawgSearchResult(List.of(game));

        List<MediaItem> result = rawgProvider.mapToMediaItems(response);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReleaseYear()).isNull();
        assertThat(result.get(0).getCoverUrl()).isNull();
    }

    @Test
    void mapToMediaItems_nullResponse_returnsEmptyList() {
        List<MediaItem> result = rawgProvider.mapToMediaItems(null);

        assertThat(result).isEmpty();
    }

    @Test
    void mapToMediaItems_noResults_returnsEmptyList() {
        RawgSearchResult response = new RawgSearchResult(List.of());

        List<MediaItem> result = rawgProvider.mapToMediaItems(response);

        assertThat(result).isEmpty();
    }

    @Test
    void mapDetail_mapsGameWithDescription() {
        RawgGameDetail detail = new RawgGameDetail(
                3498L, "Grand Theft Auto V", "2013-09-17",
                "https://example.org/gtav.jpg", "Open-World-Actionspiel...");

        MediaItem item = rawgProvider.mapDetail(detail);

        assertThat(item.getTitle()).isEqualTo("Grand Theft Auto V");
        assertThat(item.getDescription()).isEqualTo("Open-World-Actionspiel...");
        assertThat(item.getReleaseYear()).isEqualTo(2013);
        assertThat(item.getMediaType()).isEqualTo(MediaType.GAME);
        assertThat(item.getCoverUrl()).isEqualTo("https://example.org/gtav.jpg");

        assertThat(item.getSources()).hasSize(1);
        Source source = item.getSources().iterator().next();
        assertThat(source.getSourceType()).isEqualTo(SourceType.RAWG);
        assertThat(source.getExternalId()).isEqualTo("3498");
    }

    @Test
    void mapDetail_nullDetail_returnsNull() {
        MediaItem item = rawgProvider.mapDetail(null);

        assertThat(item).isNull();
    }
}
