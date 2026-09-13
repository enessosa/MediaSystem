package de.mediasystem.backend.service.exception;

public class MediaNotFoundException extends RuntimeException {
    public MediaNotFoundException() {
        super("Medium nicht gefunden.");
    }
}
