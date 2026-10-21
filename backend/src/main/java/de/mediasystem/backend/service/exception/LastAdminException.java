package de.mediasystem.backend.service.exception;

public class LastAdminException extends RuntimeException {
    public LastAdminException() {
        super("Du bist der einzige Admin und kannst deinen Account deshalb nicht löschen.");
    }
}
