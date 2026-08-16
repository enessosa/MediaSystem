package de.mediasystem.backend.provider;

public record AniListSearchRequest(String query, Variables variables) {

    public record Variables(String search) { }
}
