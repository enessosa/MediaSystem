package de.mediasystem.backend.service;

import de.mediasystem.backend.api.dto.AddToListRequest;
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
import de.mediasystem.backend.service.exception.MediaNotFoundException;
import de.mediasystem.backend.service.exception.MediaTypeUnavailableException;
import de.mediasystem.backend.service.exception.UserEntryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
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
        // Mocks führen default-Methoden nicht aus - isAvailable() wäre sonst false.
        // lenient, weil nicht jeder Test sucht.
        lenient().when(provider.isAvailable()).thenReturn(true);
        mediaService = new MediaService(List.of(provider), mediaItemRepository, sourceRepository,
                userEntryRepository, userRepository);
    }

    @Test
    void searchMediaMapsProviderResultsToSearchResults() {
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

        List<MediaSearchResult> result = mediaService.searchMedia("Naruto", null);

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
    void searchMediaNoProviderResultsReturnsEmptyList() {
        when(provider.searchMedia("nothing")).thenReturn(List.of());

        List<MediaSearchResult> result = mediaService.searchMedia("nothing", null);

        assertThat(result).isEmpty();
    }

    @Test
    void searchMediaWithMediaTypeFilterOnlyQueriesMatchingProviders() {
        Provider animeProvider = mock(Provider.class);
        when(animeProvider.isAvailable()).thenReturn(true);
        Provider gameProvider = mock(Provider.class);
        when(gameProvider.isAvailable()).thenReturn(true);
        when(animeProvider.supportedMediaTypes()).thenReturn(Set.of(MediaType.ANIME, MediaType.MANGA));
        when(gameProvider.supportedMediaTypes()).thenReturn(Set.of(MediaType.GAME));
        when(gameProvider.searchMedia("Zelda")).thenReturn(List.of());
        MediaService service = new MediaService(List.of(animeProvider, gameProvider), mediaItemRepository,
                sourceRepository, userEntryRepository, userRepository);

        service.searchMedia("Zelda", List.of(MediaType.GAME));

        verify(gameProvider).searchMedia("Zelda");
        verify(animeProvider, never()).searchMedia(any());
    }

    @Test
    void searchMediaProviderReturnsMultipleTypesFiltersDownToRequestedType() {
        // AniList liefert Anime UND Manga in einer Antwort (bewusste Design-Entscheidung, siehe AniListProvider) -
        // der Filter muss trotzdem nur den angeforderten Typ durchlassen.
        Provider aniListProvider = mock(Provider.class);
        when(aniListProvider.isAvailable()).thenReturn(true);
        when(aniListProvider.supportedMediaTypes()).thenReturn(Set.of(MediaType.ANIME, MediaType.MANGA));

        MediaItem anime = new MediaItem();
        anime.setTitle("Naruto");
        anime.setMediaType(MediaType.ANIME);
        Source animeSource = new Source();
        animeSource.setSourceType(SourceType.ANILIST);
        animeSource.setExternalId("1");
        animeSource.setMediaItem(anime);
        anime.getSources().add(animeSource);

        MediaItem manga = new MediaItem();
        manga.setTitle("Naruto");
        manga.setMediaType(MediaType.MANGA);
        Source mangaSource = new Source();
        mangaSource.setSourceType(SourceType.ANILIST);
        mangaSource.setExternalId("2");
        mangaSource.setMediaItem(manga);
        manga.getSources().add(mangaSource);

        when(aniListProvider.searchMedia("Naruto")).thenReturn(List.of(anime, manga));
        MediaService service = new MediaService(List.of(aniListProvider), mediaItemRepository,
                sourceRepository, userEntryRepository, userRepository);

        List<MediaSearchResult> result = service.searchMedia("Naruto", List.of(MediaType.ANIME));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().mediaType()).isEqualTo(MediaType.ANIME);
    }

    @Test
    void searchMediaDifferentMultiTypeProviderFiltersDownToRequestedType() {
        // Gleiches Prinzip wie bei AniList, aber mit einem anderen Mehrfach-Typ-Provider (TMDB: MOVIE+SERIES),
        // um zu belegen, dass der Filter generisch für jeden Provider gilt, nicht AniList-spezifisch ist.
        Provider tmdbProvider = mock(Provider.class);
        when(tmdbProvider.isAvailable()).thenReturn(true);
        when(tmdbProvider.supportedMediaTypes()).thenReturn(Set.of(MediaType.MOVIE, MediaType.SERIES));

        MediaItem movie = new MediaItem();
        movie.setTitle("Naruto: The Last");
        movie.setMediaType(MediaType.MOVIE);
        Source movieSource = new Source();
        movieSource.setSourceType(SourceType.TMDB);
        movieSource.setExternalId("1");
        movieSource.setMediaItem(movie);
        movie.getSources().add(movieSource);

        MediaItem series = new MediaItem();
        series.setTitle("Naruto Shippuden");
        series.setMediaType(MediaType.SERIES);
        Source seriesSource = new Source();
        seriesSource.setSourceType(SourceType.TMDB);
        seriesSource.setExternalId("2");
        seriesSource.setMediaItem(series);
        series.getSources().add(seriesSource);

        when(tmdbProvider.searchMedia("Naruto")).thenReturn(List.of(movie, series));
        MediaService service = new MediaService(List.of(tmdbProvider), mediaItemRepository,
                sourceRepository, userEntryRepository, userRepository);

        List<MediaSearchResult> result = service.searchMedia("Naruto", List.of(MediaType.SERIES));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().mediaType()).isEqualTo(MediaType.SERIES);
    }

    @Test
    void searchMediaEmptyMediaTypeFilterQueriesAllProviders() {
        Provider animeProvider = mock(Provider.class);
        when(animeProvider.isAvailable()).thenReturn(true);
        Provider gameProvider = mock(Provider.class);
        when(gameProvider.isAvailable()).thenReturn(true);
        when(animeProvider.searchMedia("Zelda")).thenReturn(List.of());
        when(gameProvider.searchMedia("Zelda")).thenReturn(List.of());
        MediaService service = new MediaService(List.of(animeProvider, gameProvider), mediaItemRepository,
                sourceRepository, userEntryRepository, userRepository);

        service.searchMedia("Zelda", List.of());

        verify(animeProvider).searchMedia("Zelda");
        verify(gameProvider).searchMedia("Zelda");
    }

    @Test
    void addToUserListNewExternalMediaCreatesMediaItemAndUserEntry() {
        AddToListRequest data = new AddToListRequest(
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
    void addToUserListManualSourceNeverLooksUpExistingSourceAndAlwaysCreatesNewMediaItem() {
        AddToListRequest first = new AddToListRequest(
                "Herr der Ringe", "", null, MediaType.BOOK, null, null, SourceType.MANUAL, null);
        AddToListRequest second = new AddToListRequest(
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
    void getUserEntriesReturnsMappedResponsesForUser() {
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
    void getUserEntriesNoEntriesForUserReturnsEmptyList() {
        Long userId = 1L;
        when(userEntryRepository.findByUserId(userId)).thenReturn(List.of());

        List<UserEntryResponse> result = mediaService.getUserEntries(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void updateUserEntryOwnEntryUpdatesStatusRatingAndNote() {
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
    void updateUserEntryEntryDoesNotExistThrowsUserEntryNotFoundException() {
        when(userEntryRepository.findById(999L)).thenReturn(Optional.empty());

        UpdateUserEntryRequest request = new UpdateUserEntryRequest(Status.WATCHING, null, null);

        assertThatThrownBy(() -> mediaService.updateUserEntry(999L, 1L, request))
                .isInstanceOf(UserEntryNotFoundException.class);
    }

    @Test
    void updateUserEntryEntryBelongsToOtherUserThrowsUserEntryNotFoundException() {
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
    void deleteUserEntryOwnEntryDeletesIt() {
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
    void deleteUserEntryEntryDoesNotExistThrowsUserEntryNotFoundException() {
        when(userEntryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mediaService.deleteUserEntry(999L, 1L))
                .isInstanceOf(UserEntryNotFoundException.class);
    }

    @Test
    void deleteUserEntryEntryBelongsToOtherUserThrowsUserEntryNotFoundException() {
        User otherUser = new User();
        otherUser.setId(2L);

        UserEntry entry = new UserEntry();
        entry.setId(7L);
        entry.setUser(otherUser);

        when(userEntryRepository.findById(7L)).thenReturn(Optional.of(entry));

        assertThatThrownBy(() -> mediaService.deleteUserEntry(7L, 1L))
                .isInstanceOf(UserEntryNotFoundException.class);
    }

    @Test
    void fetchMediaDetailMatchingProviderReturnsItemMapsToMediaSearchResult() {
        Provider rawgProvider = mock(Provider.class);
        when(rawgProvider.isAvailable()).thenReturn(true);
        when(rawgProvider.supportedSourceType()).thenReturn(SourceType.RAWG);

        MediaItem game = new MediaItem();
        game.setTitle("Grand Theft Auto V");
        game.setMediaType(MediaType.GAME);
        Source source = new Source();
        source.setSourceType(SourceType.RAWG);
        source.setExternalId("3498");
        source.setMediaItem(game);
        game.getSources().add(source);

        when(rawgProvider.fetchById("3498")).thenReturn(game);
        MediaService service = new MediaService(List.of(rawgProvider), mediaItemRepository,
                sourceRepository, userEntryRepository, userRepository);

        MediaSearchResult result = service.fetchMediaDetail(SourceType.RAWG, "3498");

        assertThat(result.title()).isEqualTo("Grand Theft Auto V");
        assertThat(result.sourceType()).isEqualTo(SourceType.RAWG);
        assertThat(result.externalId()).isEqualTo("3498");
    }

    @Test
    void fetchMediaDetailNoProviderForSourceTypeThrowsMediaNotFoundException() {
        Provider rawgProvider = mock(Provider.class);
        when(rawgProvider.isAvailable()).thenReturn(true);
        when(rawgProvider.supportedSourceType()).thenReturn(SourceType.RAWG);
        MediaService service = new MediaService(List.of(rawgProvider), mediaItemRepository,
                sourceRepository, userEntryRepository, userRepository);

        assertThatThrownBy(() -> service.fetchMediaDetail(SourceType.TMDB, "603"))
                .isInstanceOf(MediaNotFoundException.class);
    }

    @Test
    void fetchMediaDetailProviderDoesNotKnowExternalIdThrowsMediaNotFoundException() {
        Provider rawgProvider = mock(Provider.class);
        when(rawgProvider.isAvailable()).thenReturn(true);
        when(rawgProvider.supportedSourceType()).thenReturn(SourceType.RAWG);
        when(rawgProvider.fetchById("999")).thenReturn(null);
        MediaService service = new MediaService(List.of(rawgProvider), mediaItemRepository,
                sourceRepository, userEntryRepository, userRepository);

        assertThatThrownBy(() -> service.fetchMediaDetail(SourceType.RAWG, "999"))
                .isInstanceOf(MediaNotFoundException.class);
    }

    @Test
    void fetchMediaDetailNullSourceTypeThrowsMediaNotFoundException() {
        assertThatThrownBy(() -> mediaService.fetchMediaDetail(null, "123"))
                .isInstanceOf(MediaNotFoundException.class);
    }

    @Test
    void searchMediaSkipsUnavailableProvider() {
        Provider animeProvider = mock(Provider.class);
        Provider gameProvider = mock(Provider.class);
        when(animeProvider.isAvailable()).thenReturn(true);
        when(gameProvider.isAvailable()).thenReturn(false);
        when(animeProvider.searchMedia("Zelda")).thenReturn(List.of());
        MediaService service = new MediaService(List.of(animeProvider, gameProvider), mediaItemRepository,
                sourceRepository, userEntryRepository, userRepository);

        service.searchMedia("Zelda", null);

        verify(animeProvider).searchMedia("Zelda");
        verify(gameProvider, never()).searchMedia(any());
    }

    @Test
    void searchMediaFilterOnlyForUnavailableTypesThrowsMediaTypeUnavailableException() {
        Provider animeProvider = mock(Provider.class);
        Provider gameProvider = mock(Provider.class);
        when(animeProvider.isAvailable()).thenReturn(true);
        when(animeProvider.supportedMediaTypes()).thenReturn(Set.of(MediaType.ANIME, MediaType.MANGA));
        when(gameProvider.isAvailable()).thenReturn(false);
        MediaService service = new MediaService(List.of(animeProvider, gameProvider), mediaItemRepository,
                sourceRepository, userEntryRepository, userRepository);

        assertThatThrownBy(() -> service.searchMedia("Zelda", List.of(MediaType.GAME)))
                .isInstanceOf(MediaTypeUnavailableException.class);
        verify(gameProvider, never()).searchMedia(any());
        verify(animeProvider, never()).searchMedia(any());
    }

    @Test
    void searchMediaFilterWithAvailableAndUnavailableTypeSearchesOnlyAvailableProvider() {
        Provider animeProvider = mock(Provider.class);
        Provider gameProvider = mock(Provider.class);
        when(animeProvider.isAvailable()).thenReturn(true);
        when(animeProvider.supportedMediaTypes()).thenReturn(Set.of(MediaType.ANIME, MediaType.MANGA));
        when(animeProvider.searchMedia("Zelda")).thenReturn(List.of());
        when(gameProvider.isAvailable()).thenReturn(false);
        MediaService service = new MediaService(List.of(animeProvider, gameProvider), mediaItemRepository,
                sourceRepository, userEntryRepository, userRepository);

        service.searchMedia("Zelda", List.of(MediaType.ANIME, MediaType.GAME));

        verify(animeProvider).searchMedia("Zelda");
        verify(gameProvider, never()).searchMedia(any());
    }

    @Test
    void getAvailableMediaTypesContainsOnlyTypesOfAvailableProviders() {
        Provider animeProvider = mock(Provider.class);
        Provider gameProvider = mock(Provider.class);
        when(animeProvider.isAvailable()).thenReturn(true);
        when(animeProvider.supportedMediaTypes()).thenReturn(Set.of(MediaType.ANIME, MediaType.MANGA));
        when(gameProvider.isAvailable()).thenReturn(false);
        MediaService service = new MediaService(List.of(animeProvider, gameProvider), mediaItemRepository,
                sourceRepository, userEntryRepository, userRepository);

        assertThat(service.getAvailableMediaTypes()).containsExactlyInAnyOrder(MediaType.ANIME, MediaType.MANGA);
    }

    @Test
    void fetchMediaDetailUnavailableProviderThrowsMediaNotFoundException() {
        Provider rawgProvider = mock(Provider.class);
        when(rawgProvider.isAvailable()).thenReturn(false);
        MediaService service = new MediaService(List.of(rawgProvider), mediaItemRepository,
                sourceRepository, userEntryRepository, userRepository);

        assertThatThrownBy(() -> service.fetchMediaDetail(SourceType.RAWG, "3498"))
                .isInstanceOf(MediaNotFoundException.class);
        verify(rawgProvider, never()).fetchById(any());
    }
}
