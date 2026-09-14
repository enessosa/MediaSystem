package de.mediasystem.backend.api.dto;

import de.mediasystem.backend.model.Status;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateUserEntryRequest(
        @NotNull Status status,
        @Min(1) @Max(10) Integer rating,
        String note
) { }
