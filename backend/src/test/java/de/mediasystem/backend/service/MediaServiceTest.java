package de.mediasystem.backend.service;

import de.mediasystem.backend.api.dto.MediaSearchResult;
import de.mediasystem.backend.api.dto.UpdateUserEntryRequest;
import de.mediasystem.backend.api.dto.UserEntryResponse;
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
import de.mediasystem.backend.service.exception.UserEntryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        mediaService = new MediaService(List.of(provider), mediaItemRepository, sourceRepository,
                userEntryRepository, userRepository);
    }

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
        MediaSearchResult searchResult = result.getFirst();
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

    @Test
    void addToUserList_manualSource_neverLooksUpExistingSourceAndAlwaysCreatesNewMediaItem() {
        MediaSearchResult first = new MediaSearchResult(
                "Herr der Ringe", "", null, MediaType.BOOK, null, null, SourceType.MANUAL, null);
        MediaSearchResult second = new MediaSearchResult(
                "Der Hobbit", "", null, MediaType.BOOK, null, null, SourceType.MANUAL, null);
        Long userId = 1L;
        User testUser = new User();
        testUser.setUsername("testuser");

        when(mediaItemRepository.save(any(MediaItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userEntryRepository.existsByUserIdAndMediaItemId(any(), any()))
                .thenReturn(false);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser));
        ArgumentCaptor<UserEntry> savedEntries = ArgumentCaptor.forClass(UserEntry.class);
        when(userEntryRepository.save(savedEntries.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mediaService.addToUserList(first, userId);
        mediaService.addToUserList(second, userId);

        // Ebene-1-Duplikat-Prüfung darf für MANUAL nie greifen (kein externes Original zum Abgleichen).
        verify(sourceRepository, never()).findBySourceTypeAndExternalId(any(), any());

        List<UserEntry> entries = savedEntries.getAllValues();
        assertThat(entries).hasSize(2);
        assertThat(entries.get(0).getMediaItem()).isNotSameAs(entries.get(1).getMediaItem());
        assertThat(entries.get(0).getMediaItem().getTitle()).isEqualTo("Herr der Ringe");
        assertThat(entries.get(1).getMediaItem().getTitle()).isEqualTo("Der Hobbit");
    }

    @Test
    void getUserEntries_returnsMappedResponsesForUser() {
        Long userId = 1L;

        MediaItem item = new MediaItem();
        item.setId(42L);
        item.setTitle("Naruto");
        item.setDescription("Ein Manga über einen Ninja.");
        item.setReleaseYear(1999);
        item.setMediaType(MediaType.MANGA);
        item.setCreator("Masashi Kishimoto");
        item.setCoverUrl("https://example.org/cover.jpg");

        UserEntry entry = new UserEntry();
        entry.setId(7L);
        entry.setMediaItem(item);
        entry.setStatus(Status.PLANNED);
        entry.setRating(null);
        entry.setNote(null);

        when(userEntryRepository.findByUserId(userId)).thenReturn(List.of(entry));

        List<UserEntryResponse> result = mediaService.getUserEntries(userId);

        assertThat(result).hasSize(1);
        UserEntryResponse response = result.getFirst();
        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.status()).isEqualTo(Status.PLANNED);
        assertThat(response.mediaItemid()).isEqualTo(42L);
        assertThat(response.title()).isEqualTo("Naruto");
        assertThat(response.description()).isEqualTo("Ein Manga über einen Ninja.");
        assertThat(response.releaseYear()).isEqualTo(1999);
        assertThat(response.mediaType()).isEqualTo(MediaType.MANGA);
        assertThat(response.creator()).isEqualTo("Masashi Kishimoto");
        assertThat(response.coverUrl()).isEqualTo("https://example.org/cover.jpg");
    }

    @Test
    void getUserEntries_noEntriesForUser_returnsEmptyList() {
        Long userId = 1L;
        when(userEntryRepository.findByUserId(userId)).thenReturn(List.of());

        List<UserEntryResponse> result = mediaService.getUserEntries(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void updateUserEntry_ownEntry_updatesStatusRatingAndNote() {
        Long userId = 1L;
        User owner = new User();
        owner.setId(userId);

        MediaItem item = new MediaItem();
        item.setId(42L);
        item.setTitle("Naruto");
        item.setMediaType(MediaType.MANGA);

        UserEntry entry = new UserEntry();
        entry.setId(7L);
        entry.setUser(owner);
        entry.setMediaItem(item);
        entry.setStatus(Status.PLANNED);

        UpdateUserEntryRequest request = new UpdateUserEntryRequest(Status.WATCHING, 8, "Sehr gut bisher.");

        when(userEntryRepository.findById(7L)).thenReturn(Optional.of(entry));
        when(userEntryRepository.save(entry)).thenReturn(entry);

        UserEntryResponse response = mediaService.updateUserEntry(7L, userId, request);

        assertThat(entry.getStatus()).isEqualTo(Status.WATCHING);
        assertThat(entry.getRating()).isEqualTo(8);
        assertThat(entry.getNote()).isEqualTo("Sehr gut bisher.");
        assertThat(response.status()).isEqualTo(Status.WATCHING);
        assertThat(response.rating()).isEqualTo(8);
        assertThat(response.note()).isEqualTo("Sehr gut bisher.");
    }

    @Test
    void updateUserEntry_entryDoesNotExist_throwsUserEntryNotFoundException() {
        when(userEntryRepository.findById(999L)).thenReturn(Optional.empty());

        UpdateUserEntryRequest request = new UpdateUserEntryRequest(Status.WATCHING, null, null);

        assertThatThrownBy(() -> mediaService.updateUserEntry(999L, 1L, request))
                .isInstanceOf(UserEntryNotFoundException.class);
    }

    @Test
    void updateUserEntry_entryBelongsToOtherUser_throwsUserEntryNotFoundException() {
        User otherUser = new User();
        otherUser.setId(2L);

        UserEntry entry = new UserEntry();
        entry.setId(7L);
        entry.setUser(otherUser);

        when(userEntryRepository.findById(7L)).thenReturn(Optional.of(entry));

        UpdateUserEntryRequest request = new UpdateUserEntryRequest(Status.WATCHING, null, null);

        assertThatThrownBy(() -> mediaService.updateUserEntry(7L, 1L, request))
                .isInstanceOf(UserEntryNotFoundException.class);
    }

    @Test
    void deleteUserEntry_ownEntry_deletesIt() {
        Long userId = 1L;
        User owner = new User();
        owner.setId(userId);

        UserEntry entry = new UserEntry();
        entry.setId(7L);
        entry.setUser(owner);

        when(userEntryRepository.findById(7L)).thenReturn(Optional.of(entry));

        mediaService.deleteUserEntry(7L, userId);

        verify(userEntryRepository).delete(entry);
    }

    @Test
    void deleteUserEntry_entryDoesNotExist_throwsUserEntryNotFoundException() {
        when(userEntryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mediaService.deleteUserEntry(999L, 1L))
                .isInstanceOf(UserEntryNotFoundException.class);
    }

    @Test
    void deleteUserEntry_entryBelongsToOtherUser_throwsUserEntryNotFoundException() {
        User otherUser = new User();
        otherUser.setId(2L);

        UserEntry entry = new UserEntry();
        entry.setId(7L);
        entry.setUser(otherUser);

        when(userEntryRepository.findById(7L)).thenReturn(Optional.of(entry));

        assertThatThrownBy(() -> mediaService.deleteUserEntry(7L, 1L))
                .isInstanceOf(UserEntryNotFoundException.class);
    }
}