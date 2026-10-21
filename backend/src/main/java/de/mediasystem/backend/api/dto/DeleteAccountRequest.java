package de.mediasystem.backend.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Account löschen: Bestätigung per Passwort, aus demselben Grund wie bei ChangePasswordRequest.
 */
public record DeleteAccountRequest(@NotBlank String password) { }
