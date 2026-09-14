package de.mediasystem.backend.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCodewordRequest(@NotBlank String codeword) { }
