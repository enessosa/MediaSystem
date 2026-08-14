package de.mediasystem.backend.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Diese Klasse gibt die benötigte Datenstruktur vor.
public record RegisterRequest(
        @NotBlank @Size(min = 4, max = 15) String username,
        @NotBlank @Email String email,
        @NotBlank String password,
        String codeword) { }

