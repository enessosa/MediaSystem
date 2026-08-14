package de.mediasystem.backend.service.exception;

public class CodewordNotConfiguredException extends RuntimeException {
    public CodewordNotConfiguredException(String message) {
        super(message);
    }
}
