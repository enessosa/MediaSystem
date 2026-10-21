package de.mediasystem.backend.api.dto;

/**
 * Das aktuelle Signup-Codewort; null, wenn keins gesetzt ist (Registrierung offen).
 */
public record CodewordResponse(String codeword) { }
