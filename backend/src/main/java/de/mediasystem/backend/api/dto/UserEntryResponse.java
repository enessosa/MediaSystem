package de.mediasystem.backend.api.dto;

import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.Status;

import java.time.Instant;

public record UserEntryResponse(
        Long id,
        Status status,
        Integer rating,
        String note,
        Instant addedAt,
        // ab hier Media
        Long mediaItemid,
        String title,
        String description,
        Integer releaseYear,
        MediaType mediaType,
        String creator,
        String coverUrl
) { }
