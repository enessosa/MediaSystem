package de.mediasystem.backend.provider;

import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.SourceType;
import de.mediasystem.backend.provider.AniListSearchResponse.CoverImage;
import de.mediasystem.backend.provider.AniListSearchResponse.Data;
import de.mediasystem.backend.provider.AniListSearchResponse.Media;
import de.mediasystem.backend.provider.AniListSearchResponse.Page;
import de.mediasystem.backend.provider.AniListSearchResponse.Staff;
import de.mediasystem.backend.provider.AniListSearchResponse.StaffEdge;
import de.mediasystem.backend.provider.AniListSearchResponse.StaffName;
import de.mediasystem.backend.provider.AniListSearchResponse.StaffNode;
import de.mediasystem.backend.provider.AniListSearchResponse.StartDate;
import de.mediasystem.backend.provider.AniListSearchResponse.Title;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AniListProviderTest {

    private final AniListProvider aniListProvider = new AniListProvider();

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
}