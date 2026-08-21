package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.MediaSearchResult;
import de.mediasystem.backend.api.dto.UserEntryResponse;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.SourceType;
import de.mediasystem.backend.model.Status;
import de.mediasystem.backend.service.MediaService;
import de.mediasystem.backend.service.UserService;
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
    void searchResult_returnsOkWithServiceResults() {
        MediaSearchResult searchResult = new MediaSearchResult(
                "Naruto", "Ein Manga über einen Ninja.", 1999, MediaType.MANGA,
                "Masashi Kishimoto", "https://example.org/cover.jpg", SourceType.ANILIST, "1");
        when(mediaService.searchMedia("Naruto")).thenReturn(List.of(searchResult));

        ResponseEntity<List<MediaSearchResult>> response = mediaController.searchResult("Naruto");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(searchResult);
        verify(mediaService).searchMedia("Naruto");
    }

    @Test
    void addToUserList_validRequest_returnsCreatedWithServiceResult() {
        MediaSearchResult searchResult = new MediaSearchResult(
                "Naruto", "Ein Manga über einen Ninja.", 1999, MediaType.MANGA,
                "Masashi Kishimoto", "https://example.org/cover.jpg", SourceType.ANILIST, "1");
        UserEntryResponse entryResponse = new UserEntryResponse(
                1L, Status.PLANNED, null, null, Instant.now(),
                2L, "Naruto", "Ein Manga über einen Ninja.", 1999, MediaType.MANGA,
                "Masashi Kishimoto", "https://example.org/cover.jpg");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(userService.getUserIdByUsername("testuser")).thenReturn(1L);
        when(mediaService.addToUserList(searchResult, 1L)).thenReturn(entryResponse);

        ResponseEntity<UserEntryResponse> response = mediaController.addToUserList(searchResult, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(entryResponse);
        verify(mediaService).addToUserList(searchResult, 1L);
    }
}