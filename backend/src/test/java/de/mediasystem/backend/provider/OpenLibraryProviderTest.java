package de.mediasystem.backend.provider;

import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.SourceType;
import de.mediasystem.backend.provider.openlibrary.OpenLibraryProvider;
import de.mediasystem.backend.provider.openlibrary.OpenLibrarySearchResponse;
import de.mediasystem.backend.provider.openlibrary.OpenLibraryWorkDetail;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class OpenLibraryProviderTest {

    private final OpenLibraryProvider openLibraryProvider = new OpenLibraryProvider(mock(RestClient.class));

    @Test
    void mapToMediaItems_mapsSingleBookWithAuthorAndCover() {
        OpenLibrarySearchResponse.Doc doc = new OpenLibrarySearchResponse.Doc(
                "/works/OL45804W", "Der Hobbit", List.of("J.R.R. Tolkien"), 1937, 258027L);
        OpenLibrarySearchResponse response = new OpenLibrarySearchResponse(List.of(doc));

        List<MediaItem> result = openLibraryProvider.mapToMediaItems(response);

        assertThat(result).hasSize(1);
        MediaItem item = result.get(0);
        assertThat(item.getTitle()).isEqualTo("Der Hobbit");
        assertThat(item.getDescription()).isNull();
        assertThat(item.getReleaseYear()).isEqualTo(1937);
        assertThat(item.getMediaType()).isEqualTo(MediaType.BOOK);
        assertThat(item.getCreator()).isEqualTo("J.R.R. Tolkien");
        assertThat(item.getCoverUrl()).isEqualTo("https://covers.openlibrary.org/b/id/258027-M.jpg");

        assertThat(item.getSources()).hasSize(1);
        Source source = item.getSources().iterator().next();
        assertThat(source.getSourceType()).isEqualTo(SourceType.OPENLIBRARY);
        assertThat(source.getExternalId()).isEqualTo("/works/OL45804W");
    }

    @Test
    void mapToMediaItems_missingAuthorAndCover_creatorAndCoverUrlAreNull() {
        OpenLibrarySearchResponse.Doc doc = new OpenLibrarySearchResponse.Doc(
                "/works/OL1W", "Unbekanntes Buch", List.of(), 2000, null);
        OpenLibrarySearchResponse response = new OpenLibrarySearchResponse(List.of(doc));

        List<MediaItem> result = openLibraryProvider.mapToMediaItems(response);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCreator()).isNull();
        assertThat(result.get(0).getCoverUrl()).isNull();
    }

    @Test
    void mapToMediaItems_authorNameNull_creatorIsNull() {
        OpenLibrarySearchResponse.Doc doc = new OpenLibrarySearchResponse.Doc(
                "/works/OL2W", "Noch ein Buch", null, 1999, null);
        OpenLibrarySearchResponse response = new OpenLibrarySearchResponse(List.of(doc));

        List<MediaItem> result = openLibraryProvider.mapToMediaItems(response);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCreator()).isNull();
    }

    @Test
    void mapToMediaItems_nullResponse_returnsEmptyList() {
        List<MediaItem> result = openLibraryProvider.mapToMediaItems(null);

        assertThat(result).isEmpty();
    }

    @Test
    void mapToMediaItems_noResults_returnsEmptyList() {
        OpenLibrarySearchResponse response = new OpenLibrarySearchResponse(List.of());

        List<MediaItem> result = openLibraryProvider.mapToMediaItems(response);

        assertThat(result).isEmpty();
    }

    @Test
    void mapDetail_descriptionAsPlainString() {
        OpenLibraryWorkDetail detail = new OpenLibraryWorkDetail(
                "/works/OL45804W", "Der Hobbit", "Ein Fantasy-Roman.", List.of(258027L));

        MediaItem item = openLibraryProvider.mapDetail(detail);

        assertThat(item.getTitle()).isEqualTo("Der Hobbit");
        assertThat(item.getDescription()).isEqualTo("Ein Fantasy-Roman.");
        assertThat(item.getReleaseYear()).isNull();
        assertThat(item.getMediaType()).isEqualTo(MediaType.BOOK);
        assertThat(item.getCoverUrl()).isEqualTo("https://covers.openlibrary.org/b/id/258027-M.jpg");

        assertThat(item.getSources()).hasSize(1);
        Source source = item.getSources().iterator().next();
        assertThat(source.getSourceType()).isEqualTo(SourceType.OPENLIBRARY);
        assertThat(source.getExternalId()).isEqualTo("/works/OL45804W");
    }

    @Test
    void mapDetail_descriptionAsObject_extractsValue() {
        OpenLibraryWorkDetail detail = new OpenLibraryWorkDetail(
                "/works/OL1W", "Unbekanntes Buch",
                Map.of("type", "/type/text", "value", "Beschreibung als Objekt."), List.of());

        MediaItem item = openLibraryProvider.mapDetail(detail);

        assertThat(item.getDescription()).isEqualTo("Beschreibung als Objekt.");
        assertThat(item.getCoverUrl()).isNull();
    }

    @Test
    void mapDetail_noDescription_descriptionIsNull() {
        OpenLibraryWorkDetail detail = new OpenLibraryWorkDetail(
                "/works/OL2W", "Noch ein Buch", null, null);

        MediaItem item = openLibraryProvider.mapDetail(detail);

        assertThat(item.getDescription()).isNull();
        assertThat(item.getCoverUrl()).isNull();
    }

    @Test
    void mapDetail_nullDetail_returnsNull() {
        MediaItem item = openLibraryProvider.mapDetail(null);

        assertThat(item).isNull();
    }
}
