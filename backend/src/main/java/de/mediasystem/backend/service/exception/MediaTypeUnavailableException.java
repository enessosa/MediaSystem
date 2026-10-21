package de.mediasystem.backend.service.exception;

public class MediaTypeUnavailableException extends RuntimeException {
    public MediaTypeUnavailableException() {
        super("Die Suche für die gewählten Medientypen ist auf diesem Server deaktiviert "
                + "(kein API-Key konfiguriert).");
    }
}
