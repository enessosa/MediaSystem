package de.mediasystem.backend.api;

import de.mediasystem.backend.api.dto.MediaSearchResult;
import de.mediasystem.backend.api.dto.UpdateUserEntryRequest;
import de.mediasystem.backend.api.dto.UserEntryResponse;
import de.mediasystem.backend.service.MediaService;
import de.mediasystem.backend.service.UserService;
import jakarta.validation.Valid;
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


    /**
     * Post method for the entry response.
     * @param result the search result
     * @param authentication user infos without leaking hash
     * @return the response Entity
     */
    @PostMapping("/media/entries")
    public ResponseEntity<@NonNull UserEntryResponse> addToUserList(@RequestBody MediaSearchResult result,
                                                                    Authentication authentication) {
        String username = authentication.getName();
        Long userId = userService.getUserIdByUsername(username);
        UserEntryResponse userEntryResponse = mediaService.addToUserList(result, userId);

        return ResponseEntity.status(201).body(userEntryResponse);
    }

    /**
     * ist die REST methode die verhindern soll, dass die liste lokal beim client browser gespeicehrt werden muss.
     * @param authentication auth
     * @return liste der responses.
     */
    @GetMapping("/media/entries")
    public ResponseEntity<@NonNull List<UserEntryResponse>> getUserEntries(Authentication authentication) {

        String username = authentication.getName();
        Long userId = userService.getUserIdByUsername(username);
        List<UserEntryResponse> responses = mediaService.getUserEntries(userId);

        return ResponseEntity.status(200).body(responses);
    }

    /**
     * diese methode ist dafür da, user entries zu updaten.
     * @param id UserEntry id
     * @param request input der neuen daten
     * @param authentication auth
     * @return response
     */
    @PutMapping("/media/entries/{id}")
    public ResponseEntity<@NonNull UserEntryResponse> updateUserEntry(@PathVariable Long id,
                                                                      @RequestBody @Valid UpdateUserEntryRequest request,
                                                                      Authentication authentication) {
        String username = authentication.getName();
        Long userId = userService.getUserIdByUsername(username);
        UserEntryResponse response = mediaService.updateUserEntry(id, userId, request);

        return ResponseEntity.ok(response);
    }
}
