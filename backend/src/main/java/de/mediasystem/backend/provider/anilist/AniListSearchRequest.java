package de.mediasystem.backend.provider.anilist;

public record AniListSearchRequest(String query, Variables variables) {

    public record Variables(String search) { }
}
