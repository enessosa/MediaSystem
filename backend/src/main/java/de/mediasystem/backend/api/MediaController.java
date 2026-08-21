package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.MediaSearchResult;
import de.mediasystem.backend.api.dto.UserEntryResponse;
import de.mediasystem.backend.service.MediaService;
import de.mediasystem.backend.service.UserService;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MediaController {

    private final MediaService mediaService;
    private final UserService userService;

    public MediaController(MediaService mediaService, UserService userService) {
        this.mediaService = mediaService;
        this.userService = userService;
    }

    @GetMapping("/media/search")
    public ResponseEntity<@NonNull List<MediaSearchResult>> searchResult(@RequestParam String q) {
        return ResponseEntity.ok(mediaService.searchMedia(q));
    }


    @PostMapping("/media/entries")
    public ResponseEntity<@NonNull UserEntryResponse> addToUserList(@RequestBody MediaSearchResult result, Authentication authentication) {
        String username = authentication.getName();
        Long userId = userService.getUserIdByUsername(username);
        UserEntryResponse userEntryResponse = mediaService.addToUserList(result, userId);

        return ResponseEntity.status(201).body(userEntryResponse);
    }
}
