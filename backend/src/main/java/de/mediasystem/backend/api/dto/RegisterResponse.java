package de.mediasystem.backend.api.dto;

import java.time.Instant;

public record RegisterResponse(Long id,
                                String username,
                                String email,
                                Instant createdAt
) { }
