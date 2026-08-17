package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.MediaSearchResult;
import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.SourceType;
import de.mediasystem.backend.service.MediaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    @Mock
    private MediaService mediaService;

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
}