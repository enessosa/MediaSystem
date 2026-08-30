package de.mediasystem.backend.service.exception;

public class UserEntryNotFoundException extends RuntimeException {
    public UserEntryNotFoundException() {
        super("Eintrag nicht gefunden.");
    }
}
