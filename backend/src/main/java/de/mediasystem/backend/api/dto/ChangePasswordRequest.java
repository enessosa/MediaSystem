package de.mediasystem.backend.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Passwort ändern: das aktuelle Passwort wird zur Bestätigung mitgeschickt, damit niemand an einem
 * fremden, noch eingeloggten Rechner das Passwort ändern und den Account übernehmen kann.
 */
public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank String newPassword) { }
