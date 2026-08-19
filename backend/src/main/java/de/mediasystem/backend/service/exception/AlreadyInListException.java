package de.mediasystem.backend.service.exception;

public class AlreadyInListException extends RuntimeException {
    public AlreadyInListException(String message) {
        super(message);
    }
}
