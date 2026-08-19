package de.mediasystem.backend.service;

import de.mediasystem.backend.api.dto.MediaSearchResult;
import de.mediasystem.backend.db.MediaItemRepository;
import de.mediasystem.backend.db.SourceRepository;
import de.mediasystem.backend.db.UserEntryRepository;
import de.mediasystem.backend.db.UserRepository;
import de.mediasystem.backend.model.MediaItem;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.Source;
import de.mediasystem.backend.model.SourceType;
import de.mediasystem.backend.model.Status;
import de.mediasystem.backend.model.UserEntry;
import de.mediasystem.backend.model.roles.User;
import de.mediasystem.backend.provider.Provider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private Provider provider;

    @Mock
    private MediaItemRepository mediaItemRepository;

    @Mock
    private SourceRepository sourceRepository;

    @Mock
    private UserEntryRepository userEntryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MediaService mediaService;

    @Test
    void searchMedia_mapsProviderResultsToSearchResults() {
        MediaItem item = new MediaItem();
        item.setTitle("Naruto");
        item.setDescription("Ein Manga über einen Ninja.");
        item.setReleaseYear(1999);
        item.setMediaType(MediaType.MANGA);
        item.setCreator("Masashi Kishimoto");
        item.setCoverUrl("https://example.org/cover.jpg");

        Source source = new Source();
        source.setSourceType(SourceType.ANILIST);
        source.setExternalId("1");
        source.setMediaItem(item);
        item.getSources().add(source);

        when(provider.searchMedia("Naruto")).thenReturn(List.of(item));

        List<MediaSearchResult> result = mediaService.searchMedia("Naruto");

        assertThat(result).hasSize(1);
        MediaSearchResult searchResult = result.get(0);
        assertThat(searchResult.title()).isEqualTo("Naruto");
        assertThat(searchResult.description()).isEqualTo("Ein Manga über einen Ninja.");
        assertThat(searchResult.releaseYear()).isEqualTo(1999);
        assertThat(searchResult.mediaType()).isEqualTo(MediaType.MANGA);
        assertThat(searchResult.creator()).isEqualTo("Masashi Kishimoto");
        assertThat(searchResult.coverUrl()).isEqualTo("https://example.org/cover.jpg");
        assertThat(searchResult.sourceType()).isEqualTo(SourceType.ANILIST);
        assertThat(searchResult.externalId()).isEqualTo("1");
    }

    @Test
    void searchMedia_noProviderResults_returnsEmptyList() {
        when(provider.searchMedia("nothing")).thenReturn(List.of());

        List<MediaSearchResult> result = mediaService.searchMedia("nothing");

        assertThat(result).isEmpty();
    }

    @Test
    void addToUserList_newExternalMedia_createsMediaItemAndUserEntry() {
        MediaSearchResult data = new MediaSearchResult(
                "Naruto",
                "Ein Manga über einen Ninja.",
                1999,
                MediaType.MANGA,
                "Masashi Kishimoto",
                "https://example.org/cover.jpg",
                SourceType.ANILIST,
                "1"
        );
        Long userId = 1L;
        User testUser = new User();
        testUser.setUsername("testuser");

        when(sourceRepository.findBySourceTypeAndExternalId(SourceType.ANILIST, "1"))
                .thenReturn(Optional.empty());
        when(mediaItemRepository.save(any(MediaItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userEntryRepository.existsByUserIdAndMediaItemId(any(), any()))
                .thenReturn(false);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser));
        ArgumentCaptor<UserEntry> savedEntry = ArgumentCaptor.forClass(UserEntry.class);
        when(userEntryRepository.save(savedEntry.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mediaService.addToUserList(data, userId);

        UserEntry result = savedEntry.getValue();
        assertThat(result.getStatus()).isEqualTo(Status.PLANNED);
        assertThat(result.getMediaItem().getTitle()).isEqualTo("Naruto");
        assertThat(result.getMediaItem().getDescription()).isEqualTo("Ein Manga über einen Ninja.");
        assertThat(result.getMediaItem().getReleaseYear()).isEqualTo(1999);
        assertThat(result.getMediaItem().getMediaType()).isEqualTo(MediaType.MANGA);
        assertThat(result.getMediaItem().getCreator()).isEqualTo("Masashi Kishimoto");
        assertThat(result.getMediaItem().getCoverUrl()).isEqualTo("https://example.org/cover.jpg");
    }
}