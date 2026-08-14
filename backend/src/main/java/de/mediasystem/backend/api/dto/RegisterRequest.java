package de.mediasystem.backend.api.dto;

// Diese Klasse gibt die benötigte Datenstruktur vor.
public record RegisterRequest(String username, String email, String password, String codeword) { }

