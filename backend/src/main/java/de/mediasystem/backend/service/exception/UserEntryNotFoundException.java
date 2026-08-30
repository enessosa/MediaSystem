package de.mediasystem.backend.service.exception;

public class UserEntryNotFoundException extends RuntimeException {
    public UserEntryNotFoundException(String message) {
        super(message);
    }
}
