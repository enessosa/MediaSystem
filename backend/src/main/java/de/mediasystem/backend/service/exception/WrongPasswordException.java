package de.mediasystem.backend.service.exception;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException() {
        super("Das aktuelle Passwort ist falsch.");
    }
}
