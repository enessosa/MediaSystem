package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.MediaSearchResult;
import de.mediasystem.backend.service.MediaService;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/media/search")
    public ResponseEntity<@NonNull List<MediaSearchResult>> searchResult(@RequestParam String q) {
        return ResponseEntity.ok(mediaService.searchMedia(q));
    }
}
