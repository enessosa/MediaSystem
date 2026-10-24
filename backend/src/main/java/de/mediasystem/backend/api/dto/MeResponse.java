package de.mediasystem.backend.api.dto;

public record MeResponse(
        long id,
        String username,
        boolean admin
) { }
