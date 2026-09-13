package de.mediasystem.backend.provider;

import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.SourceType;
import de.mediasystem.backend.provider.anilist.AniListProvider;
import de.mediasystem.backend.provider.anilist.AniListSearchResponse;
import de.mediasystem.backend.provider.anilist.AniListSearchResponse.CoverImage;
import de.mediasystem.backend.provider.anilist.AniListSearchResponse.Data;
import de.mediasystem.backend.provider.anilist.AniListSearchResponse.Media;
import de.mediasystem.backend.provider.anilist.AniListSearchResponse.Page;
import de.mediasystem.backend.provider.anilist.AniListSearchResponse.Staff;
import de.mediasystem.backend.provider.anilist.AniListSearchResponse.StaffEdge;
import de.mediasystem.backend.provider.anilist.AniListSearchResponse.StaffName;
import de.mediasystem.backend.provider.anilist.AniListSearchResponse.StaffNode;
import de.mediasystem.backend.provider.anilist.AniListSearchResponse.StartDate;
import de.mediasystem.backend.provider.anilist.AniListSearchResponse.Title;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AniListProviderTest {

    private final AniListProvider aniListProvider = new AniListProvider(mock(RestClient.class));

    @Test
    void mapToMediaItems_mapsSingleMangaWithCreator() {
        Media media = new Media(
                "MANGA",
                "Ein Manga über einen Ninja.",
                1L,
                new Title("NARUTO", "Naruto"),
                new StartDate(1999),
                new CoverImage("https://example.org/cover.jpg"),
                new Staff(List.of(
                        new StaffEdge("Story & Art", new StaffNode(new StaffName("Masashi Kishimoto")))
                ))
        );
        AniListSearchResponse response = new AniListSearchResponse(new Data(new Page(List.of(media))));

        List<MediaItem> result = aniListProvider.mapToMediaItems(response);

        assertThat(result).hasSize(1);
        MediaItem item = result.get(0);
        assertThat(item.getTitle()).isEqualTo("NARUTO");
        assertThat(item.getDescription()).isEqualTo("Ein Manga über einen Ninja.");
        assertThat(item.getReleaseYear()).isEqualTo(1999);
        assertThat(item.getMediaType()).isEqualTo(MediaType.MANGA);
        assertThat(item.getCreator()).isEqualTo("Masashi Kishimoto");
        assertThat(item.getCoverUrl()).isEqualTo("https://example.org/cover.jpg");

        assertThat(item.getSources()).hasSize(1);
        Source source = item.getSources().iterator().next();
        assertThat(source.getSourceType()).isEqualTo(SourceType.ANILIST);
        assertThat(source.getExternalId()).isEqualTo("1");
    }

    @Test
    void mapToMediaItems_mapsAnimeWithoutStaff_creatorIsNull() {
        Media media = new Media(
                "ANIME",
                "Ein Anime ohne Staff-Angabe.",
                2L,
                new Title("SOME ANIME", "Some Anime"),
                new StartDate(2020),
                new CoverImage("https://example.org/anime-cover.jpg"),
                new Staff(List.of())
        );
        AniListSearchResponse response = new AniListSearchResponse(new Data(new Page(List.of(media))));

        List<MediaItem> result = aniListProvider.mapToMediaItems(response);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCreator()).isNull();
    }

    @Test
    void mapMedia_mapsSingleAnimeWithCreator() {
        Media media = new Media(
                "ANIME",
                "Ein Anime über einen Ninja.",
                1L,
                new Title("NARUTO", "Naruto"),
                new StartDate(1999),
                new CoverImage("https://example.org/cover.jpg"),
                new Staff(List.of(
                        new StaffEdge("Story & Art", new StaffNode(new StaffName("Masashi Kishimoto")))
                ))
        );

        MediaItem item = aniListProvider.mapMedia(media);

        assertThat(item.getTitle()).isEqualTo("NARUTO");
        assertThat(item.getDescription()).isEqualTo("Ein Anime über einen Ninja.");
        assertThat(item.getReleaseYear()).isEqualTo(1999);
        assertThat(item.getMediaType()).isEqualTo(MediaType.ANIME);
        assertThat(item.getCreator()).isEqualTo("Masashi Kishimoto");
        assertThat(item.getCoverUrl()).isEqualTo("https://example.org/cover.jpg");

        assertThat(item.getSources()).hasSize(1);
        Source source = item.getSources().iterator().next();
        assertThat(source.getSourceType()).isEqualTo(SourceType.ANILIST);
        assertThat(source.getExternalId()).isEqualTo("1");
    }

    @Test
    void mapMedia_usesEnglishTitle_whenRomajiIsNull() {
        Media media = new Media(
                "MANGA",
                "Beschreibung.",
                3L,
                new Title(null, "English Title"),
                new StartDate(2010),
                new CoverImage("https://example.org/cover2.jpg"),
                new Staff(List.of())
        );

        MediaItem item = aniListProvider.mapMedia(media);

        assertThat(item.getTitle()).isEqualTo("English Title");
    }
}