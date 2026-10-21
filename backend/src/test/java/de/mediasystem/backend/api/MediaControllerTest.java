package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.AddToListRequest;
import de.mediasystem.backend.api.dto.MediaSearchResult;
import de.mediasystem.backend.api.dto.UpdateUserEntryRequest;
import de.mediasystem.backend.api.dto.UserEntryResponse;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.SourceType;
import de.mediasystem.backend.model.Status;
import de.mediasystem.backend.service.MediaService;
import de.mediasystem.backend.service.UserService;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    @Mock
    private MediaService mediaService;

    @Mock
    private UserService userService;

    @InjectMocks
    private MediaController mediaController;

    @Test
    void searchResultReturnsOkWithServiceResults() {
        MediaSearchResult searchResult = new MediaSearchResult(
                "Naruto", "Ein Manga über einen Ninja.", 1999, MediaType.MANGA,
                "Masashi Kishimoto", "https://example.org/cover.jpg", SourceType.ANILIST, "1");
        when(mediaService.searchMedia("Naruto", null)).thenReturn(List.of(searchResult));

        ResponseEntity<@NonNull List<MediaSearchResult>> response = mediaController.searchResult("Naruto", null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(searchResult);
        verify(mediaService).searchMedia("Naruto", null);
    }

    @Test
    void searchResultWithMediaTypeFilterPassesFilterToService() {
        MediaSearchResult searchResult = new MediaSearchResult(
                "Zelda II", "", 1987, MediaType.GAME, null, null, SourceType.RAWG, "53205");
        List<MediaType> filter = List.of(MediaType.GAME);
        when(mediaService.searchMedia("Zelda", filter)).thenReturn(List.of(searchResult));

        ResponseEntity<@NonNull List<MediaSearchResult>> response = mediaController.searchResult("Zelda", filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(searchResult);
        verify(mediaService).searchMedia("Zelda", filter);
    }

    @Test
    void addToUserListValidRequestReturnsCreatedWithServiceResult() {
        AddToListRequest request = new AddToListRequest(
                "Naruto", "Ein Manga über einen Ninja.", 1999, MediaType.MANGA,
                "Masashi Kishimoto", "https://example.org/cover.jpg", SourceType.ANILIST, "1");
        UserEntryResponse entryResponse = new UserEntryResponse(
                1L, Status.PLANNED, null, null, Instant.now(),
                2L, "Naruto", "Ein Manga über einen Ninja.", 1999, MediaType.MANGA,
                "Masashi Kishimoto", "https://example.org/cover.jpg");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(userService.getUserIdByUsername("testuser")).thenReturn(1L);
        when(mediaService.addToUserList(request, 1L)).thenReturn(entryResponse);

        ResponseEntity<@NonNull UserEntryResponse> response =
                mediaController.addToUserList(request, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(entryResponse);
        verify(mediaService).addToUserList(request, 1L);
    }

    @Test
    void getUserEntriesValidRequestReturnsOkWithServiceResults() {
        UserEntryResponse entryResponse = new UserEntryResponse(
                1L, Status.PLANNED, null, null, Instant.now(),
                2L, "Naruto", "Ein Manga über einen Ninja.", 1999, MediaType.MANGA,
                "Masashi Kishimoto", "https://example.org/cover.jpg");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(userService.getUserIdByUsername("testuser")).thenReturn(1L);
        when(mediaService.getUserEntries(1L)).thenReturn(List.of(entryResponse));

        ResponseEntity<@NonNull List<UserEntryResponse>> response = mediaController.getUserEntries(authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(entryResponse);
        verify(mediaService).getUserEntries(1L);
    }

    @Test
    void getUserEntriesNoEntriesReturnsOkWithEmptyList() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(userService.getUserIdByUsername("testuser")).thenReturn(1L);
        when(mediaService.getUserEntries(1L)).thenReturn(List.of());

        ResponseEntity<@NonNull List<UserEntryResponse>> response = mediaController.getUserEntries(authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void updateUserEntryValidRequestReturnsOkWithServiceResult() {
        UpdateUserEntryRequest request = new UpdateUserEntryRequest(Status.WATCHING, 8, "Sehr gut bisher.");
        UserEntryResponse entryResponse = new UserEntryResponse(
                7L, Status.WATCHING, 8, "Sehr gut bisher.", Instant.now(),
                2L, "Naruto", "Ein Manga über einen Ninja.", 1999, MediaType.MANGA,
                "Masashi Kishimoto", "https://example.org/cover.jpg");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(userService.getUserIdByUsername("testuser")).thenReturn(1L);
        when(mediaService.updateUserEntry(7L, 1L, request)).thenReturn(entryResponse);

        ResponseEntity<@NonNull UserEntryResponse> response =
                mediaController.updateUserEntry(7L, request, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(entryResponse);
        verify(mediaService).updateUserEntry(7L, 1L, request);
    }

    @Test
    void deleteUserEntryValidRequestReturnsNoContent() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(userService.getUserIdByUsername("testuser")).thenReturn(1L);

        ResponseEntity<@NonNull Void> response = mediaController.deleteUserEntry(7L, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(mediaService).deleteUserEntry(7L, 1L);
    }

    @Test
    void getAvailableMediaTypesReturnsOkWithServiceResult() {
        when(mediaService.getAvailableMediaTypes()).thenReturn(Set.of(MediaType.ANIME, MediaType.MANGA));

        ResponseEntity<@NonNull Set<MediaType>> response = mediaController.getAvailableMediaTypes();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactlyInAnyOrder(MediaType.ANIME, MediaType.MANGA);
    }
}
