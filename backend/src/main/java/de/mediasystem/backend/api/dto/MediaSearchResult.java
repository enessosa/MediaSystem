package de.mediasystem.backend.api.dto;

import de.mediasystem.backend.model.MediaType;
import de.mediasystem.backend.model.SourceType;

public record MediaSearchResult(
        String title,
        String description,
        Integer releaseYear,
        MediaType mediaType,
        String creator,
        String coverUrl,
        SourceType sourceType,
        String externalId) { }
